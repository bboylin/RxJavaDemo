package xyz.bboylin.minirxjava.internal.operators

import xyz.bboylin.minirxjava.Flowable
import xyz.bboylin.minirxjava.Subscriber
import xyz.bboylin.minirxjava.Subscription
import xyz.bboylin.minirxjava.internal.util.SubscriptionHelper

/**
 * Created by denglin03 on 2019-07-22.
 */
class FlowableRange(
    private val start: Int,
    count: Int
) : Flowable<Int>() {
    private val end = start + count
    override fun subscribeActual(s: Subscriber<in Int>) {
        s.onSubscribe(RangeSubscription(s, start, end))
    }

    class RangeSubscription(
        private val downstream: Subscriber<in Int>,
        private val index: Int,
        private val end: Int
    ) : Subscription {
        @Volatile
        var cancelled: Boolean = false

        override fun request(n: Long) {
            if (SubscriptionHelper.validate(n)) {
                fastPath()
            }
        }

        /**
         * no backpressure
         */
        private fun fastPath() {
            for (i in index until end) {
                if (cancelled) {
                    return
                }
                downstream.onNext(i)
            }
            if (cancelled) {
                return
            }
            downstream.onComplete()
        }

        override fun cancel() {
            cancelled = true
        }
    }
}