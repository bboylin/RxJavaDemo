package xyz.bboylin.minirxjava

/**
 * Created by denglin03 on 2019-07-22.
 */
interface Function<T, R> {
    @Throws(Exception::class)
    fun apply(t: T): R
}