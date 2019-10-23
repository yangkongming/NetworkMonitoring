package www.pdx.life.networkmonitoring.util;


import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GlobalFormat {
    public static final int PRICES_USD = 0;//值不能修改必须是0
    public static final int PRICES_CNY = 1;//值不能修改必须是1
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static final DateFormat DATE_FORMAT_DAY = new SimpleDateFormat(
            "yyyy-MM-dd");

    public static final DateFormat CONTRACT_FORMAT = new SimpleDateFormat(
            "MMdd");

    public static final DateFormat DATE_FORMAT_MINUTE = new SimpleDateFormat(
            "mm:ss");

    public static String formatContract(Date v) {
        return CONTRACT_FORMAT.format(v);
    }

    public static String formatContract(long v) {
        return CONTRACT_FORMAT.format(new Date(v));
    }



    public static String formatDate(Date v) {
        return DATE_FORMAT.format(v);
    }

    public static String formatDate(long v) {
        return DATE_FORMAT.format(new Date(v));
    }

    public static String formatDateDay(long v) {
        return DATE_FORMAT_DAY.format(new Date(v));
    }

    public static String formatDatMinute(long v) {
        return DATE_FORMAT_MINUTE.format(new Date(v));
    }



    public static final double CNY_CUTOFF_FACTOR = Math.pow(10.0, 2);
    public static final double BTC_CUTOFF_FACTOR = Math.pow(10.0, 3);
    public static final double LTC_CUTOFF_FACTOR = Math.pow(10.0, 3);

    public static double cutOffDecimal(double v, double factor) {
        return Math.floor(v * factor) / factor;
    }

    public static String formatPercent(double v) {
        NumberFormat formatter = NumberFormat.getPercentInstance(Locale.US);
        formatter.setMaximumFractionDigits(2);
        formatter.setGroupingUsed(false);
        return formatter.format(v);
    }

    public static String formatNumber(double v) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setGroupingUsed(false);
        return formatter.format(v);
    }

    public static String formatNumber(double v, int fractionDigits) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMaximumFractionDigits(fractionDigits);
        formatter.setMinimumFractionDigits(fractionDigits);
        formatter.setGroupingUsed(false);
        return formatter.format(v);
    }

    /**
     * BTC数量小于10个币显示1.123、10个币<=数量<1000个币显示10.1(1位小数)、>= 1000个币显示1.0k或10.1k；
     * LTC数量小于10个币显示1.12、10个币<=数量<1000个币显示10.1(1位小数)、>= 1000个币显示1.0k或10.1k；
     *
     * @param v
     * @param symbol
     * @return
     */
    public static String formatDepthAmount(double v, String symbol) {
        return formatDepthAmount(v);
    }
    public static String formatDepthAmount(double v) {
        int amountFractionDigits = 0;
        double temp = 0;
        if (v < 10) {
            amountFractionDigits = 3;
            temp = v;
        } else if (v < 1000) {
            amountFractionDigits = 1;
            temp = v;
        } else {
            amountFractionDigits = 1;
            temp = v / 1000;
        }
        NumberFormat amountFormat = NumberFormat.getNumberInstance(Locale.US);
        amountFormat.setGroupingUsed(false);
        amountFormat.setRoundingMode(RoundingMode.FLOOR);
        amountFormat.setMaximumFractionDigits(amountFractionDigits);
        amountFormat.setMinimumFractionDigits(amountFractionDigits);
        if (v >= 1000) {
            return amountFormat.format(temp) + "k";
        } else {
            return amountFormat.format(temp);
        }
    }

    /**
     * 永续合约成交：不保留末尾0，用‘k’格式化
     *
     * @param v
     * @param digits 保留小数位
     * @return
     */
    public static String formatDepthAmount(double v, int digits) {
        int amountFractionDigits;
        double formatV;
        if (v < 1000) {
            amountFractionDigits = digits;
            formatV = v;
        } else {
            amountFractionDigits = 1;
            formatV = v / 1000;
        }

        StringBuilder sb = new StringBuilder("##########");
        for (int i = 1; i <= amountFractionDigits; i++) {
            if (i == 1 && !sb.toString().contains(".")) {
                sb.append(".");
            }
            sb.append("#");
        }

        DecimalFormat decimalFormat = new DecimalFormat(sb.toString());
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        if (v >= 1000) {
            return decimalFormat.format(formatV) + "k";
        } else {
            return decimalFormat.format(formatV);
        }
    }

    public static String formatDisplayTime(float displayTime) {
        return String.format("%.2f", displayTime) + "ms";
    }



}