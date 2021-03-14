package com.amiasraf;

import com.amiasraf.Helpers.ReadContent;
import com.amiasraf.format.ASS;
import com.amiasraf.format.SRT;

import java.util.*;

public class Converter {
    protected String[] formats = {"ass", "srt"};
    private ASS ass = new ASS();
    private SRT srt = new SRT();



    private Map<String,Object> selectFormat(String format){
        Map<String,Object> options = new HashMap<>();
        switch (format.toLowerCase()) {
            case "ass":
                options.put("format", "ass");
                break;
            case "srt":
                options.put("format", "srt");
                break;
        }
        return options;
    }
    public String assToSRT(String filePath){
        return srt.build(ass.parse(getFile(filePath), selectFormat("srt")),selectFormat("ass"));
    }
    public String srtToAss(String filePath){
        return ass.build(srt.parse(getFile(filePath), selectFormat("ass")),selectFormat("srt"));
    }

    private String getFile(String path){
        ReadContent rc = new ReadContent();
        return rc.readFile(path);
    }

}
