package com.newbit.newbituserservice.user.support;

public class PasswordValidator {
    public static boolean isValid(String password) {
        if (password == null) return false;
        // 최소 8자, 영문자, 숫자, 특수문자 포함
        String regex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,}$";
        return password.matches(regex);
    }
}
