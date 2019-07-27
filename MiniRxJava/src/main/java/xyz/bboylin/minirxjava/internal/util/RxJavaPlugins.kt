package xyz.bboylin.minirxjava.internal.util

/**
 * Created by denglin03 on 2019-07-18.
 */
class RxJavaPlugins {
    companion object {
        @JvmStatic
        fun uncaught(error: Throwable) {
            error.printStackTrace()
            val currentThread = Thread.currentThread()
            val handler = currentThread.uncaughtExceptionHandler
            handler.uncaughtException(currentThread, error)
        }
    }
}