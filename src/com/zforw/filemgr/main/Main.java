package com.zforw.filemgr.main;

import com.zforw.filemgr.file.FileStore;
import com.zforw.filemgr.file.FileType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @version: 1.0
 * @author: zforw
 * @date: 2022/02/22 7:07 下午
 * @project: FIleMgr
 * @description: 档案管理系统
 * OSX Command: java -XstartOnFirstThread -jar xxx.jar
 */
public class Main {
    public static FileStore fileStore = new FileStore();
    protected static boolean isLogin = false;
    protected static Display display = new Display();
    protected static Shell logShell = new Shell(display);
    public static Shell infoShell = new Shell(display);
    protected static String filePath = "/Users/zforw/IdeaProjects/FIleMgr/src/data.txt";
    protected static String OS;
    protected static User user;

    public static void main(String[] args) {


        /**
         * 设置: 目录
         */

        OS = System.getProperty("os.name").toLowerCase();
        if(OS.indexOf("windows") > 0) {

            filePath = "C:\\";
        }

        /*
        File directory = new File("");//设定为当前文件夹
        try{
            System.out.println(directory.getCanonicalPath());//获取标准的路径
            System.out.println(directory.getAbsolutePath());//获取绝对路径
        }catch(Exception e){}
        */


        Func.log("load file data.txt");

        LoginShell.login();
        logShell.open();


        while(!infoShell.isDisposed()) {
            if(!display.readAndDispatch()){
                display.sleep();
            }
        }
        if (!display.isDisposed()) {
            display.dispose();
        }
        /*
        try {
            fileStore.saveFile(filePath + "data.txt");
        } catch (IOException | SQLException | ClassNotFoundException e) {
            Func.log(e.getMessage());
            return;
        }
        Func.log("store file data.txt");
        */
    }
}
