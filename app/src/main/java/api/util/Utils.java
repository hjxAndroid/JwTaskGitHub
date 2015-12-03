package api.util;

import android.content.Context;

import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by asusa on 2015/11/12.
 */
public class Utils {
    /*
     * 正则表达式获取数字
     */
    public static String getNum(String content){
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(content);
        String all = matcher.replaceAll("");
        return all;
    }

    //获取ip地址
    public static String getIp(Context mcContext){
        String ip = (String) SharedPreferencesUtils.get(mcContext, Contants.BASE_URL, Contants.URL);
        return ip;
    }

    //获取图片ip地址
    public static String getPicIp(Context mcContext){
        String picIp = (String) SharedPreferencesUtils.get(mcContext, Contants.PIC_URL, Contants.P_URL);
        return picIp;
    }

    //获取32位随机码
    public static String getUUid(){
        String uuid = java.util.UUID.randomUUID().toString();
        uuid = uuid.replace("-","");
        return uuid;
    }

}
