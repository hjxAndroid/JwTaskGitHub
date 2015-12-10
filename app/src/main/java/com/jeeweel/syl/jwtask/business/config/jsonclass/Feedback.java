package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

/**
 * Created by Administrator on 2015/12/10.
 */
@Table(name = "feedback")
public class Feedback {
    /**
     * mid
     */
    @Id(column = "mid")
    private int mid;
    /**
     * 代码
     */
    private String code;
    /**
     * 内容
     */
    private String content;
    /**
     * 用户代码
     */
    private String user_code;

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }
}
