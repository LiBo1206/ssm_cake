package com.entity;

public class Admins {
    // 管理员的唯一标识符
    private Integer id;

    // 管理员的用户名
    private String username;

    // 管理员的密码
    private String password;

    // 管理员的新密码，可能用于更新密码时的临时存储
    private String passwordNew;

    // 以下为类的getter和setter方法

    // 获取新密码
    public String getPasswordNew() {
        return passwordNew;
    }

    // 设置新密码
    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }

    // 获取管理员ID
    public Integer getId() {
        return id;
    }

    // 设置管理员ID
    public void setId(Integer id) {
        this.id = id;
    }

    // 获取管理员用户名
    public String getUsername() {
        return username;
    }

    // 设置管理员用户名，对输入的用户名进行空值检查和修剪空白
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    // 获取管理员密码
    public String getPassword() {
        return password;
    }

    // 设置管理员密码，对输入的密码进行空值检查和修剪空白
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }
}