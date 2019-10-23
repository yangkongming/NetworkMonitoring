package com.okinc.core.profiler

import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import android.os.SystemClock
import android.support.annotation.UiThread
import android.util.Log
import www.pdx.life.networkmonitoring.BuildConfig
import www.pdx.life.networkmonitoring.profiler.PageInfo
import www.pdx.life.networkmonitoring.util.toSafeFloat
import www.pdx.life.networkmonitoring.util.toSafeInt

/**
 *@Date 2018/6/19

 *@Author fangzhenyu

 *@Description 计时类，用来测算两次计时的间隔时长
 */
object AppLaunchTimer {

    private val timeMap = HashMap<String, Long>()
    private val handler = Handler(Looper.getMainLooper())
    private val dataMap = HashMap<String, PageInfo>()

    @UiThread
    fun startTimer(tag: Any) {
        if (BuildConfig.DEBUG) {
            timeMap.put(getHashKey(tag), SystemClock.currentThreadTimeMillis())
        }
    }

    @UiThread
    fun stopTimerWhenIdle(tag: Any): Boolean {
        if (!BuildConfig.DEBUG) {
            return false
        }
        val hashKey = getHashKey(tag)
        val dataKey = getDataKey(tag)
        val startTime = timeMap.remove(hashKey)
        startTime?.let {
            handler.postAtFrontOfQueue {
                Looper.myQueue().addIdleHandler(object : MessageQueue.IdleHandler {
                    override fun queueIdle(): Boolean {
                        val costTime = SystemClock.currentThreadTimeMillis() - it

                        val list = dataMap.get(dataKey) ?: dataMap.let {
                            val pageInfo = PageInfo(dataKey)
                            it.put(dataKey, pageInfo)
                            pageInfo
                        }
                        list.add(costTime.toSafeInt())
                        val averageTime = averageTime(list)
                        Log.i("AppLaunchTimer", hashKey + " DisplayTime:" + costTime + "ms"
                                + "  AverageTime:" + String.format("%.2f", averageTime) + "ms" + "  count:" + list.size)
                        return false
                    }
                })
            }
        }
        return startTime != null
    }

    @UiThread
    fun removeTimer(tag: Any): Boolean {
        if (!BuildConfig.DEBUG) {
            return false
        }
        val hashKey = getHashKey(tag)
        return timeMap.remove(hashKey) != null
    }

    @UiThread
    fun clearAllTimer() {
        timeMap.clear()
    }

    private fun getHashKey(tag: Any): String {
        return StringBuffer(tag.javaClass.name).append("@").append(tag.hashCode()).toString()
    }


    private fun getDataKey(tag: Any): String {
        return tag.javaClass.name
    }


    private fun averageTime(dataList: ArrayList<Int>): Float {
        var sum = 0
        dataList.forEach {
            sum += it
        }
        return sum.toSafeFloat() / dataList.size
    }

    fun getStatisticsData(): List<PageInfo> {
        val pageInfoList = ArrayList<PageInfo>(dataMap.size)
        dataMap.values.forEach {
            pageInfoList.add(it)
        }
        return pageInfoList
    }

}