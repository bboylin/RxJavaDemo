package xyz.bboylin.minirxjava

import xyz.bboylin.minirxjava.internal.util.RxJavaPlugins

/**
 * Created by denglin03 on 2019-07-18.
 */
class Functions {
    companion object {
        @JvmField
        val EMPTY_CONSUMER = EmptyConsumer()
        @JvmField
        val ON_ERROR_MISSING_CONSUMER = OnErrorMissingConsumer()
        @JvmField
        val EMPTY_ACTION = EmptyAction()

        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <T> emptyConsumer(): Consumer<T> = EMPTY_CONSUMER as Consumer<T>
    }
}

class EmptyAction : Action {
    override fun run() {

    }

}

class OnErrorMissingConsumer : Consumer<Throwable> {
    override fun accept(t: Throwable) {
        RxJavaPlugins.uncaught(t)
    }
}

class EmptyConsumer : Consumer<Any> {
    override fun accept(t: Any) {

    }
}