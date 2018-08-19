package com.dexter.ecommerce.models;

public class SettingData {
    private static Double tax;
    private static String currency;

    public static Double getTax() {
        return tax;
    }

    public static String getCurrency() {
        return currency;
    }

    public void setTax(Double tax) {
        SettingData.tax = tax;
    }

    public void setCurrency(String currency) {
        SettingData.currency = currency;
    }

}
