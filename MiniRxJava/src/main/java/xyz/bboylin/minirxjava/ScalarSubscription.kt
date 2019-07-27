package xyz.bboylin.minirxjava

import xyz.bboylin.minirxjava.internal.util.SubscriptionHelper
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by denglin03 on 2019-07-18.
 */
class ScalarSubscription<T>(
    private val subscriber: Subscriber<in T>,
    private val value: T
) : AtomicInteger(), Subscription {
    override fun toByte(): Byte = Byte.MAX_VALUE

    override fun toChar(): Char = Char.MAX_HIGH_SURROGATE

    override fun toShort(): Short = Short.MAX_VALUE

    override fun request(n: Long) {
        if (!SubscriptionHelper.validate(n)) {
            return
        }
        if (compareAndSet(NO_REQUEST, REQUESTED)) {
            val s = subscriber

            s.onNext(value)
            if (get() != CANCELLED) {
                s.onComplete()
            }
        }
    }

    override fun cancel() {
        lazySet(CANCELLED)
    }

    companion object {
        /** No request has been issued yet.  */
        internal const val NO_REQUEST = 0
        /** Request has been called. */
        internal const val REQUESTED = 1
        /** Cancel has been called.  */
        internal const val CANCELLED = 2
    }
}