package xyz.bboylin.minirxjava.internal.operators

import xyz.bboylin.minirxjava.*
import xyz.bboylin.minirxjava.internal.fuseable.AbstractFlowableWithUpstream
import xyz.bboylin.minirxjava.internal.util.RxJavaPlugins
import xyz.bboylin.minirxjava.internal.util.SubscriptionHelper

/**
 * Created by denglin03 on 2019-07-24.
 */
class FlowableFilter<T>(
    source: Flowable<T>,
    private val predicate: Predicate<in T>
) : AbstractFlowableWithUpstream<T, T>(source) {
    override fun subscribeActual(s: Subscriber<in T>) {
        source.subscribe(FilterSubscriber(s, predicate))
    }
}

class FilterSubscriber<T>(
    private val downstream: Subscriber<in T>,
    private val filter: Predicate<in T>
) : Subscriber<T>, Subscription {
    private var done: Boolean = false
    private var upstream: Subscription? = null
    override fun onNext(t: T) {
        if (done) {
            return
        }
        val b: Boolean
        try {
            b = filter.test(t)
        } catch (e: Throwable) {
            fail(e)
            return
        }

        if (b) {
            downstream.onNext(t)
        }
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

    override fun onSubscribe(s: Subscription) {
        if (SubscriptionHelper.validate(upstream, s)) {
            upstream = s
            downstream.onSubscribe(this)
        }
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
