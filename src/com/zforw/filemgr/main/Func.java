package com.zforw.filemgr.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @version: 1.0
 * @author: zforw
 * @date: 2021/05/22 3:53 下午
 * @project: Basic
 * @description: 这个类提供了一系列需要用到的函数
 */
public class Func {

    /**
     * @description: 将信息添加到系统日志中
     * @param: [content]
     * @return: void
     */
    public static void log(String content) {
        File file = new File(Main.filePath + "log.txt");

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;

        String str = null;
        try {
            if (!file.exists()) {
                boolean hasFile = file.createNewFile();
                if (hasFile) {
                    str = "log.txt not exists, create a new file";
                }
                fos = new FileOutputStream(file);
            } else {
                fos = new FileOutputStream(file, true);
            }

            osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            if(str != null) {
                osw.write(str);
                osw.write("\r\n");
            }
            osw.write("\r\n");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /* 关闭流 */
            try {
                if (osw != null) {
                    osw.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @description: 
     * @param: [type]
     * @return: 
     */

    
    /**
     * @description: 用空格将字符串分割
     *      用于处理当两个子字符串之间多于一个空格的特殊情况
     * @param: [str]
     * @return:
     */
    public static String[] Split(String str) {
        String[] result = str.split(" ");
        for (int i = 0; i < result.length; i++) {
            if (result[i].length() == 0 && i < result.length - 1) {
                String t = result[i];
                result[i] = result[i + 1];
                result[i + 1] = t;
            }
        }
        return result;
    }

    /**
     * @description: 创建目录
     * @param: [fileDir]
     * @return:
     */
    public static void createDir(String fileDir) {
        File file = new File(fileDir);
        //如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            System.out.println("文件夹不存在");
            boolean flag = file.mkdirs();
        }
    }

    /**
     * @description: 将字符串转换为一个大整数
     * @param: [msg]
     * @return:
     */
    public static String encrypt(String msg) throws UnsupportedEncodingException {
        msg = java.net.URLEncoder.encode(msg,"GBK");
        byte[] text = msg.getBytes("GBK");//将字符串转换成byte类型数组，即各字符的二进制形式
        for (int i = 0;i < text.length;i++) {
            text[i] += 23;//偏移
        }
        BigInteger m = new BigInteger(text);
        return m.toString();
    }

    /**
     * @description: 解密函数
     * @param: [encoded]
     * @return:
     */
    public static String decrypt(String encoded) throws UnsupportedEncodingException {
        BigInteger m = new BigInteger(encoded);//二进制串转换为一个大整数
        byte[] mt = m.toByteArray();//mt为密文的BigInteger类型
        for (int i = 0; i < mt.length;i++) {
            mt[i] -= 23;
        }
        String str = new String(mt,"GBK");
        str=java.net.URLDecoder.decode(str,"GBK");
        return str;
    }

    /**
     * @description: 计算两个字符串之间的相似度, v1[len2] := 由src转换成dst所需的最少编辑操作次数
     * @param: [src, dst]
     * @return: src与dst的相似度
     */
    public static float levenshtein(String src, String dst) {
        int len1 = src.length();
        int len2 = dst.length();
        if (len1 == 0 || len2 == 0) return 0;
        int[] v0 = new int[len2 + 1];
        int[] v1 = new int[len2 + 1];
        for (int i = 0;i < v0.length;i++) {
            v0[i] = i;
        }

        for (int i = 0;i < len1;i++) {
            v1[0] = i + 1;
            for (int j = 0; j < len2;j++) {
                int cost = (src.charAt(i) == dst.charAt(j)) ? 0 : 1;
                v1[j + 1] = Math.min(v1[j] + 1, Math.min(v0[j + 1] + 1, v0[j] + cost));
            }
            System.arraycopy(v1, 0, v0, 0, v0.length);
        }
        return 1 - (float) v1[len2] / Math.max(len1, len2);
    }
}
