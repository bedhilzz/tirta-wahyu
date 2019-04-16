package com.tirtawahyu.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static String formatPrice(int price) {
        Locale localeID = new Locale("in", "ID");

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        String formattedPrice = formatRupiah.format(price);

        return formattedPrice;
    }

    public static int parsePrice(String priceStr) {
        Locale localeID = new Locale("in", "ID");

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        int price = 0;
        try {
            Number priceNumber = formatRupiah.parse(priceStr);
            price = priceNumber.intValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return price;
    }

    public static String formatUsername(String username) {
        String dummyEmailDomain = Constants.DUMMY_EMAIL_DOMAIN;
        String email = username + dummyEmailDomain;

        return email;
    }

    public static boolean validate(String username, String password) {
        return  !username.isEmpty() && !password.isEmpty();
    }

    public static boolean validate(String displayName, String username, String password) {
        return  !displayName.isEmpty() && validate(username, password);
    }

    public static boolean isAdmin(String role) {
        String admin = Constants.ADMIN_ROLE;
        return role.equals(admin);
    }

    public static String formatDate(long dateLong, String pattern) {
        Date date = new Date(dateLong);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        String dateStr = dateFormat.format(date);
        return dateStr;
    }

    public static boolean sameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public static boolean sameMonth(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public static boolean isWeekend() {
        Date now = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("E", Locale.US);
        String day = fmt.format(now);
        return day.equalsIgnoreCase("Sat") || day.equalsIgnoreCase("Mon");
    }
}
