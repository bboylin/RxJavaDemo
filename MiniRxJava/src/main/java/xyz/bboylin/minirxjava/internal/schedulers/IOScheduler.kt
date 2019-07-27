package xyz.bboylin.minirxjava.internal.schedulers

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Created by denglin03 on 2019-07-27.
 */
class IOScheduler : Scheduler() {
    override fun createWorker(): Worker = EventLoopWorker()

    class EventLoopWorker : Worker() {
        private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        override fun schedule(run: Runnable, delay: Long, unit: TimeUnit) {
            if (delay <= 0) {
                executor.submit(run)
            } else {
                executor.schedule(run, delay, unit)
            }
        }
    }
}