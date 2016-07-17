package com.kunpeng.ev.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckStringUtil {
    //是否为英文字母
    public static boolean is_alpha(String alpha) {
        if (alpha == null)
            return false;
        return alpha.matches("[a-zA-Z]+");
    }

    //是否为单个汉字
    public static boolean is_chinese(String chineseContent) {
        if (chineseContent == null)
            return false;
        return chineseContent.matches("[\u4e00-\u9fa5]");
    }

    //是否数字（不包含小数点）
    public static boolean is_number(String number) {
        if (number == null)
            return false;
        return number.matches("[+-]?[1-9]+[0-9]*(\\.[0-9]+)?");
    }

    //是否数字（可以包含小数点）
    //是否数字
    public static boolean is_number_with_dot(String number) {
        if (number == null)
            return false;
        return number.matches("^\\d+(\\.\\d+)?$");
    }

    /**
     * 检测String是否全是中文
     *
     * @param name
     * @return
     */
    public static boolean checkNameChese(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();

        for (int i = 0; i < name.length(); i++) {
            if (!is_chinese(String.valueOf(cTemp[i]))) {
                res = false;
                break;
            }
        }
        return res;
    }

    public static boolean emailFormat(String email) {
        if (email == null)
            return false;
        return email.matches("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?");
    }


    //号码有效性验证，目前仅支持13x，15x，18x,14x,17x号段
    public static boolean is_phone(String inputText) {
        if (inputText == null)
            return false;
        return inputText.matches("^(13[0-9]|15[012356789]|17[1678]|18[0-9]|14[157])\\d{8}$");
    }

    //银行卡号密文显示 卡号为19位  1-6明文 7-16暗文  16-19明文
    public static String replaceBankCard(String str) {
        String str2_1;
        String str1;
        String str2;
        String str3 = str;
        //如果字符串等于19位
        if (str.length() > 15 && str.length() < 20) {
            str2_1 = "";
            str1 = str.substring(0, 6);
            str2 = str.substring(str.length() - 4, str.length());
            for (int i = 0; i < str.length() - str1.length() - str2.length(); i++) {
                str2_1 = str2_1 + "*";
            }
            str3 = str1 + str2_1 + str2;
        }

        return str3;
    }

    //身份证号码密文显示   号码为18为  1-10位为明文  11-14位为暗文  15-18位为明文
    public static String replaceIdCard(String str) {
        String str4;
        String str5;
        String str6;
        //身份证为18位
        if (str.length() == 18) {
            str4 = str.substring(0, 10);
            str5 = str.substring(14, 18);
            str6 = str4 + "****" + str5;
        } else {
            str6 = str;
        }
        return str6;
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;

        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);

        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;

    }

    /**
     * 检查密码是否有效 只能为数字和字母
     *
     * @param fpassword 密码
     * @param context   上下文
     * @return 是否有效 true 密码有效  false密码无效
     */
    public static boolean isPwdValidate(String fpassword, Context context) {
        char[] pwdArray = fpassword.toCharArray();

        if (fpassword.length() < 6) {//判断 6-20位密码  只能是数字和字母
            Toast.makeText(context, "密码太短，请重新输入", Toast.LENGTH_SHORT).show();
            return false;
        } else if (fpassword.length() > 20) {
            Toast.makeText(context, "密码太长，请重新输入", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (char c : pwdArray) {
            if (c >= '0' && c <= '9') {

            } else if (c >= 'a' && c <= 'z') {

            } else if (c >= 'A' && c <= 'Z') {

            } else {
                Toast.makeText(context, "密码只能为字母和数字，请重新输入", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


}
