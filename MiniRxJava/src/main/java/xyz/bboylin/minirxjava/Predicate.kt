package xyz.bboylin.minirxjava

/**
 * Created by denglin03 on 2019-07-24.
 */
interface Predicate<T> {
    @Throws(Exception::class)
    fun test(t: T): Boolean
}