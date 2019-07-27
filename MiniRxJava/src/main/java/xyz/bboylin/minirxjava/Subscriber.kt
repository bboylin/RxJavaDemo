package xyz.bboylin.minirxjava

/**
 * Created by denglin03 on 2019-07-18.
 */
interface Subscriber<T> {
    fun onSubscribe(s: Subscription)
    fun onNext(t: T)
    fun onError(t: Throwable)
    fun onComplete()
}