package xyz.bboylin.minirxjava

import xyz.bboylin.minirxjava.internal.operators.*
import xyz.bboylin.minirxjava.internal.schedulers.Scheduler
import xyz.bboylin.minirxjava.internal.util.ObjectHelper

/**
 * Created by denglin03 on 2019-07-18.
 */
abstract class Flowable<T> : Publisher<T> {

    fun <R> map(mapper: Function<in T, out R>): Flowable<R> {
        ObjectHelper.requireNonNull(mapper, "mapper is null")
        return FlowableMap(this, mapper)
    }

    fun filter(predicate: Predicate<in T>): Flowable<T> {
        ObjectHelper.requireNonNull(predicate, "predicate is null")
        return FlowableFilter<T>(this, predicate)
    }

    fun <R> flatMap(
        mapper: Function<in T, out Publisher<out R>>
    ): Flowable<R> {
        ObjectHelper.requireNonNull(mapper, "mapper is null")
        return FlowableFlatMap(this, mapper)
    }

    fun subscribeOn(scheduler: Scheduler): Flowable<T> = FlowableSubscribeOn(this, scheduler)

    fun observeOn(scheduler: Scheduler): Flowable<T> = FlowableObserveOn(this, scheduler)

    fun subscribe(): Disposable = subscribe(
        Functions.emptyConsumer(),
        Functions.ON_ERROR_MISSING_CONSUMER,
        Functions.EMPTY_ACTION,
        FlowableInternalHelper.REQUEST_MAX
    )

    fun subscribe(onNext: Consumer<in T>): Disposable = subscribe(
        onNext,
        Functions.ON_ERROR_MISSING_CONSUMER,
        Functions.EMPTY_ACTION,
        FlowableInternalHelper.REQUEST_MAX
    )

    fun subscribe(onNext: Consumer<in T>, onError: Consumer<in Throwable>): Disposable =
        subscribe(onNext, onError, Functions.EMPTY_ACTION, FlowableInternalHelper.REQUEST_MAX)

    fun subscribe(
        onNext: Consumer<in T>,
        onError: Consumer<in Throwable>,
        onComplete: Action
    ): Disposable = subscribe(onNext, onError, onComplete, FlowableInternalHelper.REQUEST_MAX)

    fun subscribe(
        onNext: Consumer<in T>,
        onError: Consumer<in Throwable>,
        onComplete: Action,
        onSubscribe: Consumer<in Subscription>
    ): Disposable {
        ObjectHelper.requireNonNull(onNext, "onNext is null")
        ObjectHelper.requireNonNull(onError, "onError is null")
        ObjectHelper.requireNonNull(onComplete, "onComplete is null")
        ObjectHelper.requireNonNull(onSubscribe, "onSubscribe is null")
        val ls = LambdaSubscriber(onNext, onError, onComplete, onSubscribe)
        subscribe(ls)
        return ls
    }

    override fun subscribe(s: Subscriber<in T>) {
        ObjectHelper.requireNonNull(s, "s is null")
        subscribeActual(s)
        // TODO catch exception??
    }

    abstract fun subscribeActual(s: Subscriber<in T>)

    companion object {
        @JvmStatic
        fun <T> just(item: T): Flowable<T> = FlowableJust(item)

        @JvmStatic
        fun range(start: Int, count: Int): Flowable<Int> {
            when {
                count < 1 -> throw IllegalArgumentException("count >= 0 required but it was $count")
                count == 1 -> return just(start)
                start.toLong() + (count - 1) > Integer.MAX_VALUE -> throw IllegalArgumentException("Integer overflow")
                else -> return FlowableRange(start, count)
            }
        }
    }
}