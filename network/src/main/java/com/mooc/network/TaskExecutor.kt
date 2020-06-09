package com.mooc.network

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

object TaskExecutor {
    private val mDiskIO: ExecutorService
    private val lock = Any()

    @Volatile
    private var mHandler: Handler? = null

    fun executeOnDiskIO(runnable: () -> Unit) {
        mDiskIO.execute(runnable)
    }

    fun postToMain(runnable: () -> Unit) {
        if (mHandler == null) {
            synchronized(lock) {
                if (mHandler == null) {
                    mHandler = Handler(Looper.getMainLooper())
                }
            }
        }
        mHandler?.post(runnable)
    }

    val isMainThread: Boolean
        get() = Looper.getMainLooper().thread === Thread.currentThread()


    init {
        val threadFactory: ThreadFactory = object : ThreadFactory {

            private val THREAD_NAME_STEM = "task_disk_io_%d"
            private val mThreadId = AtomicInteger(0)

            @SuppressLint("DefaultLocale")
            override fun newThread(r: Runnable): Thread {
                val t = Thread(r)
                t.name = String.format(THREAD_NAME_STEM, mThreadId.getAndIncrement())
                return t
            }
        }
        mDiskIO = Executors.newFixedThreadPool(4, threadFactory)
    }
}