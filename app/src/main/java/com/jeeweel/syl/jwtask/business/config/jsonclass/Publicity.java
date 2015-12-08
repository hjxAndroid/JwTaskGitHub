package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;
import com.jeeweel.syl.lib.api.config.publicjsonclass.BaseItem;

/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(公告表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name = "publicity")
public class Publicity extends BaseItem{
    /**
     * mid
     */
    @Id(column = "mid")
    private int mid;
    /**
     * 公告代码
     */
    private String publicity_code;
    /**
     * 公告名称
     */
    private String publicity_title;
    /**
     * 公告内容
     */
    private String publicity_content;
    /**
     * 证人
     */
    private String witness;
    /**
     * 预留字段
     */
    private int photo_id;
    /**
     * 图片代码
     */
    private String photo_code;
    /**
     * 发布人代码
     */
    private String prouser_code;
    private String prouser_name;
    /**
     * 发布时间
     */
    private String publicity_time;
    private int accept_orgid;
    /**
     * 接收组织代码
     */
    private String accept_org_code;

    private String accept_org_name;
    /**
     * 备注
     */
    private String remark;
    private String create_time;
    private String update_time;


    private String nickname;



    public String getProuser_code() {
        return prouser_code;
    }

    public void setProuser_code(String prouser_code) {
        this.prouser_code = prouser_code;
    }

    public String getAccept_org_code() {
        return accept_org_code;
    }

    public void setAccept_org_code(String accept_org_code) {
        this.accept_org_code = accept_org_code;
    }

    public String getPhoto_code() {
        return photo_code;
    }

    public void setPhoto_code(String photo_code) {
        this.photo_code = photo_code;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getPublicity_code() {
        return publicity_code;
    }

    public void setPublicity_code(String publicity_code) {
        this.publicity_code = publicity_code;
    }

    public String getPublicity_title() {
        return publicity_title;
    }

    public void setPublicity_title(String publicity_title) {
        this.publicity_title = publicity_title;
    }

    public String getWitness() {
        return witness;
    }

    public void setWitness(String witness) {
        this.witness = witness;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }

    public String getPublicity_content() {
        return publicity_content;
    }

    public void setPublicity_content(String publicity_content) {
        this.publicity_content = publicity_content;
    }

    public String getProuser_name() {
        return prouser_name;
    }

    public void setProuser_name(String prouser_name) {
        this.prouser_name = prouser_name;
    }

    public String getPublicity_time() {
        return publicity_time;
    }

    public void setPublicity_time(String publicity_time) {
        this.publicity_time = publicity_time;
    }

    public int getAccept_orgid() {
        return accept_orgid;
    }

    public void setAccept_orgid(int accept_orgid) {
        this.accept_orgid = accept_orgid;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAccept_org_name() {
        return accept_org_name;
    }

    public void setAccept_org_name(String accept_org_name) {
        this.accept_org_name = accept_org_name;
    }
}

