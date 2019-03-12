package com.tirtawahyu.util.printer;

import com.google.android.gms.common.util.ArrayUtils;
import com.tirtawahyu.model.Ticket;
import com.tirtawahyu.util.Util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class PrinterController {
    private static final String THIN_HORIZONTAL_DIVIDER = "--------------------------------\n";
    private static final String THICK_HORIZONTAL_DIVIDER = "================================\n";

    private static byte[] data = new byte[]{};
    private static byte[] result = new byte[]{};
    private static final String ENCODING_METHOD = "GBK";

    public static void leftAligned() {
        Command.ESC_Align[2] = 0x00;
        concat( Command.ESC_Align);
    }

    public static void centerAligned() {
        Command.ESC_Align[2] = 0x01;
        concat(Command.ESC_Align);
    }

    public static void rightAligned() {
        Command.ESC_Align[2] = 0x02;
        concat(Command.ESC_Align);
    }

    public static void textNormal() {
        Command.GS_ExclamationMark[2] = 0x00;
        concat(Command.GS_ExclamationMark);
    }

    public static void textBold() {
        Command.GS_ExclamationMark[2] = 0x11;
        concat(Command.GS_ExclamationMark);
    }

    public static void EOF() {
        concat(PrinterCommand.POS_Set_PrtAndFeedPaper(48));
        concat(Command.GS_V_m_n);

        result = Arrays.copyOf(data, data.length);
        data = new byte[]{};
    }
    
    public static void concat(byte[] newData) {
        data = ArrayUtils.concatByteArrays(data, newData);
    }

    public static void concat(String newData) {
        try {
            concat(newData.getBytes(ENCODING_METHOD));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] getFormattedReceipt(ArrayList<Ticket> tickets, int paymentAmount) {
        centerAligned();
        textBold();
        concat("WAHYU TIRTA AJI\n");

        textNormal();

        concat("Jl. Serayu RT03/02, Sudagaran\nBanyumas, Jawa Tengah\n");

        concat(THIN_HORIZONTAL_DIVIDER);
        String now = Util.formatDate(new Date().getTime(), "d/MM/yyyy HH:mm:ss");
        concat(String.format("%s%n", now));
        concat(THIN_HORIZONTAL_DIVIDER);

        leftAligned();

        int total = 0;
        for (Ticket t : tickets) {
            total += t.getTotal();
            int price = t.getTotal() / t.getJumlah();
            String ticket = String.format("%-13s  %2s  %5s  %6s%n", t.getTipe(), t.getJumlah(), price, t.getTotal());
            concat(ticket);
        }

        concat(THIN_HORIZONTAL_DIVIDER);

        rightAligned();

        String totalPrice = String.format("%s  %6s%n", "TOTAL:", total);
        concat(totalPrice);

        String payment = String.format("%s  %6s%n", "PEMBAYARAN:", paymentAmount);
        concat(payment);

        String change = String.format("%s  %6s%n%n", "KEMBALIAN:", paymentAmount-total);
        concat(change);

        EOF();

        return result;
    }
}
