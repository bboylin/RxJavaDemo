package xyz.bboylin.minirxjava

/**
 * Created by denglin03 on 2019-07-18.
 */
interface Publisher<T> {
    fun subscribe(s: Subscriber<in T>)
}