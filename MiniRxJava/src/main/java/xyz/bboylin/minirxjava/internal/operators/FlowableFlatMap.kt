package xyz.bboylin.minirxjava.internal.operators

import xyz.bboylin.minirxjava.*
import xyz.bboylin.minirxjava.Function
import xyz.bboylin.minirxjava.internal.fuseable.AbstractFlowableWithUpstream
import xyz.bboylin.minirxjava.internal.util.ObjectHelper
import xyz.bboylin.minirxjava.internal.util.RxJavaPlugins
import xyz.bboylin.minirxjava.internal.util.SubscriptionHelper
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by denglin03 on 2019-07-24.
 */
class FlowableFlatMap<T, R>(
    source: Flowable<T>,
    private val mapper: Function<in T, out Publisher<out R>>
) : AbstractFlowableWithUpstream<T, R>(source) {
    override fun subscribeActual(s: Subscriber<in R>) {
        source.subscribe(MergeSubscriber(s, mapper))
    }
}

class MergeSubscriber<T, U>(
    private val downstream: Subscriber<in U>,
    private val mapper: Function<in T, out Publisher<out U>>
) : Subscription, Subscriber<T> {
    private var upstream: Subscription? = null
    @Volatile
    private var done: Boolean = false
    @Volatile
    private var cancelled: Boolean = false
    public var bufferSize: Long = 128

    override fun request(n: Long) {
        upstream!!.request(n)
    }

    override fun cancel() {
        if (!cancelled) {
            cancelled = true
            upstream!!.cancel()
            // TODO dispose inner subscriber
        }
    }

    override fun onSubscribe(s: Subscription) {
        if (SubscriptionHelper.validate(this.upstream, s)) {
            this.upstream = s
            downstream.onSubscribe(this)
            if (!cancelled) {
                s.request(Long.MAX_VALUE)
            }
        }
    }

    override fun onNext(t: T) {
        if (done) {
            return
        }
        val p: Publisher<out U>
        try {
            p = ObjectHelper.requireNonNull(mapper.apply(t), "The mapper returned a null Publisher")
        } catch (e: Throwable) {
            upstream!!.cancel()
            onError(e)
            return
        }
        p.subscribe(InnerSubscriber(this))
    }

    private fun tryEmit(t: U) {
        downstream.onNext(t)
    }

    override fun onError(t: Throwable) {
        done = true
        downstream.onError(t)
    }

    override fun onComplete() {
        done = true
        downstream.onComplete()
    }

    class InnerSubscriber<T, U>(
        private val parent: MergeSubscriber<T, U>
    ) : AtomicReference<Subscription>(), Subscriber<U> {
        private val bufferSize = parent.bufferSize
        override fun onSubscribe(s: Subscription) {
            if (SubscriptionHelper.setOnce(this, s)) {
                s.request(bufferSize)
            }
        }

        override fun onNext(t: U) {
            parent.tryEmit(t)
        }

        override fun onError(t: Throwable) {
            lazySet(SubscriptionHelper.CANCELLED)
            parent.innerError(t)
        }

        override fun onComplete() {
            // TODO how to handle complete , should not simply call onComplete as it has not completed at all
        }

    }

    private fun innerError(t: Throwable) {
        done = true
        RxJavaPlugins.uncaught(t)
    }
}
