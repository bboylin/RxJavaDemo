package xyz.bboylin.minirxjava

/**
 * Created by denglin03 on 2019-07-18.
 */
interface Consumer<T> {
    @Throws(Exception::class)
    fun accept(t: T)
}