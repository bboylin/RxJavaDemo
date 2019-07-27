package xyz.bboylin.minirxjava.internal.util

import xyz.bboylin.minirxjava.Subscription
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by denglin03 on 2019-07-19.
 */
enum class SubscriptionHelper : Subscription {
    CANCELLED;

    override fun request(n: Long) {
        // deliberately ignored
    }

    override fun cancel() {
        // deliberately ignored
    }

    companion object {
        @JvmStatic
        fun setOnce(field: AtomicReference<Subscription>, s: Subscription): Boolean {
            ObjectHelper.requireNonNull(s, "s is null")
            if (!field.compareAndSet(null, s)) {
                s.cancel()
                return false
            }
            return true
        }

        @JvmStatic
        fun validate(n: Long): Boolean {
            if (n <= 0) {
                RxJavaPlugins.uncaught(IllegalArgumentException("n is negative"))
                return false
            }
            return true
        }

        @JvmStatic
        fun cancel(field: AtomicReference<Subscription>): Boolean {
            var current: Subscription? = field.get()
            if (current !== CANCELLED) {
                current = field.getAndSet(CANCELLED)
                if (current !== CANCELLED) {
                    current?.cancel()
                    return true
                }
            }
            return false
        }

        /**
         * Verifies that current is null, next is not null, otherwise signals errors
         * to the RxJavaPlugins and returns false.
         * @param current the current Subscription, expected to be null
         * @param next the next Subscription, expected to be non-null
         * @return true if the validation succeeded
         */
        @JvmStatic
        fun validate(current: Subscription?, next: Subscription?): Boolean {
            if (next == null) {
                RxJavaPlugins.uncaught(NullPointerException("next is null"))
                return false
            }
            current?.let {
                next.cancel()
                return false
            }
            return true
        }
    }
}