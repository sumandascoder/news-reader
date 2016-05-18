package com.suman.news_hound.utils;

import java.util.UUID;

/**
 * Created by sumansucharitdas on 5/16/16.
 */
public class FileInstance {

    private static FileInstance fileInstance = null;
    public static String fileName = "";

    public FileInstance(){

    }

    public static FileInstance getFileInstance(){
        if(fileInstance == null){
            fileInstance = new FileInstance();
            fileInstance.fileName = UUID.randomUUID().toString();
        }
        return fileInstance;
    }
}
