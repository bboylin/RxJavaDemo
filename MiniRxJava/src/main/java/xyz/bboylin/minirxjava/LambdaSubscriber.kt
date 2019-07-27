package xyz.bboylin.minirxjava

import xyz.bboylin.minirxjava.internal.util.RxJavaPlugins
import xyz.bboylin.minirxjava.internal.util.SubscriptionHelper
import java.lang.Exception
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by denglin03 on 2019-07-18.
 */
class LambdaSubscriber<T>(
    private val onNext: Consumer<in T>,
    private val onError: Consumer<in Throwable>,
    private val onComplete: Action,
    private val onSubscribe: Consumer<in Subscription>
) : AtomicReference<Subscription>(), Subscriber<T>, Subscription, Disposable {
    override fun onSubscribe(s: Subscription) {
        if (SubscriptionHelper.setOnce(this, s)) {
            try {
                onSubscribe.accept(this)
            } catch (ex: Exception) {
                s.cancel()
                onError(ex)
            }
        }
    }

    override fun onNext(t: T) {
        if (!isDisposed()) {
            try {
                onNext.accept(t)
            } catch (e: Throwable) {
                get().cancel()
                onError(e)
            }

        }
    }

    override fun onError(t: Throwable) {
        if (get() !== SubscriptionHelper.CANCELLED) {
            lazySet(SubscriptionHelper.CANCELLED)
            try {
                onError.accept(t)
            } catch (e: Throwable) {
                RxJavaPlugins.uncaught(e)
            }

        } else {
            RxJavaPlugins.uncaught(t)
        }
    }

    override fun onComplete() {
        if (get() !== SubscriptionHelper.CANCELLED) {
            lazySet(SubscriptionHelper.CANCELLED)
            try {
                onComplete.run()
            } catch (e: Throwable) {
                RxJavaPlugins.uncaught(e)
            }
        }
    }

    override fun request(n: Long) {
        get().request(n)
    }

    override fun cancel() {
        SubscriptionHelper.cancel(this)
    }

    override fun dispose() {
        cancel()
    }

    override fun isDisposed(): Boolean = get() === SubscriptionHelper.CANCELLED
}