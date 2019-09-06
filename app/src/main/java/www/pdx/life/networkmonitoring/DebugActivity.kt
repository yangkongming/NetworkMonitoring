package www.pdx.life.networkmonitoring

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class DebugActivity : AppCompatActivity() {

    private var httpBtn: Button? = null
    private var netWorkBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        httpBtn = findViewById(R.id.http)
        netWorkBtn = findViewById(R.id.network)
        httpBtn!!.setOnClickListener { startActivity(Intent(this@DebugActivity, HttpDebugActivity::class.java)) }
        netWorkBtn!!.setOnClickListener { startActivity(Intent(this@DebugActivity, NetworkActivity::class.java)) }
    }
}
