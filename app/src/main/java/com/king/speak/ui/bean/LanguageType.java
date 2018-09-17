package com.king.speak.ui.bean;

import java.util.Arrays;
import java.util.List;

public enum LanguageType {

    CHINESE(1, "中文","zh"),
    ENGLISH(2, "英语","en"),
    Russian(3, "俄语","ru"),
    Japanese(4, "日语","ja"),
    Korean(5, "韩语","ko"),
    Thai(6, "泰语","th"),
    French(7, "法语","fr"),
    German(8, "德语","de"),
    Italian(9, "意大利语","it"),
    Spanish(10, "西班牙语","es");

    private int value;
    private String name;
    private String type;

    LanguageType(int value, String name,String type) {
        this.value = value;
        this.name = name;
        this.type = type;
    }

    public static LanguageType get(Integer value) {
        if (value == null) {
            return null;
        }
        for (LanguageType languageType : values()) {
            if (value == languageType.getValue()) {
                return languageType;
            }
        }
        return null;
    }

    public static LanguageType getName(String name) {
        if (name == null) {
            return null;
        }
        for (LanguageType languageType : values()) {
            if (name.equals(languageType.getName())) {
                return languageType;
            }
        }
        return null;
    }

    public static LanguageType getType(String type) {
        if (type == null) {
            return null;
        }
        for (LanguageType languageType : values()) {
            if (type.equals(languageType.getType())) {
                return languageType;
            }
        }
        return null;
    }

    public static List<String> getLanguageName() {
        return Arrays.asList(CHINESE.getName(), ENGLISH.getName(), Russian.getName(), Japanese.getName(), Korean.getName(), Thai.getName(), French.getName(), German.getName(), Italian.getName(), Spanish.getName());
    }

    public static List<String> getLanguageType() {
        return Arrays.asList(CHINESE.getType(), ENGLISH.getType(), Russian.getType(), Japanese.getType(), Korean.getType(), Thai.getType(), French.getType(), German.getType(), Italian.getType(), Spanish.getType());
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getTypeName() {
        String statusName = "";
        if (value == 0)//${jdbc.driver-class-name}
            statusName = "Pending-assignment";
        else if (value == 1 || value == 2 || value == 3 || value == 4)
            statusName = "Pending-bid";
        else if (value == 5)
            statusName = "Expired";
        else if (value == 6)
            statusName = "Close";
        else if (value == 7)
            statusName = "Pending-fulfillment";
        else if (value == 8)
            statusName = "Pending-reg";
        else if (value == 9)
            statusName = "Closed-Lost";
        return statusName;
    }

}
