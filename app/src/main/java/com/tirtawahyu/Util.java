package com.tirtawahyu;

import java.text.NumberFormat;
import java.util.Locale;

public class Util {

    public static String formatPrice(int price) {
        Locale localeID = new Locale("in", "ID");

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        String formattedPrice = formatRupiah.format(price);

        return formattedPrice;
    }
}
