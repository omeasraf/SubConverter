package com.amiasraf.format;

import com.amiasraf.Interfaces.Helper;

import java.util.*;
import java.util.regex.Pattern;


// Advanced SubStation Alpha
// Version: 4.0
// Ami Asraf

public class ASS implements Helper {

    public  List<Object> parse(String content, Map<String, Object> options) {
        Map<String, Object> meta = new HashMap();
        List columns = new ArrayList();
        List<Object> captions = new ArrayList<>();
        var eol = options.get("eol") != null ? options.get("eol") : "\r\n";
        var parts = content.split("\\r?\\n\\s*\\r?\\n");
        for (var i = 0; i < parts.length; i++) {

            Pattern regex =  Pattern.compile("^\\s*\\[([^\\]]+)\\]\\r?\\n([\\s\\S]*)(\\r?\\n)*$", Pattern.CASE_INSENSITIVE);
            var match = regex.matcher(parts[i]);
            if (match.matches()) {
                var tag = match.group(1);
                var lines = match.group(2).split("\\r?\\n");
                for (var l = 0; l < lines.length; l++) {
                    var line = lines[l];
                    if (Pattern.compile("^\\s*;").matcher(line).matches()) {
                        continue; //Skip comment
                    }
                    var m = Pattern.compile("^\\s*([^:]+):\\s*(.*)(\\r?\\n)?$").matcher(line);
                    if (m.matches()) {

                        if (tag.equals("Script Info")) {
                            if (meta.isEmpty()) {

                                meta.put("type", "meta");
                                meta.put("data", new HashMap<>());
                                captions.add(meta);
                            }
                            var name = m.group(1).trim();
                            var value = m.group(2).trim();
                            Map innerMap = (Map) meta.get("data");
                            if (innerMap == null) {
                                innerMap = new HashMap<String, Object>();
                                meta.put("data", innerMap);
                            }

                            innerMap.put(name, value);
                            continue;
                        }
                        if (tag.equals("V4 Styles") || tag.equals("V4+ Styles")) {
                            var name = m.group(1).trim();
                            var value = m.group(2).trim();
                            if (name.equals( "Format")) {
                                columns = Arrays.asList(value.split("\\s*,\\s*"));
                                continue;
                            }
                            if (name.equals("Style")) {
                                var values = value.split("\\s*,\\s*");
                                var caption = new HashMap<>();
                                caption.put("type", "style");
                                caption.put("data", new HashMap<String, Object>());
                                Map data = (Map) caption.get("data");
                                for (int c = 0; c < columns.size() && c < values.length; c++) {

                                    data.put(columns.get(c), values[c]);
                                }
                                captions.add(caption);
                                continue;
                            }
                        }
                        if (tag.equals("Events")) {
                            var name = m.group(1).trim();
                            var value = m.group(2).trim();
                            if (name.equals("Format")) {
                                columns = Arrays.asList(value.split("\\s*,\\s*"));
                                continue;
                            }
                            if (name.equals("Dialogue")) {
                                var values = value.split("\\s*,\\s*");
                                Map<String, Object> caption = new HashMap<>();
                                caption.put("type", "caption");
                                Map<Object, Object> temp = new HashMap<>();
                                for (var c = 0; c < columns.size() && c < values.length; c++) {
//                                    caption["data"][columns[c]] = values[c];
                                    temp.put(columns.get(c), values[c]);
                                }
                                caption.put("data", temp);
                                Map data = (Map) caption.get("data");
                                caption.put("start",
                                        toMilliseconds.apply(data.get("Start").toString()));
                                caption.put("end",
                                        toMilliseconds.apply((String) data.get("End")));
                                caption.put("duration", (int)caption.get("end") - (int)caption.get("start"));

                                caption.put("content", data.get("Text"));

                                var indexOfText = getPosition(value, ",", columns.size() - 1) + 1;
                                caption.put("content", value.substring(indexOfText));
                                data.put("Text", caption.get("content"));

                                caption.put("text", caption.get("content").toString()
                                        .replaceAll("\\n", eol.toString())
                                        .replaceAll("\\{[^\\}]+\\}", ""));
                                captions.add(caption);
                                continue;
                            }
                        }
                    }
                }
            }
        }
        return captions;
    }


    public  int getPosition(String s, String search, int index) {
        return String.join(search,Arrays.asList(s.split(search)).subList(0, index)).length();
    }


    public String build(List<Object> captions, Map<String, Object> options) {
        String eol = options.get("eol") != null ? options.get("eol").toString() : "\r\n";
        print(options.get("format"));
        boolean ass = options.get("format").toString().equals("ass");

        var content = "";
        content += "[Script Info]" + eol;
        content += "; Script generated by SubtitleConverter by Ome Asraf " + eol;
        content += "ScriptType: v4.00" + (ass ? "+" : "") + eol;
        content += "Collisions: Normal" + eol;
        content += eol;
        if (ass) {
            content += "[V4+ Styles]" + eol;
            content += "Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding" + eol;
            content += "Style: DefaultVCD, Arial,28,&H00B4FCFC,&H00B4FCFC,&H00000008,&H80000008,-1,0,0,0,100,100,0.00,0.00,1,1.00,2.00,2,30,30,30,0" + eol;
        }
        else {
            content += "[V4 Styles]" + eol;
            content += "Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, TertiaryColour, BackColour, Bold, Italic, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, AlphaLevel, Encoding" + eol;
            content += "Style: DefaultVCD, Arial,28,11861244,11861244,11861244,-2147483640,-1,0,1,1,2,2,30,30,30,0,0" + eol;
        }
        content += eol;
        content += "[Events]" + eol;
        content += "Format: " + (ass ? "Layer" : "Marked") + ", Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text" + eol;

        for (var i = 0; i < captions.size(); i++) {
            var caption = (Map) captions.get(i);
            if (caption.get("type").equals("meta")) {
                continue;
            }

            if (caption.get("type").equals("undefined") || caption.get("type").equals("caption")) {
                content += "Dialogue: " + (ass ? "0" : "Marked=0") + "," + toTimeString.apply((int) caption.get("start")) + "," + toTimeString.apply((int) caption.get("end")) + ",DefaultVCD, NTP,0000,0000,0000,," + caption.get("text").toString().replaceAll("\r?\n", "\\N") + eol;
                continue;
            }
        }

        return content;
    }


}
