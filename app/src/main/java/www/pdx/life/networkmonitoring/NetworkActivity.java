package www.pdx.life.networkmonitoring;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import www.pdx.life.networkmonitoring.LDNetDiagnoService.LDNetDiagnoListener;
import www.pdx.life.networkmonitoring.LDNetDiagnoService.NetDiagnoService;

import static java.sql.DriverManager.println;

public class NetworkActivity extends AppCompatActivity {

    private EditText mEditText;
    private Button mButton;
    private TextView mText;

    private String showInfo = "";
    private boolean isRunning = false;
    private NetDiagnoService mNetDiagnoseService;
    private String domainName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        mEditText = findViewById(R.id.domainName);
        mButton = findViewById(R.id.btn);
        mText = findViewById(R.id.text);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRunning) {
                    showInfo = "";
                    domainName = mEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(domainName)) {
                        try {
                            domainName = Uri.parse("https://www.baidu.com").getHost();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }


                    mNetDiagnoseService = new NetDiagnoService(getApplicationContext()
                            , "Demo"
                            , "网络诊断"
                            , "1.0.0"
                            , "ok_UID"
                            , "deviceID"
                            , domainName
                            , null
                            , ""
                            , ""
                            , ""
                            , new LDNetDiagnoListener() {
                        @Override
                        public void OnNetDiagnoFinished(String log) {
                            mText.setText(log);
                            println("");
                            mButton.setText("开始诊断");
                            mButton.setEnabled(true);
                            mEditText.setInputType(InputType.TYPE_NULL);
                            mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                            isRunning = false;
                        }

                        @Override
                        public void OnNetDiagnoUpdated(String log) {
                            showInfo += log;
                            mText.setText(showInfo);
                        }
                    });

                    mNetDiagnoseService.setIfUseJNICTrace(true);
                    //        mNetDiagnoseService.setIfUseJNICConn(true);
                    mNetDiagnoseService.execute();

                    mText.setText("Traceroute with max 30 hops...");
                    mButton.setText("停止诊断");
                    mButton.setEnabled(true);
                    mEditText.setInputType(InputType.TYPE_NULL);
                } else {
                    mButton.setText("开始诊断");
                    mNetDiagnoseService.cancel(true);
                    mNetDiagnoseService = null;
                    mButton.setEnabled(true);
                    mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                isRunning = !isRunning;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetDiagnoseService != null) {
            mNetDiagnoseService.stopNetDialogsis();
        }
    }
}
