package xyz.bboylin.minirxjava.internal.schedulers

import xyz.bboylin.minirxjava.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by denglin03 on 2019-07-27.
 */
abstract class Scheduler {
    abstract fun createWorker(): Worker
    abstract class Worker {
        /**
         * Schedules a Runnable for execution without any time delay.
         *
         *
         * The default implementation delegates to [.schedule].
         *
         * @param run
         * Runnable to schedule
         * @return a Disposable to be able to unsubscribe the action (cancel it if not executed)
         */
        fun schedule(run: Runnable) {
            schedule(run, 0L, TimeUnit.NANOSECONDS)
        }

        /**
         * Schedules an Runnable for execution at some point in the future specified by a time delay
         * relative to the current time.
         *
         *
         * Note to implementors: non-positive `delayTime` should be regarded as non-delayed schedule, i.e.,
         * as if the [.schedule] was called.
         *
         * @param run
         * the Runnable to schedule
         * @param delay
         * time to "wait" before executing the action; non-positive values indicate an non-delayed
         * schedule
         * @param unit
         * the time unit of `delayTime`
         * @return a Disposable to be able to unsubscribe the action (cancel it if not executed)
         */
        abstract fun schedule(run: Runnable, delay: Long, unit: TimeUnit)
    }
}