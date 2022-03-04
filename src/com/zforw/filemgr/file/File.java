package com.zforw.filemgr.file;

import com.zforw.filemgr.main.Func;


/**
 * @version: 1.0
 * @author: zforw
 * @date: 2022/02/22 4:33 下午
 * @project: FIleMgr
 * @description:
 */
public class File {
    protected static int tot = 0;
    protected int id;
    protected String name;
    protected FileType fileType;

    public static int getSelect(String r) {
        return switch (r) {
            case "P5" -> 1;
            case "P6" -> 2;
            case "P7" -> 3;
            default -> 0;
        };
    }

    public File(java.io.File file, FileType fileType) {

    }

    protected String getInfo() {
        return String.format("%s %s", id, name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static int getTot() { return tot; }

    protected File delete() {
        tot--;
        return this;
    }

    public static String[] classOfFileEN = {"Administration", "Contract", "Enterprise", "Finance", "Food", "Law", "Market", "Others"};
    public static String[] classOfFileCN = {"行政档案", "合同档案", "企业影像档案", "财务档案", "食品许可档案", "执法档案", "市场监管档案", "其他档案"};
    public static int fileClass(String fc) {
        for (int i = 0;i < 8;i++) {
            if (classOfFileCN[i].equals(fc) || classOfFileEN[i].equals(fc)) {
                return i;
            }
        }
        return -1;
    }
}
