package xyz.bboylin.rxjavademo.ext

import android.os.Handler
import android.os.Message
import xyz.bboylin.minirxjava.internal.schedulers.Scheduler
import java.util.concurrent.TimeUnit

/**
 * Created by denglin03 on 2019-07-27.
 */
class HandlerScheduler(private val handler: Handler) : Scheduler() {
    override fun createWorker(): Worker = HandlerWorker(handler)

    class HandlerWorker(private val handler: Handler) : Worker() {
        override fun schedule(run: Runnable, delay: Long, unit: TimeUnit) {
            val message = Message.obtain(handler, run)
            handler.sendMessageDelayed(message, unit.toMillis(delay))
        }
    }
}