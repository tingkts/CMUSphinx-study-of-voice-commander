package ting.utility;

import org.unix4j.Unix4j;
import org.unix4j.line.Line;
import org.unix4j.unix.Grep;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private final static String SRC_DICT = "/media/ting/localDrive/Data_TPV/CMUSphinx/official_src/Model/Mandarin/zh_broadcastnews_utf8.dic";
    private final static String TARGET_DICT = "/media/ting/localDrive/Data_TPV/CMUSphinx/workspace/TAR0008/TAR0008/0008.dic";




    public static void main(String[] args) {
        // -------------------------------
        // load items in dictionary
        // -------------------------------
        ArrayList<String> dict_items = new ArrayList<>();
        try {
            BufferedReader buf_reader = new BufferedReader(new FileReader(TARGET_DICT));
            String readLine = "";
            while ((readLine = buf_reader.readLine()) != null) {
                dict_items.add(readLine);
            }
            buf_reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ----------------------------------------
        // find Pinyin for each item in dictionary
        // ----------------------------------------
        ArrayList<String> dict_items_pinyin = new ArrayList<>();
        for (String item : dict_items) {
            dict_items_pinyin.add(handleDictItem(item));
        }

        // ----------------------------------------
        // write file
        // ----------------------------------------
        try {
            BufferedWriter buf_writer = new BufferedWriter(new FileWriter("tmp.txt"));
            String readLine = "";
            for (String item : dict_items_pinyin) {
                buf_writer.write(item); buf_writer.newLine();
            }
            buf_writer.close();

        /// ----------
        /// replace file
        /// ----------
            Files.move(Paths.get("tmp.txt"), Paths.get(TARGET_DICT), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done.");
    }

    private static String wordToPinyin(String word) {
        File file = new File(SRC_DICT);
        List<Line> lines = Unix4j.grep("^"+word+" ", file). toLineList();
        String line = lines.get(0).getContent();
        line = line.substring(2);
        return line;
    }

    private static String handleDictItem(String item) {
        String output = item;
        int num_words = item.length();
        for (int i = 0 ; i < num_words ; i++) {
            output += " " +  wordToPinyin(String.valueOf(item.charAt(i)));
        }
        return output;
    }
}