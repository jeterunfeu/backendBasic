package com.srlab.basic.serverside.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    private final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMeg(long bytes) {
        return bytes / MEGABYTE;
    }

    public static long bytesToMB(long sizeInBytes) {
        return sizeInBytes / (1024 * 1024);
    }

    public static String getFileNameFromFileFullPath(String fileFullName) {
        int length = fileFullName.length();
        int lastIndex = fileFullName.lastIndexOf(File.separator);
        return fileFullName.substring(lastIndex + 1, length);
    }

    public static void writeFile(String filename, byte[] content) {
        File file = new File(filename);
        FileOutputStream fop = null;
        try {
            /// check if file exist
            if (!file.exists()) {
                file.createNewFile();
            }
            // save file
            fop = new FileOutputStream(file);
            fop.write(content);
            fop.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyInputStreamToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    // file exist method
    public static Boolean fileIsLive(String isLivefile) {
        File f1 = new File(isLivefile);

        if (f1.exists()) {
            return true;
        } else {
            return false;
        }
    }

    // make file method
    public static void fileMake(String makeFileName) {
        File f1 = new File(makeFileName);
        try {
            f1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // delete file method
    public static void fileDelete(String deleteFileName) {
        File I = new File(deleteFileName);
        I.delete();
    }

    // copy file method
    public static void fileCopy(String inFileName, String outFileName) {
        try {
            FileInputStream fis = new FileInputStream(inFileName);
            FileOutputStream fos = new FileOutputStream(outFileName);

            int data = 0;
            while ((data = fis.read()) != -1) {
                fos.write(data);
            }
            fis.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // file move method
    public static void fileMove(String inFileName, String outFileName) {
        try {
            FileInputStream fis = new FileInputStream(inFileName);
            FileOutputStream fos = new FileOutputStream(outFileName);

            int data = 0;
            while ((data = fis.read()) != -1) {
                fos.write(data);
            }
            fis.close();
            fos.close();

            // copy then delete original
            fileDelete(inFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // read file list method
    public static List<File> getDirFileList(String dirPath) {
        // directory file list
        List<File> dirFileList = null;

        // make file's instance
        File dir = new File(dirPath);

        // if directory exists
        if (dir.exists()) {
            // make file list
            File[] files = dir.listFiles();

            // file array to file list
            dirFileList = Arrays.asList(files);
        }

        return dirFileList;
    }


    // file exist check and make
    public void directoryConfirmAndMake(String targetDir){
        File d = new File(targetDir);
        if(!d.isDirectory()){
            if(!d.mkdirs()){
                d.mkdirs();
                System.out.println("made folder");
            }
        }
    }

    public static String renameOfString(File f) { // f is original file
        if (createNewFile(f))
            return f.getName(); // if f is not duplicated then return

        String name = f.getName();
        String body = null;
        String ext = null;

        int dot = name.lastIndexOf(".");
        if (dot != -1) { // file type none
            body = name.substring(0, dot);
            ext = name.substring(dot);
        } else { // file type exists
            body = name;
            ext = "";
        }

        int count = 0;
        //if duplicated exists
        //add a_(num).file type, it's number limit 99999
        while (!createNewFile(f) && count < 99999) {
            count++;
            //String newName = body + count + ext;
            String newName = body + " (" + count + ")" + ext;
            f = new File(f.getParent(), newName);
        }
        return f.getName();
    }

    public static File renameOfFile(File f) { // f is original file
        if (createNewFile(f))
            return f; // if f doesn't duplicate then return

        String name = f.getName();
        String body = null;
        String ext = null;

        int dot = name.lastIndexOf(".");
        if (dot != -1) { // file type doesn't exist
            body = name.substring(0, dot);
            ext = name.substring(dot);
        } else { // file type exists
            body = name;
            ext = "";
        }

        int count = 0;
        //if duplicated exists
        //add a_(num).file type, it's number limit 99999
        while (!createNewFile(f) && count < 99999) {
            count++;
            //String newName = body + count + ext;
            String newName = body + " (" + count + ")" + ext;
            f = new File(f.getParent(), newName);
        }
        return f;
    }

    private static boolean createNewFile(File f) {
        try {
            return f.createNewFile(); // if it is not exist file
        } catch (IOException ignored) {
            return false;
        }
    }
    public static boolean changeFileName(String uploadPath,String originName,String targetName){
        boolean result = false;

        try {
            String originFullPath = uploadPath+originName;
            String changeFullPath = uploadPath+targetName;
            File file = new File(originFullPath);
            File renameFile = new File(changeFullPath);

            if(file.exists()){
                System.out.println("exsits file");
            }else{
                System.out.println("not found file");
            }

            System.out.println("originaFullPath: "+originFullPath);
            System.out.println("changeFullPath: "+changeFullPath);
            if (file.renameTo(renameFile)) {
                System.out.println("File renamed");
            } else {
                System.out.println("Sorry! the file can't be renamed..");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
