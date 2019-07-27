package xyz.bboylin.minirxjava.internal.operators

import xyz.bboylin.minirxjava.Flowable
import xyz.bboylin.minirxjava.Subscriber
import xyz.bboylin.minirxjava.Subscription
import xyz.bboylin.minirxjava.internal.fuseable.AbstractFlowableWithUpstream
import xyz.bboylin.minirxjava.internal.schedulers.Scheduler
import xyz.bboylin.minirxjava.internal.util.SubscriptionHelper
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by denglin03 on 2019-07-27.
 */
class FlowableObserveOn<T>(
    source: Flowable<T>,
    private val scheduler: Scheduler
) : AbstractFlowableWithUpstream<T, T>(source) {
    override fun subscribeActual(s: Subscriber<in T>) {
        val worker = scheduler.createWorker()
        source.subscribe(ObserveOnSubscriber(s, worker))
    }

    class ObserveOnSubscriber<T>(
        private val downstream: Subscriber<in T>,
        private val worker: Scheduler.Worker
    ) : Subscriber<T>, Subscription {
        private val upstream: AtomicReference<Subscription> = AtomicReference()
        private val requested: AtomicLong = AtomicLong(128)

        override fun onSubscribe(s: Subscription) {
            if (SubscriptionHelper.setOnce(upstream, s)) {
                val r = requested.getAndSet(0L)
                if (r != 0L) {
                    upstream.get().request(r)
                }
            }
        }

        override fun onNext(t: T) {
            worker.schedule(Runnable {
                downstream.onNext(t)
            })
        }

        override fun onError(t: Throwable) {
            worker.schedule(Runnable {
                downstream.onError(t)
            })
        }

        override fun onComplete() {
            worker.schedule(Runnable {
                downstream.onComplete()
            })
        }

        override fun request(n: Long) {
            upstream.get().request(n)
        }

        override fun cancel() {
            SubscriptionHelper.cancel(upstream)
        }

    }
}