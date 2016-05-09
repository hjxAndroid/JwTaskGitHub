package api.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        String url = Contants.All_URL;
        return url;
    }

    /**
     * 获取推送servlet
     */
    public static String getPushUrl() {
        String url = Contants.All_URL + "/servlet/PushServlet";
        return url;
    }

    /**
     * 多个推送servlet
     */
    public static String getPublicUrl() {
        String url = Contants.All_URL + "/servlet/PublicServlet";
        return url;
    }

    /**
     * 全组推送servlet
     */
    public static String getOrgUrl() {
        String url = Contants.All_URL + "/servlet/OrgServlet";
        return url;
    }

    //比较两个时间大小
    public static boolean compare_date(String DATE1, String DATE2) {
        boolean result = false;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                result = true;
            } else if (dt1.getTime() < dt2.getTime()) {
                result = false;
            } else {
                result = false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    /**
     * 上传图片
     */
    public static String uploadPic() {
        String url = Contants.All_URL + "/servlet/CloudFileRest?appkey=58975c511b1bcaddecc906a2c9337665";
        return url;
    }


}
