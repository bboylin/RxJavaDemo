package xyz.bboylin.minirxjava

import java.lang.Exception

/**
 * Created by denglin03 on 2019-07-18.
 */
interface Action {
    @Throws(Exception::class)
    fun run()
}