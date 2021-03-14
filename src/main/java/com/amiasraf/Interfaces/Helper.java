package com.amiasraf.Interfaces;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Helper {
    Function<String, Integer> toMilliseconds = str -> {
        Pattern pattern = Pattern.compile("^\\s*(\\d+):?(\\d{1,2}):(\\d{1,2})([.,](\\d{1,3}))?\\s*$", Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(str);
        int hour =  match.matches() && match.group(1) != null ? Integer.parseInt((String) match.group(1).replace(":","")) : 0;
        int minute = Integer.parseInt(match.group(2));
        int second = Integer.parseInt(match.group(3));
        int ff = match.group(5) != null ? Integer.parseInt(match.group(5)) : 0;
        int ms = hour * 3600 * 1000 + minute * 60 * 1000 + second * 1000 + ff * 10;
        return ms;
    };


    Function<Integer,String> toTimeString = ms -> {
        int hh = (int) Math.floor(ms/ 1000 / 3600);
        int mm = (int) Math.floor(ms/ 1000 / 60 % 60);
        int ss = (int) Math.floor(ms/ 1000 % 60);
        int ff = (int) Math.floor(ms % 1000 / 10);
        var time = hh + ":" + (mm < 10 ? "0" : "") + mm + ":" + (ss < 10 ? "0" : "") + ss + "." + (ff < 10 ? "0" : "") + ff;
        return time;
    };

    default void print(Object output){
        System.out.println(output);
    }

}
