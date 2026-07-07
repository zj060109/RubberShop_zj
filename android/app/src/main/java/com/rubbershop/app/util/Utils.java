package com.rubbershop.app.util;

import android.widget.Toast;
import android.content.Context;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {
    private static final String BASE_URL = "http://172.20.10.2:8080";
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("¥#,##0.00");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private static final DateTimeFormatter SHORT_FORMAT = DateTimeFormatter.ofPattern("MM/dd HH:mm");
    private static final DateTimeFormatter API_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String toPrice(Double value) {
        if (value == null) return "¥0.00";
        return PRICE_FORMAT.format(value);
    }

    public static String toPriceInt(Integer value) {
        if (value == null) return "¥0";
        return "¥" + value;
    }

    public static String toDateString(String raw) {
        if (raw == null || raw.isEmpty()) return "-";
        try {
            LocalDateTime ldt = LocalDateTime.parse(raw, API_FORMAT);
            return ldt.format(DISPLAY_FORMAT);
        } catch (Exception e) { return raw; }
    }

    public static String toShortDateString(String raw) {
        if (raw == null || raw.isEmpty()) return "-";
        try {
            LocalDateTime ldt = LocalDateTime.parse(raw, API_FORMAT);
            return ldt.format(SHORT_FORMAT);
        } catch (Exception e) { return raw; }
    }

    public static String getOrderStatusLabel(String status) {
        if (status == null) return "-";
        switch (status) {
            case "paid": return "待接单";
            case "accepted": return "已接单";
            case "shipped": return "运输中";
            case "completed": return "已完成";
            case "cancelled": return "已取消";
            case "refunding": return "退款中";
            case "refunded": return "已退款";
            case "shipped_to_merchant": return "已寄送商户";
            case "installing": return "安装中";
            case "installed": return "已安装";
            default: return status;
        }
    }

    public static String getReceivableStatusLabel(String status) {
        if (status == null) return "-";
        switch (status) {
            case "unpaid": return "待还";
            case "partially_paid": return "部分还";
            case "paid": return "已结清";
            case "void": return "已作废";
            default: return status;
        }
    }

    public static String getPurchaseStatusLabel(String status) {
        if (status == null) return "-";
        switch (status) {
            case "pending": return "待确认";
            case "quoted": return "已报价";
            case "paid": return "已付款";
            case "shipped": return "已发货";
            case "received": return "已收货";
            case "cancelled": return "已取消";
            default: return status;
        }
    }

    public static String getCustomStatusLabel(String status) {
        if (status == null) return "-";
        switch (status) {
            case "pending_quote": return "待报价";
            case "quoted": return "已报价";
            case "confirmed": return "已确认";
            case "converted": return "已转化";
            case "cancelled": return "已取消";
            default: return status;
        }
    }

    public static void snack(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void toast(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static String getImageUrl(String path) {
        if (path == null || path.isEmpty()) return null;
        if (path.startsWith("http")) return path;
        if (path.startsWith("/")) return BASE_URL + path;
        return BASE_URL + "/" + path;
    }

    public static String getFirstImageUrl(String imagesJson) {
        if (imagesJson == null || imagesJson.isEmpty() || imagesJson.equals("[]")) return null;
        try {
            JSONArray arr = new JSONArray(imagesJson);
            if (arr.length() > 0) {
                return getImageUrl(arr.getString(0));
            }
        } catch (Exception e) {}
        return null;
    }
}
