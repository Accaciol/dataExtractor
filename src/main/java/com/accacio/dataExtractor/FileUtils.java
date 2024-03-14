package com.accacio.dataExtractor;

import java.io.File;

public class FileUtils {
    public static String getJarDirectory() {
        return System.getProperty("user.dir");
    }

    public static String getUserHomeDirectory() {
        return System.getProperty("user.home");
    }

    public static String buildFilePath(String directory, String fileName) {
        return directory + File.separator + fileName;
    }
}
