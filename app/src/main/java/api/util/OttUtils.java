package api.util;

import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.jeeweel.syl.lib.api.core.otto.OttoBus;

/**
 * Created by asusa on 2015/11/10.
 */
public class OttUtils {
    public static void push(String msg,String param){
        ActivityMsgEvent activityMsgEvent = new ActivityMsgEvent();
        activityMsgEvent.setMsg(msg);
        activityMsgEvent.setParam(param);
        OttoBus.getDefault().post(activityMsgEvent);
    }
}
