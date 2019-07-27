package xyz.bboylin.minirxjava.internal.util

import java.lang.NullPointerException

/**
 * Created by denglin03 on 2019-07-18.
 */
class ObjectHelper {
    companion object {
        @JvmStatic
        fun <T> requireNonNull(item: T, msg: String): T {
            item?.let {
                return item
            }
            throw NullPointerException(msg)
        }
    }
}