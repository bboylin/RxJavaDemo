package xyz.bboylin.minirxjava

/**
 * Created by denglin03 on 2019-07-18.
 */
interface Subscription {
    fun request(n: Long)
    fun cancel()
}