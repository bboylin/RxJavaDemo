package xyz.bboylin.minirxjava.internal.operators

import xyz.bboylin.minirxjava.Flowable
import xyz.bboylin.minirxjava.ScalarSubscription
import xyz.bboylin.minirxjava.Subscriber

/**
 * Created by denglin03 on 2019-07-18.
 */
class FlowableJust<T>(private val value: T) : Flowable<T>() {
    override fun subscribeActual(s: Subscriber<in T>) {
        s.onSubscribe(ScalarSubscription(s, value))
    }
}