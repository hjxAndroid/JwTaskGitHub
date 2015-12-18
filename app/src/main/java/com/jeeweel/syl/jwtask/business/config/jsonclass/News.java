package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;
import com.jeeweel.syl.lib.api.config.publicjsonclass.BaseItem;

/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(消息表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name = "news")
public class News extends BaseItem{
    /**
     * mid
     */
    @Id(column = "mid")
    private int mid;
    /**
     * 消息代码
     */
    private String msg_name;
    /**
     * 消息类型
     */
    private String readsum;
    /**
     * 消息
     */
    private String alread;
    /**
     * 备注
     */
    private String readstate;
    private String msg_title;
    private String msg_content;

    private String create_time;

    private String principal_state;

    private String auditor_state;

    private String participant_state;

    private String observer_state;

    private int draw_id;

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getMsg_name() {
        return msg_name;
    }

    public void setMsg_name(String msg_name) {
        this.msg_name = msg_name;
    }

    public String getReadsum() {
        return readsum;
    }

    public void setReadsum(String readsum) {
        this.readsum = readsum;
    }

    public String getAlread() {
        return alread;
    }

    public void setAlread(String alread) {
        this.alread = alread;
    }

    public String getReadstate() {
        return readstate;
    }

    public void setReadstate(String readstate) {
        this.readstate = readstate;
    }

    public String getMsg_title() {
        return msg_title;
    }

    public void setMsg_title(String msg_title) {
        this.msg_title = msg_title;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getPrincipal_state() {
        return principal_state;
    }

    public void setPrincipal_state(String principal_state) {
        this.principal_state = principal_state;
    }

    public String getAuditor_state() {
        return auditor_state;
    }

    public void setAuditor_state(String auditor_state) {
        this.auditor_state = auditor_state;
    }

    public String getParticipant_state() {
        return participant_state;
    }

    public void setParticipant_state(String participant_state) {
        this.participant_state = participant_state;
    }

    public String getObserver_state() {
        return observer_state;
    }

    public void setObserver_state(String observer_state) {
        this.observer_state = observer_state;
    }

    public int getDraw_id() {
        return draw_id;
    }

    public void setDraw_id(int draw_id) {
        this.draw_id = draw_id;
    }
}

