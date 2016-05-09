package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(部门表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name = "deptfile")
public class DeptFileItem {
    /**
     * mid
     */
    @Id(column = "mid")
    private int mid;
    /**
     * 组织代码
     */
    private String org_code;
    /**
     * 组织名称
     */
    private String org_name;

    /**
     * 部门代码
     */
    private String depart_code;

    private String file_name;
    /**
     * 部门名称
     */
    private String depart_name;
    /**
     * 创建人
     */
    private String founder_code;
    /**
     * 创建人电话
     */
    private String founder_name;
    /**
     * 创建人昵称
     */
    private String nickname;
    /**
     * 备注
     */
    private String remark;
    private String create_time;
    private String update_time;

    /**
     * 图片代码
     */
    private String photo_code;

    private String pic_road;

    public String getPhoto_code() {
        return photo_code;
    }

    public void setPhoto_code(String photo_code) {
        this.photo_code = photo_code;
    }

    public String getPic_road() {
        return pic_road;
    }

    public void setPic_road(String pic_road) {
        this.pic_road = pic_road;
    }

    public String getFile_name() {
        return file_name;
    }


    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getDepart_code() {
        return depart_code;
    }

    public void setDepart_code(String depart_code) {
        this.depart_code = depart_code;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getDepart_name() {
        return depart_name;
    }

    public void setDepart_name(String depart_name) {
        this.depart_name = depart_name;
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

    public String getFounder_code() {
        return founder_code;
    }

    public void setFounder_code(String founder_code) {
        this.founder_code = founder_code;
    }

    public String getFounder_name() {
        return founder_name;
    }

    public void setFounder_name(String founder_name) {
        this.founder_name = founder_name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}




