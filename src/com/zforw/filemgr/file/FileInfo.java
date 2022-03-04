package com.zforw.filemgr.file;

import java.io.FileInputStream;
import java.sql.Date;

/**
 * @version: 1.0
 * @author: zforw
 * @date: 2022/02/26 7:13 PM
 * @project: FIleMgr
 * @description:
 */
public class FileInfo {
    public String name;
    public String fileClass;
    public FileType type;
    public FileInfo(String name, String fileClass, FileType type) {
        this.name = name;
        this.fileClass = fileClass;
        this.type = type;
    }
}
