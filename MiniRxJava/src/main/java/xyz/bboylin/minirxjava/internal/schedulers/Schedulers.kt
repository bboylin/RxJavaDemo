package xyz.bboylin.minirxjava.internal.schedulers

/**
 * Created by denglin03 on 2019-07-27.
 */
class Schedulers private constructor() {
    companion object {
        @JvmField
        internal val IO = IOScheduler()

        @JvmStatic
        fun io(): Scheduler = IO
    }
}