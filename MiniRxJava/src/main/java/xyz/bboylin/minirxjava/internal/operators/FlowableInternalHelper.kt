package xyz.bboylin.minirxjava.internal.operators

import xyz.bboylin.minirxjava.Consumer
import xyz.bboylin.minirxjava.Subscription

/**
 * Created by denglin03 on 2019-07-18.
 */
class FlowableInternalHelper {
    companion object {
        @JvmField
        val REQUEST_MAX = object : Consumer<Subscription> {
            override fun accept(t: Subscription) {
                t.request(Long.MAX_VALUE)
            }
        }
    }
}