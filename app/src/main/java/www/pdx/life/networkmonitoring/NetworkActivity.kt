package www.pdx.life.networkmonitoring

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import www.pdx.life.networkmonitoring.LDNetDiagnoService.LDNetDiagnoListener
import www.pdx.life.networkmonitoring.LDNetDiagnoService.NetDiagnoService

import java.sql.DriverManager.println

class NetworkActivity : AppCompatActivity() {

    private var mEditText: EditText? = null
    private var mButton: Button? = null
    private var mText: TextView? = null

    private var showInfo = ""
    private var isRunning = false
    private var mNetDiagnoseService: NetDiagnoService? = null
    private var domainName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)
        mEditText = findViewById(R.id.domainName)
        mButton = findViewById(R.id.btn)
        mText = findViewById(R.id.text)
        mButton!!.setOnClickListener {
            if (!isRunning) {
                showInfo = ""
                domainName = mEditText!!.text.toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(domainName)) {
                    try {
                        domainName = Uri.parse("https://www.baidu.com").host
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                }


                mNetDiagnoseService = NetDiagnoService(applicationContext, "Demo", "网络诊断", "1.0.0", "ok_UID", "deviceID", domainName, null, "", "", "", object : LDNetDiagnoListener {
                    override fun OnNetDiagnoFinished(log: String) {
                        mText!!.text = log
                        println("")
                        mButton!!.text = "开始诊断"
                        mButton!!.isEnabled = true
                        mEditText!!.inputType = InputType.TYPE_NULL
                        mEditText!!.inputType = InputType.TYPE_CLASS_TEXT
                        isRunning = false
                    }

                    override fun OnNetDiagnoUpdated(log: String) {
                        showInfo += log
                        mText!!.text = showInfo
                    }
                })

                mNetDiagnoseService!!.setIfUseJNICTrace(true)
                //        mNetDiagnoseService.setIfUseJNICConn(true);
                mNetDiagnoseService!!.execute()

                mText!!.text = "Traceroute with max 30 hops..."
                mButton!!.text = "停止诊断"
                mButton!!.isEnabled = true
                mEditText!!.inputType = InputType.TYPE_NULL
            } else {
                mButton!!.text = "开始诊断"
                mNetDiagnoseService!!.cancel(true)
                mNetDiagnoseService = null
                mButton!!.isEnabled = true
                mEditText!!.inputType = InputType.TYPE_CLASS_TEXT
            }

            isRunning = !isRunning
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mNetDiagnoseService != null) {
            mNetDiagnoseService!!.stopNetDialogsis()
        }
    }
}
