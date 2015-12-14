package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(签到表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name = "sign")
public class Sign {
    /**
     * mid
     */
    @Id(column = "mid")
    private int mid;
    /**
     * 签到代码
     */
    private String sign_code;
    /**
     * 签到名称
     */
    private String sign_title;
    /**
     * 预留字段
     */
    private int prouser_id;
    /**
     * 发布人代码
     */
    private String prouser_code;
    /**
     * 发布人名称
     */
    private String prouser_name;
    /**
     * 发布时间,以下的CreateTime,这个为预留字段
     */
    private String sign_time;
    /**
     * 预留字段
     */
    /*private int receive_id;*/
    /**
     * 接受者代码,多个
     */
    private String receive_code;
    /**
     * 接受者名称
     */
    private String receive_name;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private String create_time;
    /**
     * 更新时间
     */
    private String update_time;
    /**
     * 签到内容
     */
    private String send_context;
    /**
     * 阅读状态
     */
    private String read_status;

    public void setSend_context(String send_context) {
        this.send_context = send_context;
    }

    public String getSend_context() {

        return send_context;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getSign_code() {
        return sign_code;
    }

    public void setSign_code(String sign_code) {
        this.sign_code = sign_code;
    }

    public String getSign_title() {
        return sign_title;
    }

    public void setSign_title(String sign_title) {
        this.sign_title = sign_title;
    }

    public int getProuser_id() {
        return prouser_id;
    }

    public void setProuser_id(int prouser_id) {
        this.prouser_id = prouser_id;
    }

    public String getProuser_code() {
        return prouser_code;
    }

    public void setProuser_code(String prouser_code) {
        this.prouser_code = prouser_code;
    }

    public String getProuser_name() {
        return prouser_name;
    }

    public void setProuser_name(String prouser_name) {
        this.prouser_name = prouser_name;
    }

    public String getSign_time() {
        return sign_time;
    }

    public void setSign_time(String sign_time) {
        this.sign_time = sign_time;
    }

    public String getRead_status() {
        return read_status;
    }

    public void setRead_status(String read_status) {
        this.read_status = read_status;
    }


    public String getReceive_code() {
        return receive_code;
    }

    public void setReceive_code(String receive_code) {
        this.receive_code = receive_code;
    }

    public String getReceive_name() {
        return receive_name;
    }

    public void setReceive_name(String receive_name) {
        this.receive_name = receive_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}