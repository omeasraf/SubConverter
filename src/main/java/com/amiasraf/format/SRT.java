package com.amiasraf.format;

import com.amiasraf.Interfaces.Helper;

import java.util.*;
import java.util.regex.Pattern;

public class SRT implements Helper {

    public List<Object> parse(String content, Map<String,Object> options) {
        List<Object> captions = new ArrayList<>();
        String eol = options.get("eol") != null ? options.get("eol").toString() : "\r\n";
        var parts = content.split("\\r?\\n\\s+\\r?");
        for (var i = 0; i < parts.length; i++) {
            Pattern regex =  Pattern.compile("^(\\d+)\\r?\\n(\\d{1,2}:\\d{1,2}:\\d{1,2}([.,]\\d{1,3})?)\\s*\\-\\-\\>\\s*(\\d{1,2}:\\d{1,2}:\\d{1,2}([.,]\\d{1,3})?)\\r?\\n([\\s\\S]*)(\\r?\\n)*$", Pattern.CASE_INSENSITIVE);
            var match = regex.matcher(parts[i]);
            if (match.matches()) {
                var caption = new HashMap<>();
                caption.put("type",  "caption");
                caption.put("index",  Integer.parseInt(match.group(1)));
                caption.put("start",  toMilliseconds.apply(match.group(2)));
                caption.put("end", toMilliseconds.apply(match.group(4)));
                caption.put("duration", (int) caption.get("end") - (int) caption.get("start"));
                var lines = match.group(6).split("\r?\n");
                caption.put("content", String.join(eol,lines));
                caption.put("text", caption.get("content")
                        .toString().replaceAll("\\<[^\\>]+\\>", "") //<b>bold</b> or <i>italic</i>
                        .replaceAll("\\{[^\\}]+\\}", "") //{b}bold{/b} or {i}italic{/i}
                        .replaceAll("\\>\\>\s*[^:]*:\s*", "")); //>> SPEAKER NAME:
                captions.add(caption);
                continue;
            }
        }
        return captions;
    }

    // SRT Converter
    public static String build(List<Object> captions, Map<String, Object> options) {
        var srt = "";
        var eol = options.get("eol") != null ? options.get("eol") : "\r\n";
        for (var i = 0; i < captions.size(); i++) {
            var caption = (Map) captions.get(i);
            if (caption.get("type").equals("undefined") || caption.get("type").equals( "caption")){
                srt += (i + 1) + eol.toString();
                srt += toTimeString.apply((int) caption.get("start")) +
                        " --> " +
                        toTimeString.apply((int) caption.get("end")) +
                        eol;
                srt += (String) caption.get("text") + eol;
                srt += eol;
                continue;
            }
        }
        return srt;
    }
}
