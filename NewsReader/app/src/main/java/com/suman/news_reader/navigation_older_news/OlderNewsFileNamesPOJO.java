package com.suman.news_reader.navigation_older_news;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

        import java.util.ArrayList;

/**
 * The needful Data in ProcessedVineDataValues Object
 * @author sumansucharitdas
 *
 */
public class OlderNewsFileNamesPOJO {
    public static List<String> fileNames = new ArrayList<String>();
    public static List<String> dateOfCreation = new ArrayList<String>();
    private String             dir = Environment.getExternalStorageDirectory() + "/NewsReader/";
    private File               files = new File(dir);

    public OlderNewsFileNamesPOJO(){
        if(files.exists()){
            int count = 0 ;
            for(File file: files.listFiles()){
                dateOfCreation.add(count, String.valueOf(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(file.lastModified())));
                fileNames.add(count, file.getName());
                count++;
            }
        }
    }
}