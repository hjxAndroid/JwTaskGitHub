package api.util;

import android.content.Context;

import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by asusa on 2015/11/12.
 */
public class Utils {
    /*
     * 正则表达式获取数字
     */
    public static String getNum(String content) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(content);
        String all = matcher.replaceAll("");
        return all;
    }

    //获取32位随机码
    public static String getUUid() {
        String uuid = java.util.UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid;
    }

    /**
     * 获取当前系统小时和分钟
     */
    public static String getHourAndM() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return hour + ":" + minute;
    }

    /**
     * 获取当前系统小时和分钟
     */
    public static String getPicUrl() {
        String url = Contants.URL + "/" + Contants.WEB_NAME;
        return url;
    }
}
