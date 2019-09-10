package www.pdx.life.networkmonitoring

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.webkit.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import www.pdx.life.networkmonitoring.jsbrage.JsBridge
import www.pdx.life.networkmonitoring.jsbrage.web.BridgeWebViewManager
import www.pdx.life.networkmonitoring.jsbrage.web.JsInterface
import java.io.ByteArrayOutputStream
import java.io.IOException


class WebActivity : AppCompatActivity() {

    private var jsStr = ""
    private var url = "http://www.baidu.com"
    private var mLayoutWeb: FrameLayout? = null
    private val mWebViewClient: OkWebViewClient by lazy { OkWebViewClient() }
    private val mWebChromeClient: OkWebChromeClient by lazy { OkWebChromeClient() }
    private val mBridgeWebViewManager: BridgeWebViewManager by lazy { BridgeWebViewManager(mAgentWeb.webCreator.webView) }

    val mAgentWeb: AgentWeb by lazy {
        AgentWeb.with(this)
                .setAgentWebParent(this.mLayoutWeb!!, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator()
                .setWebViewClient(mWebViewClient)
                .setWebChromeClient(mWebChromeClient)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .createAgentWeb()
                .ready()
                .go(url)//去除两边空格，后台有可能返回前后带有空格的url
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        mLayoutWeb = findViewById(R.id.web)

        mAgentWeb.webCreator.webView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        //H5 innerwidth error https://github.com/Justson/AgentWeb/issues/84
        mAgentWeb.agentWebSettings.webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        mAgentWeb.agentWebSettings.webSettings.loadWithOverviewMode = true
        mAgentWeb.agentWebSettings.webSettings.useWideViewPort = true
        mAgentWeb.agentWebSettings.webSettings.setSupportZoom(true)
        mAgentWeb.agentWebSettings.webSettings.builtInZoomControls = true
        mAgentWeb.agentWebSettings.webSettings.displayZoomControls = false
        mAgentWeb.agentWebSettings.webSettings.minimumFontSize = 1

        //user-agent
        mAgentWeb.agentWebSettings.webSettings.userAgentString =  "Unsafe_environment"

        //localStorage
        mAgentWeb.agentWebSettings.webSettings.javaScriptEnabled = true
        mAgentWeb.webCreator.webView.addJavascriptInterface(JsInterface(), "native")
        mAgentWeb.agentWebSettings.webSettings.domStorageEnabled = true
        mAgentWeb.agentWebSettings.webSettings.setAppCacheMaxSize(1024 * 1024 * 8)
        mAgentWeb.agentWebSettings.webSettings.setAppCachePath(applicationContext.cacheDir.absolutePath)
        mAgentWeb.agentWebSettings.webSettings.allowFileAccess = true
        mAgentWeb.agentWebSettings.webSettings.setAppCacheEnabled(true)


        //注入web调试js
        try {
            val inputStream = assets.open("vconsole.min.js")
            val buff = ByteArray(1024)
            val fromFile = ByteArrayOutputStream()
            do {
                val numRead = inputStream.read(buff)
                if (numRead <= 0) {
                    break
                }
                fromFile.write(buff, 0, numRead)
            } while (true)
            jsStr = fromFile.toString()
            inputStream.close()
            fromFile.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    inner class OkWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
        }

        override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
            super.onReceivedHttpError(view, request, errorResponse)
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)
        }
    }

    inner class OkWebChromeClient : WebChromeClient() {
        override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
            JsBridge.handleMessage(mBridgeWebViewManager, message)
            result?.confirm("JsBridge")
            return true
        }

        override fun onReceivedTitle(view: WebView?, h5Title: String?) {

        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            mAgentWeb.webCreator.webView.loadUrl("javascript:$jsStr")
            super.onProgressChanged(view, newProgress)
        }
    }

}
