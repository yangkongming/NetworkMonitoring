package www.pdx.life.networkmonitoring.util;

public class UserInfo {
    public String userAgent = "ali-yingyongbao/1.5.0 (A3000; U; Android 7.1; zh-CN;)locale=zh-CN";
    public String webUserAgent = "1261566919";//APP中唯一标识 一般使用手机号
    public String userId = "";

    private static UserInfo sUserInfo;
    private UserInfo() {}
    private static UserInfo getInstance() {
        if (sUserInfo == null) {
            sUserInfo = new UserInfo();
        }
        return sUserInfo;
    }

}
