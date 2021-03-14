package com.amiasraf.Helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadContent {
    public  String readFile(String filePath){
        Scanner scanner = null;
        String content = "";
        try {
            scanner = new Scanner(new File( filePath));
            while (scanner.hasNext()) {
                content += scanner.nextLine() + "\n";
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "FNF";
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return content;
    }
}
