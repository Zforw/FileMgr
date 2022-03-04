package com.zforw.filemgr.file;

/**
 * @author zforw
 */
public enum FileType {
    /*
     * 图像
     */
    IMAGE(0, "图像"),
    /*
     * 音频
     */
    MUSIC(1, "音频"),
    /*
     * 文本
     */
    TEXT(2, "文本"),
    /*
     * 视频
     */
    VIDEO(3, "视频");
    private int code;
    private String str;
    public static FileType getType(String type) {
        if("图像".equals(type)) {
            return IMAGE;
        } else if("音频".equals(type)) {
            return MUSIC;
        } else if("文本".equals(type)) {
            return TEXT;
        } else {
            return VIDEO;
        }
    }
    public static FileType getType(int type) {
        if(type == 0) {
            return IMAGE;
        } else if(type == 1) {
            return MUSIC;
        } else if(type == 2) {
            return TEXT;
        } else {
            return VIDEO;
        }
    }
    public int getCode() {
        return code;
    }
    public String getStr() { return str; }
    FileType(int code, String str) {
        this.code = code;
        this.str = str;
    }
}
