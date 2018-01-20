package hello.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CheckParams {
    /**
     * 参考：http://blog.csdn.net/afei__/article/details/51482801
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean checkPhone(String phone) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[^9])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    public static boolean checkEmail(String email) {
        String regExp = "^[a-zA-Z0-9-_]+@[a-zA-Z0-9_-]+\\.[a-zA-Z0-9]{2,}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static void main(String[] args) {
        System.out.println("check phone:");
        System.out.println(checkPhone("155211"));
        System.out.println(checkPhone("15521164491"));  // true
        System.out.println(checkPhone("15421164491"));
        System.out.println(checkPhone("155211644911"));
        System.out.println(checkPhone("14721164491"));  // true
        System.out.println(checkPhone("13821164491"));  // true
        System.out.println(checkPhone("15521abc491"));

        System.out.println("check email:");
        System.out.println(checkEmail("8739@qq.com"));  // true
        System.out.println(checkEmail("87-3_9@qq.com"));  // true
        System.out.println(checkEmail("87.39@qq.com"));
        System.out.println(checkEmail("cty@qq.cc"));  // true
        System.out.println(checkEmail("8739@123.com"));  // true
        System.out.println(checkEmail("8739@12_.com"));  // true
        System.out.println(checkEmail("8739@qq.c"));
    }
}
