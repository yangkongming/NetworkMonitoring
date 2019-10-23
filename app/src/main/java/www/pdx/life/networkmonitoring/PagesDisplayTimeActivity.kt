package www.pdx.life.networkmonitoring

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.okinc.core.profiler.AppLaunchTimer
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import www.pdx.life.networkmonitoring.databinding.ActivityPagesDisplayTimeBinding
import www.pdx.life.networkmonitoring.databinding.ItemActivityPageInfoBinding
import www.pdx.life.networkmonitoring.multipe.databinding.BindingHolder
import www.pdx.life.networkmonitoring.multipe.databinding.ItemViewBindingTemplate
import www.pdx.life.networkmonitoring.multipe.databinding.MultiTypeAdapterEx
import www.pdx.life.networkmonitoring.profiler.PageInfo
import java.util.*
import kotlin.Comparator

class PagesDisplayTimeActivity : AppCompatActivity() {


    private lateinit var mBinding: ActivityPagesDisplayTimeBinding
    private lateinit var multiTypeAdapter: MultiTypeAdapterEx

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pages_display_time)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
        }
        mBinding.rvPages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider))
        mBinding.rvPages.addItemDecoration(dividerItemDecoration)
        mBinding.tvAverageTime.setOnClickListener {
            loadData(AverageTimeCompator())
        }
        mBinding.tvCount.setOnClickListener {
            loadData(CountCompator())
        }
        loadData(AverageTimeCompator())
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        loadData(AverageTimeCompator())
    }

    private fun loadData(comparator: Comparator<PageInfo>) {
        Observable.just(AppLaunchTimer.getStatisticsData())
                .map { t ->
                    Collections.sort(t, comparator)
                    t
                }.subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    apply {
                        setupData(it)
                    }
                }, {
                    apply {

                    }
                })
    }

    private fun setupData(data: List<PageInfo>) {
        if (mBinding.rvPages.adapter == null) {
            multiTypeAdapter = MultiTypeAdapterEx()
            multiTypeAdapter.register(PageInfo::class.java, PageDisplayTimeItemTemplate())
            multiTypeAdapter.items = data
            mBinding.rvPages.adapter = multiTypeAdapter
        } else {
            mBinding.rvPages.smoothScrollToPosition(0)
            multiTypeAdapter.reload(data)
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.debug_pages_display_time_options, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.menu_item_info -> {
                showInfoDialog()
                return true
            }
            R.id.menu_item_notification_entry -> {
                if (item.isChecked) {
                    cancelNotificationEntry()
                } else {
                    showNotificationEntry()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showNotificationEntry() {
        val intent = Intent(this, PagesDisplayTimeActivity::class.java)
        val notification = NotificationCompat.Builder(this, "PagesDisplayTime")
                .setContentTitle("Pages Display Time Entrance")
                .setContentText("点击可直接查看界面渲染耗时")
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_info)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            var notificationChannel = notificationManager.getNotificationChannel("PagesDisplayTime")
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel("PagesDisplayTime",
                        "PagesDisplayTime",
                        NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
        NotificationManagerCompat.from(this).notify(1024, notification)
    }


    private fun cancelNotificationEntry() {
        NotificationManagerCompat.from(this).cancel(1024)
    }


    private fun showInfoDialog(): AlertDialog {
        val alertDialog = AlertDialog.Builder(this)
                .setTitle("说明")
                .setMessage("本界面统计所有打开过的Activity/Fragment渲染平均耗时。\n\n" +
                        "1.同一界面打开次数越多统计越精确，建议开/关界面10次+测试\n" +
                        "2.横屏可查看完整界面名，长按后可复制\n" +
                        "3.点击'平均耗时'按照耗时长短降序排列(默认)，点击'次数'按照次数多少降序排列'\n" +
                        "4.页面渲染耗时分级{\n" +
                        "   红色>1000ms，急需优化\n" +
                        "   橙色>500ms，需要优化\n" +
                        "   蓝色>200ms，可以优化\n" +
                        "   绿色<200ms，表现很好}")
                .setPositiveButton("知道了") { dialog, which ->
                    dialog.dismiss()
                }.create()
        alertDialog.show()
        return alertDialog
    }


    inner class AverageTimeCompator : Comparator<PageInfo> {
        override fun compare(o1: PageInfo?, o2: PageInfo?): Int {
            if (o1 == null || o2 == null) {
                return 0
            }
            if (o1.getAverageTime() == o2.getAverageTime()) {
                return 0
            }
            val result = o1.getAverageTime() - o2.getAverageTime()
            return if (result > 0) -1 else 1
        }

    }

    inner class CountCompator : Comparator<PageInfo> {
        override fun compare(o1: PageInfo?, o2: PageInfo?): Int {
            if (o1 == null || o2 == null) {
                return 0
            }
            if (o1.size == o2.size) {
                return 0
            }
            val result = o1.size - o2.size
            return if (result > 0) -1 else 1
        }
    }


    inner class PageDisplayTimeItemTemplate : ItemViewBindingTemplate<PageInfo, ItemActivityPageInfoBinding>() {

        override fun getVariableId(): Int {
            return BR.pageInfo
        }

        override fun getItemLayoutId(): Int {
            return R.layout.item_activity_page_info
        }

        override fun onBindViewHolder(holder: BindingHolder<ItemActivityPageInfoBinding>, item: PageInfo) {
            super.onBindViewHolder(holder, item)
            holder.viewDataBinding.tvAverageTime.setTextColor(getTextColorByTime(item.getAverageTime()))
            holder.viewDataBinding.tvName.setOnLongClickListener {
                (it as TextView).setTextIsSelectable(true)
                true
            }
        }

        private fun getTextColorByTime(time: Float): Int {
            return when (time) {
                in Float.MIN_VALUE..0f -> Color.BLACK
                in 0..200 -> Color.parseColor("#00C853")
                in 200..500 -> Color.parseColor("#2962FF")
                in 500..1000 -> Color.parseColor("#FF6D00")
                else -> Color.parseColor("#D50000")
            }
        }
    }

}
