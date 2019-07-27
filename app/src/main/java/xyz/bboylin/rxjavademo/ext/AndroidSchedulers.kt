package xyz.bboylin.rxjavademo.ext

import android.os.Handler
import android.os.Looper
import xyz.bboylin.minirxjava.internal.schedulers.Scheduler

/**
 * Created by denglin03 on 2019-07-27.
 */
class AndroidSchedulers private constructor() {
    companion object {
        private val MAIN_THREAD = HandlerScheduler(Handler(Looper.getMainLooper()))

        @JvmStatic
        fun mainThread(): Scheduler = MAIN_THREAD
    }
}