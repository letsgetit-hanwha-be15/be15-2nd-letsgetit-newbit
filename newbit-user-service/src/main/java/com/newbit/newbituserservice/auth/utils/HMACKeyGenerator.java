package com.newbit.newbituserservice.auth.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class HMACKeyGenerator {

    public static void main(String[] args) {
        try {
            /* HS512를 위한 KeyGenerator 생성 */
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA512");
            keyGen.init(512); // 512비트 키 사이즈 설정

            /* 비밀 키 생성 */
            SecretKey secretKey = keyGen.generateKey();

            /* 키를 Base64로 인코딩하여 문자열로 변환 */
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            System.out.println("HS512 Key: " + encodedKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
