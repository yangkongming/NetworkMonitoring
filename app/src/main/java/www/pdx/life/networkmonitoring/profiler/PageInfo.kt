package www.pdx.life.networkmonitoring.profiler

import www.pdx.life.networkmonitoring.util.toSafeFloat


/**
 *@Date 2018/6/22

 *@Author fangzhenyu

 *@Description 页面渲染耗时相关信息
 */
data class PageInfo(var name: String) : ArrayList<Int>(), Cloneable, Comparable<PageInfo> {

    private var averageTime: Float = 0f

    public fun getSimpleName(): String {
        return name.substring(name.lastIndexOf("."), name.length)
    }


    override fun add(element: Int): Boolean {
        val result = super.add(element)
        averageTime = averageTime()
        return result
    }

    override fun add(index: Int, element: Int) {
        super.add(index, element)
        averageTime = averageTime()
    }

    override fun addAll(elements: Collection<Int>): Boolean {
        val result = super.addAll(elements)
        averageTime = averageTime()
        return result
    }

    override fun addAll(index: Int, elements: Collection<Int>): Boolean {
        val result = super.addAll(index, elements)
        averageTime = averageTime()
        return result
    }

    override fun clone(): Any {
        val pageInfo: PageInfo = super<ArrayList>.clone() as PageInfo
        pageInfo.name = this.name
        return pageInfo
    }


    private fun averageTime(): Float {
        var sum = 0
        this.forEach {
            sum += it
        }
        return sum.toSafeFloat() / size
    }

    fun getAverageTime(): Float {
        return averageTime
    }


    override fun compareTo(other: PageInfo): Int {
        if (this.getAverageTime() == other.getAverageTime()) {
            return 0
        }
        val result = this.getAverageTime() - other.getAverageTime()
        return if (result > 0) -1 else 1
    }

}