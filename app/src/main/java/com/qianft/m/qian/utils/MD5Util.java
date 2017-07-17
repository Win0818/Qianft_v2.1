package com.qianft.m.qian.utils;

import android.util.Log;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @ClassName MD5Util
 * @Description 闁插洨鏁D5閸旂姴鐦戠憴锝呯槕 
 */
public class MD5Util {
    private static String tag = "Md5Util";

    public static String md5LowerCase(String string) {
        try {
            MessageDigest digester = MessageDigest.getInstance(ALGORITHM);
            byte[] buffer = string.getBytes(DEFAULT_CHARSET);
            digester.update(buffer);
            buffer = digester.digest();
            string = toLowerCaseHex(buffer);
        } catch (NoSuchAlgorithmException e) {
            Log.e(tag, e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            Log.e(tag, e.getMessage(), e);
        }
        return string;
    }

    private static String toLowerCaseHex(byte[] b) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            int v = b[i];
            builder.append(HEX_LOWER_CASE[(0xF0 & v) >> 4]);
            builder.append(HEX_LOWER_CASE[0x0F & v]);
        }
        return builder.toString();
    }

    private static final char[] HEX_CAPITALS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    private static final char[] HEX_LOWER_CASE = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    public static final String ALGORITHM = "MD5";
    public static final String DEFAULT_CHARSET = HTTP.UTF_8;

    private static final char HEX_DIGITS[] = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private static String toHexString(byte[] b) { // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }
  
    public static String string2MD5(String inStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
  
    }  
  
    /** 
     * 閸旂姴鐦戠憴锝呯槕缁犳纭�閹笛嗩攽娑擄拷濞嗏�濮炵�鍡礉娑撱倖顐肩憴锝呯槕 
     */   
    public static String convertMD5(String inStr){  
  
        char[] a = inStr.toCharArray();  
        for (int i = 0; i < a.length; i++){  
            a[i] = (char) (a[i] ^ 't');  
        }  
        String s = new String(a);  
        return s;  
  
    }  
}  