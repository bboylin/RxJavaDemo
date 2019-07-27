package xyz.bboylin.minirxjava.internal.fuseable

import xyz.bboylin.minirxjava.Publisher

/**
 * Created by denglin03 on 2019-07-22.
 */
interface HasUpstreamPublisher<T> {
    fun source(): Publisher<T>
}