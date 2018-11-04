package com.tirtawahyu.util;

import java.text.NumberFormat;
import java.util.Locale;

public class Util {

    public static String formatPrice(int price) {
        Locale localeID = new Locale("in", "ID");

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        String formattedPrice = formatRupiah.format(price);

        return formattedPrice;
    }

    public static String formatUsername(String username) {
        String dummyEmailDomain = Constants.DUMMY_EMAIL_DOMAIN;
        String email = username + dummyEmailDomain;

        return email;
    }

    public static boolean validate(String username, String password) {
        return  !username.isEmpty() && !password.isEmpty();
    }
}