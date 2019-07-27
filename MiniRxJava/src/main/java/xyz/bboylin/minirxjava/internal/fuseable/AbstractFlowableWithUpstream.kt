package xyz.bboylin.minirxjava.internal.fuseable

import xyz.bboylin.minirxjava.Flowable
import xyz.bboylin.minirxjava.internal.util.ObjectHelper
import xyz.bboylin.minirxjava.Publisher

/**
 * Created by denglin03 on 2019-07-22.
 */
abstract class AbstractFlowableWithUpstream<T, R>(source: Flowable<T>) : Flowable<R>(),
    HasUpstreamPublisher<T> {
    protected val source: Flowable<T> =
        ObjectHelper.requireNonNull(source, "source is null")
    final override fun source(): Publisher<T> = source
}