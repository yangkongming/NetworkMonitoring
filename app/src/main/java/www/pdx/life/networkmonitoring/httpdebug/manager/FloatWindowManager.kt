package www.pdx.life.networkmonitoring.httpdebug.manager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import www.pdx.life.networkmonitoring.R
import www.pdx.life.networkmonitoring.app.MyApplication
import www.pdx.life.networkmonitoring.httpdebug.bean.HttpBean
import www.pdx.life.networkmonitoring.httpdebug.floatwindow.*
import java.lang.Exception

class FloatWindowManager private constructor() {
    private val TAG = "FloatWindow"
    private var mFloatLayout: ConstraintLayout
    private var recyclerView: RecyclerView
    private val applicationContext = MyApplication.context
    private var adapter: ActivityAdapter = ActivityAdapter(applicationContext)
    private val MAX_RESPONSE_COUNT = 200
    private val mHandler = Handler(Looper.getMainLooper())

    companion object {
        private var instance: FloatWindowManager? = null
        val TAG_ICON = "icon"
        val TAG_DETAIL = "detail"
        @Synchronized
        fun get(): FloatWindowManager {
            if (instance == null) {
                instance = FloatWindowManager()
            }
            return instance!!
        }
    }

    init {
        val inflater = LayoutInflater.from(applicationContext)
        // 获取浮动窗口视图所在布局
        mFloatLayout = inflater.inflate(R.layout.float_window, null) as ConstraintLayout
        recyclerView = mFloatLayout.findViewById(R.id.rl_http)
        mFloatLayout.findViewById<View>(R.id.iv_delete).setOnClickListener {
            adapter.clear()
        }
        mFloatLayout.findViewById<View>(R.id.iv_zoom).setOnClickListener {
            FloatWindow.get(TAG_ICON).show()
            FloatWindow.get(TAG_DETAIL).hide()
        }
        mFloatLayout.findViewById<View>(R.id.iv_close).setOnClickListener {
            destroyFloatWindow()
        }
    }

    @Synchronized
    fun addMsg(response: Response, body: ResponseBody) {
        mHandler.post {
            var httpBean = HttpBean(
                    response.request().method(),
                    response.code().toString(),
                    (response.receivedResponseAtMillis() - response.sentRequestAtMillis()).toString() + "ms",
                    response.request().url().encodedPath(),
                    response.headers(),
                    response.request().headers(),
                    body.source().readUtf8())

            insertItem(httpBean)
        }
    }

    @Synchronized
    fun addMsg(request: Request, errorMsg: String) {
        mHandler.post {
            var httpBean = HttpBean(
                    request.method(),
                    "null",
                    "null",
                    request.url().encodedPath(),
                    null,
                    request.headers(),
                    errorMsg)

            insertItem(httpBean)
            Log.d(TAG, "message size = " + adapter.count)
        }
    }

    private val mPermissionListener = object : PermissionListener {
        override fun onSuccess() {
            Log.d(TAG, "onSuccess")
        }

        override fun onFail() {
            Log.d(TAG, "onFail")
        }
    }

    private val mViewStateListener = object : ViewStateListener {
        override fun onPositionUpdate(x: Int, y: Int) {
            Log.d(TAG, "onPositionUpdate: x=$x y=$y")
        }

        override fun onShow() {
            Log.d(TAG, "onShow")
        }

        override fun onHide() {
            Log.d(TAG, "onHide")
        }

        override fun onDismiss() {
            Log.d(TAG, "onDismiss")
        }

        override fun onMoveAnimStart() {
            Log.d(TAG, "onMoveAnimStart")
        }

        override fun onMoveAnimEnd() {
            Log.d(TAG, "onMoveAnimEnd")
        }

        override fun onBackToDesktop() {
            Log.d(TAG, "onBackToDesktop")
        }
    }

    fun showFloatWindow() {
        val imageView = ImageView(applicationContext)
        imageView.setImageResource(R.drawable.icon_http)

        FloatWindow
                .with(applicationContext)
                .setView(imageView)
                .setTag(TAG_ICON)
                .setX(Screen.width)
                .setY(Screen.height, 0.7f)
                .setMoveType(MoveType.slide)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
                .build()

        FloatWindow
                .with(applicationContext)
                .setView(mFloatLayout)
                .setTag(TAG_DETAIL)
                .setWidth(Screen.width, 1f) //设置悬浮控件宽高
                .setX(Screen.width, 0f)
                .setY(Screen.height, 0.2f)
                .setMoveType(MoveType.slide)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
                .build()

        imageView.setOnClickListener {
            FloatWindow.get(TAG_ICON).hide()
            FloatWindow.get(TAG_DETAIL).show()
        }

        FloatWindow.get(TAG_ICON).hide()
        FloatWindow.get(TAG_DETAIL).show()


        // 列表配置
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { position ->
            try {
                Log.d(TAG, adapter.getItem(position).path + "onclick")

                var item = adapter.getItem(position)
//                val intent = Intent(applicationContext, WebActivity::class.java)
//                var map = mapOf("path" to item.path, "Status Code" to item.code, "Method" to item.method, "RTT" to item.time)
//
//                intent.putExtra("info", JSON.toJSONString(map))
//                intent.putExtra("data", item.body)
//                intent.putExtra("requestHeaders", JSON.toJSONString(item.requestHeaders.toMultimap()))
//                if (item.responseHeaders != null)
//                    intent.putExtra("responseHeaders", JSON.toJSONString(item.responseHeaders.toMultimap()))
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                applicationContext.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun destroyFloatWindow() {
        if (FloatWindow.get(TAG_ICON) != null) {
            FloatWindow.get(TAG_ICON).show()
            FloatWindow.destroy(TAG_ICON)
            FloatWindow.get(TAG_DETAIL).show()
            FloatWindow.destroy(TAG_DETAIL)
        }
        adapter.clear()
        instance = null
    }

    private class ActivityAdapter(context: Context) : RecyclerArrayAdapter<HttpBean>(context) {

        override fun OnCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
            return ViewHolder(parent)
        }

        inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<HttpBean>(parent, R.layout.item_http) {
            internal var method: TextView = `$`(R.id.tv_method)
            internal var code: TextView = `$`(R.id.tv_code)
            internal var time: TextView = `$`(R.id.tv_time)
            internal var path: TextView = `$`(R.id.tv_path)

            override fun setData(data: HttpBean) {
                super.setData(data)
                method.text = data.method.toString()
                if (method.text != "GET") {
                    method.setBackgroundResource(R.drawable.gray_bg_round4_bg)
                } else {
                    method.setBackgroundResource(R.drawable.blue_bg_round4_bg)
                }
                code.text = data.code
                if (code.text != "200") {
                    code.setBackgroundResource(R.drawable.red_bg_round4_bg)
                } else {
                    code.setBackgroundResource(R.drawable.green_bg_round4_bg)
                }
                time.text = data.time
                path.text = data.path
            }
        }
    }

    @Synchronized
    private fun insertItem(msg: HttpBean) {
        adapter.add(msg)
        while (adapter.count >= MAX_RESPONSE_COUNT) {
            adapter.remove(0)
        }
        adapter.notifyDataSetChanged()
        if (!recyclerView.canScrollVertically(1)) {
            recyclerView.smoothScrollToPosition(adapter.count)
        }
    }
}