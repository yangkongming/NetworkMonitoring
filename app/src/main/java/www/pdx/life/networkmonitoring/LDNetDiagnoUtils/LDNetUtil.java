package www.pdx.life.networkmonitoring.LDNetDiagnoUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("DefaultLocale")
public class LDNetUtil {

    public static final String OPEN_IP = "";// 可ping的IP地址
    public static final String OPERATOR_URL = "";

    public static final String NETWORKTYPE_INVALID = "UNKNOWN";// 没有网络
    public static final String NETWORKTYPE_WAP = "WAP"; // wap网络
    public static final String NETWORKTYPE_WIFI = "WIFI"; // wifi网络

    @SuppressWarnings({"deprecation"})
    public static String getNetWorkType(Context context) {
        String mNetWorkType = null;
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return "ConnectivityManager not found";
        }
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                if (TextUtils.isEmpty(proxyHost)) {
                    mNetWorkType = mobileNetworkType(context);
                } else {
                    mNetWorkType = NETWORKTYPE_WAP;
                }
            }
        } else {
            mNetWorkType = NETWORKTYPE_INVALID;
        }
        return mNetWorkType;
    }

    /**
     * 判断网络是否连接
     */
    public static Boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public static String getMobileOperator(Context context) {
        TelephonyManager telManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager == null)
            return "未知运营商";
        String operator = telManager.getSimOperator();
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                return "中国移动";
            } else if (operator.equals("46001") || operator.equals("46006")) {
                return "中国联通";
            } else if (operator.equals("46003") || operator.equals("46005") || operator.equals("46011")) {
                return "中国电信";
            }
        }
        return operator;
    }

    /**
     * 获取本机IP(wifi)
     */
    public static String getLocalIpByWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return "wifiManager not found";
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return "wifiInfo not found";
        }
        int ipAddress = wifiInfo.getIpAddress();
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }

    /**
     * 获取本机IP(2G/3G/4G)
     */
    public static String getLocalIpBy3G() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * wifi状态下获取网关
     */
    public static String pingGateWayInWifi(Context context) {
        String gateWay = null;
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return "wifiManager not found";
        }
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo != null) {
            int tmp = dhcpInfo.gateway;
            gateWay = String.format("%d.%d.%d.%d", (tmp & 0xff), (tmp >> 8 & 0xff),
                    (tmp >> 16 & 0xff), (tmp >> 24 & 0xff));
        }
        return gateWay;
    }

    /**
     * 获取本地DNS
     */
    public static String getLocalDns(String dns) {
        Process process = null;
        String str = "";
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec("getprop net." + dns);
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                str += line;
            }
            reader.close();
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return str.trim();
    }

    /**
     * 域名解析
     */
    public static Map<String, Object> getDomainIp(String _dormain) {
        Map<String, Object> map = new HashMap<String, Object>();
        long start = 0;
        long end = 0;
        String time = null;
        InetAddress[] remoteInet = null;
        try {
            start = System.currentTimeMillis();
            remoteInet = InetAddress.getAllByName(_dormain);
            if (remoteInet != null) {
                end = System.currentTimeMillis();
                time = (end - start) + "";
            }
        } catch (UnknownHostException e) {
            end = System.currentTimeMillis();
            time = (end - start) + "";
            remoteInet = null;
            e.printStackTrace();
        } finally {
            map.put("remoteInet", remoteInet);
            map.put("useTime", time);
        }
        return map;
    }

    private static String mobileNetworkType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return "TM==null";
        }
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:// ~ 50-100 kbps
                return "2G";
            case TelephonyManager.NETWORK_TYPE_CDMA:// ~ 14-64 kbps
                return "2G";
            case TelephonyManager.NETWORK_TYPE_EDGE:// ~ 50-100 kbps
                return "2G";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:// ~ 400-1000 kbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:// ~ 600-1400 kbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_GPRS:// ~ 100 kbps
                return "2G";
            case TelephonyManager.NETWORK_TYPE_HSDPA:// ~ 2-14 Mbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_HSPA:// ~ 700-1700 kbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_HSUPA: // ~ 1-23 Mbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_UMTS:// ~ 400-7000 kbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_EHRPD:// ~ 1-2 Mbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_EVDO_B: // ~ 5 Mbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_HSPAP:// ~ 10-20 Mbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_IDEN:// ~25 kbps
                return "2G";
            case TelephonyManager.NETWORK_TYPE_LTE:// ~ 10+ Mbps
                return "4G";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "UNKNOWN";
            default:
                return "4G";
        }
    }

    /**
     * 输入流转变成字符串
     */
    public static String getStringFromStream(InputStream is) {
        byte[] bytes = new byte[1024];
        int len = 0;
        String res = "";
        try {
            while ((len = is.read(bytes)) != -1) {
                res = res + new String(bytes, 0, len, "gbk");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }


    /**
     * 获取外网IP地址
     *
     * @return
     */
    public static String GetNetIp() {
        String IP = "";
        try {
            String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
            URL url = new URL(address);

            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.7 Safari/537.36"); //设置浏览器ua 保证不出现503

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();

                // 将流转化为字符串
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in));

                String tmpString = "";
                StringBuilder retJSON = new StringBuilder();
                while ((tmpString = reader.readLine()) != null) {
                    retJSON.append(tmpString + "\n");
                }

                JSONObject jsonObject = new JSONObject(retJSON.toString());
                String code = jsonObject.getString("code");

                if (code.equals("0")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    IP = data.getString("ip") + "(" + data.getString("country")
                            + data.getString("area") + "区"
                            + data.getString("region") + data.getString("city")
                            + data.getString("isp") + ")";

                    Log.e("提示", "您的IP地址是：" + IP);
                } else {
                    IP = "";
                    Log.e("提示", "IP接口异常，无法获取IP地址！");
                }
            } else {
                IP = "";
                Log.e("提示", "网络连接异常，无法获取IP地址！ code = " + connection.getResponseCode());
            }
        } catch (Exception e) {
            IP = "";
            Log.e("提示", "获取IP地址时出现异常，异常信息是：" + e.toString());
        }
        return IP;
    }
}
