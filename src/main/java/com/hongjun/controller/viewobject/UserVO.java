package com.hongjun.controller.viewobject;

/**
 * @author hongjun500
 * @date 2020/6/13 23:37
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
public class UserVO {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telphone;

    // 下面的不需要返回给前端
    /*private String registerMode;
    private String thirdPartyId;
    private String encrptPassword;*/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }
}
