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

    // 파일을 존재여부를 확인하는 메소드
    public static Boolean fileIsLive(String isLivefile) {
        File f1 = new File(isLivefile);

        if (f1.exists()) {
            return true;
        } else {
            return false;
        }
    }

    // 파일을 생성하는 메소드
    public static void fileMake(String makeFileName) {
        File f1 = new File(makeFileName);
        try {
            f1.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 파일을 삭제하는 메소드
    public static void fileDelete(String deleteFileName) {
        File I = new File(deleteFileName);
        I.delete();
    }

    // 파일을 복사하는 메소드
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 파일을 이동하는 메소드
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

            // 복사한뒤 원본파일을 삭제함
            fileDelete(inFileName);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 디렉토리의 파일 리스트를 읽는 메소드
    public static List<File> getDirFileList(String dirPath) {
        // 디렉토리 파일 리스트
        List<File> dirFileList = null;

        // 파일 목록을 요청한 디렉토리를 가지고 파일 객체를 생성함
        File dir = new File(dirPath);

        // 디렉토리가 존재한다면
        if (dir.exists()) {
            // 파일 목록을 구함
            File[] files = dir.listFiles();

            // 파일 배열을 파일 리스트로 변화함
            dirFileList = Arrays.asList(files);
        }

        return dirFileList;
    }


    // 폴더 존재 여부 체크 후 생성
    public void directoryConfirmAndMake(String targetDir){
        File d = new File(targetDir);
        if(!d.isDirectory()){
            if(!d.mkdirs()){
                d.mkdirs();
                System.out.println("폴더 생성");
            }
        }
    }

    public static String renameOfString(File f) { // File f는 원본 파일
        if (createNewFile(f))
            return f.getName(); // 생성된 f가 중복되지 않으면 리턴

        String name = f.getName();
        String body = null;
        String ext = null;

        int dot = name.lastIndexOf(".");
        if (dot != -1) { // 확장자가 없을때
            body = name.substring(0, dot);
            ext = name.substring(dot);
        } else { // 확장자가 있을때
            body = name;
            ext = "";
        }

        int count = 0;
        // 중복된 파일이 있을때
        // 파일이름뒤에 a_(숫자).확장자 이렇게 들어가게 되는데 숫자는 99999까지 된다.
        while (!createNewFile(f) && count < 99999) {
            count++;
            //String newName = body + count + ext;
            String newName = body + " (" + count + ")" + ext;
            f = new File(f.getParent(), newName);
        }
        return f.getName();
    }

    public static File renameOfFile(File f) { // File f는 원본 파일
        if (createNewFile(f))
            return f; // 생성된 f가 중복되지 않으면 리턴

        String name = f.getName();
        String body = null;
        String ext = null;

        int dot = name.lastIndexOf(".");
        if (dot != -1) { // 확장자가 없을때
            body = name.substring(0, dot);
            ext = name.substring(dot);
        } else { // 확장자가 있을때
            body = name;
            ext = "";
        }

        int count = 0;
        // 중복된 파일이 있을때
        // 파일이름뒤에 a_(숫자).확장자 이렇게 들어가게 되는데 숫자는 99999까지 된다.
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
            return f.createNewFile(); // 존재하는 파일이 아니면
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
