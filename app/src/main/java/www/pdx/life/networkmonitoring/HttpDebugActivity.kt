package www.pdx.life.networkmonitoring

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout

import www.pdx.life.networkmonitoring.httpdebug.UntouchableSwitchCompat
import www.pdx.life.networkmonitoring.httpdebug.manager.FloatWindowManager
import www.pdx.life.networkmonitoring.httpdebug.floatwindow.FloatWindow


class HttpDebugActivity : AppCompatActivity() {

    private var mUntouchableSwitchCompat: UntouchableSwitchCompat? = null
    private var mLinearLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_debug)
        mUntouchableSwitchCompat = findViewById(R.id.switch_http)
        mLinearLayout = findViewById(R.id.ll_back)
        mUntouchableSwitchCompat!!.setOnClickListener {
            if (mUntouchableSwitchCompat!!.isChecked) {
                FloatWindowManager.get().destroyFloatWindow()
                mUntouchableSwitchCompat!!.isChecked = false
            } else {
                FloatWindowManager.get().showFloatWindow()
                mUntouchableSwitchCompat!!.isChecked = true
            }
        }
        mLinearLayout!!.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        val checked = if (FloatWindow.get(FloatWindowManager.TAG_DETAIL) == null) {
            false
        } else {
            FloatWindow.get(FloatWindowManager.TAG_DETAIL).isShowing || FloatWindow.get(FloatWindowManager.TAG_ICON).isShowing
        }
        mUntouchableSwitchCompat?.isChecked = checked
    }

}
