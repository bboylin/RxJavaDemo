package xyz.bboylin.minirxjava.internal.operators

import xyz.bboylin.minirxjava.*
import xyz.bboylin.minirxjava.Function
import xyz.bboylin.minirxjava.internal.fuseable.AbstractFlowableWithUpstream
import xyz.bboylin.minirxjava.internal.util.ObjectHelper
import xyz.bboylin.minirxjava.internal.util.RxJavaPlugins
import xyz.bboylin.minirxjava.internal.util.SubscriptionHelper

/**
 * Created by denglin03 on 2019-07-22.
 */
class FlowableMap<T, U>(
    source: Flowable<T>,
    private val mapper: Function<in T, out U>
) : AbstractFlowableWithUpstream<T, U>(source) {
    override fun subscribeActual(s: Subscriber<in U>) {
        source.subscribe(MapSubscriber(s, mapper))
    }

    class MapSubscriber<T, U>(
        private val downstream: Subscriber<in U>,
        private val mapper: Function<in T, out U>
    ) : Subscriber<T>, Subscription {
        private var upstream: Subscription? = null
        private var done: Boolean = false
        override fun onSubscribe(s: Subscription) {
            if (SubscriptionHelper.validate(upstream, s)) {
                upstream = s
                downstream.onSubscribe(this)
            }
        }

        override fun onNext(t: T) {
            if (done) {
                return
            }
            val v: U

            try {
                v = ObjectHelper.requireNonNull(mapper.apply(t), "The mapper function returned a null value.")
            } catch (ex: Throwable) {
                fail(ex)
                return
            }

            downstream.onNext(v)
        }

        override fun onError(t: Throwable) {
            if (done) {
                RxJavaPlugins.uncaught(t)
                return
            }
            done = true
            downstream.onError(t)
        }

        override fun onComplete() {
            if (done) {
                return
            }
            done = true
            downstream.onComplete()
        }

        override fun request(n: Long) {
            upstream!!.request(n)
        }

        override fun cancel() {
            upstream!!.cancel()
        }

        /**
         * calls [onError].
         * @param t the throwable to signal to the actual subscriber
         */
        private fun fail(t: Throwable) {
            upstream!!.cancel()
            onError(t)
        }
    }
}
