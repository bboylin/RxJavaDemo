package xyz.bboylin.minirxjava.internal.operators

import xyz.bboylin.minirxjava.Flowable
import xyz.bboylin.minirxjava.Publisher
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
class FlowableSubscribeOn<T>(
    source: Flowable<T>,
    private val scheduler: Scheduler
) : AbstractFlowableWithUpstream<T, T>(source) {
    override fun subscribeActual(s: Subscriber<in T>) {
        val w = scheduler.createWorker()
        val sos = SubscribeOnSubscriber(s, w, source)
        source.subscribe(sos)
    }

    class SubscribeOnSubscriber<T>(
        private val downstream: Subscriber<in T>,
        private val worker: Scheduler.Worker,
        private var source: Publisher<T>?
    ) : AtomicReference<Thread>(), Subscriber<T>, Subscription, Runnable {
        private val upstream: AtomicReference<Subscription> = AtomicReference()
        private val requested: AtomicLong = AtomicLong(3)

        override fun onSubscribe(s: Subscription) {
            worker.schedule(this)
            if (SubscriptionHelper.setOnce(upstream, s)) {
                val r = requested.getAndSet(0L)
                if (r != 0L) {
                    requestUpstream(r, s)
                }
            }
        }

        override fun onNext(t: T) {
            downstream.onNext(t)
        }

        override fun onError(t: Throwable) {
            downstream.onError(t)
        }

        override fun onComplete() {
            downstream.onComplete()
        }

        override fun request(n: Long) {
            if (SubscriptionHelper.validate(n)) {
                requestUpstream(n, upstream.get())
            }
        }

        private fun requestUpstream(n: Long, s: Subscription) {
            if (Thread.currentThread() === get()) {
                s.request(n)
            } else {
                worker.schedule(Request(s, n))
            }
        }

        override fun cancel() {
            SubscriptionHelper.cancel(upstream)
        }

        override fun run() {
            lazySet(Thread.currentThread())
            source!!.subscribe(this)
        }

        internal class Request(
            private val upstream: Subscription,
            private val n: Long
        ) : Runnable {
            override fun run() {
                upstream.request(n)
            }
        }
    }
}