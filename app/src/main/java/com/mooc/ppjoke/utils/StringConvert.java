package com.mooc.ppjoke.utils;

public class StringConvert {

    public static String convertFeedUgc(int count) {

        if (count < 10_000) {
            return String.valueOf(count);
        }
        return count / 10_000 + "万";
    }
}
