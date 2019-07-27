package xyz.bboylin.rxjavademo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import xyz.bboylin.minirxjava.*
import xyz.bboylin.minirxjava.Function
import xyz.bboylin.minirxjava.internal.schedulers.Schedulers
import xyz.bboylin.rxjavademo.ext.AndroidSchedulers
import java.lang.RuntimeException

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Flowable.just("123")
            .map(object : Function<String, Int> {
                override fun apply(t: String): Int {
                    Log.d(TAG, "just $t")
                    return t.toInt() / 3
                }
            })
            .subscribe(object : Consumer<Int> {
                override fun accept(t: Int) {
                    Log.d(TAG, "onNext $t")
                    throw RuntimeException()
                }
            }, object : Consumer<Throwable> {
                override fun accept(t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "on error")
                }
            }, object : Action {
                override fun run() {
                    Log.d(TAG, "on complete")
                }
            })
        findViewById<TextView>(R.id.tv).setOnClickListener {
            Flowable.range(1, 10)
                .filter(object : Predicate<Int> {
                    override fun test(t: Int): Boolean = t % 2 == 0
                })
                .subscribeOn(Schedulers.io())
                .map(object : Function<Int, Int> {
                    override fun apply(t: Int): Int {
                        Log.d(TAG, "range $t,thread:${Thread.currentThread()}")
                        return t * t
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(object : Function<Int, Publisher<Int>> {
                    override fun apply(t: Int): Publisher<Int> {
                        Log.d(TAG, "flatmap $t,thread:${Thread.currentThread()}")
                        return Flowable.just(t + 1)
                    }
                })
                .subscribe(object : Consumer<Int> {
                    override fun accept(t: Int) {
                        Log.d(TAG, "onNext $t,thread:${Thread.currentThread()}")
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable) {
                        t.printStackTrace()
                        Log.d(TAG, "on error")
                    }
                }, object : Action {
                    override fun run() {
                        Log.d(TAG, "on complete")
                    }
                })
        }
    }
}
