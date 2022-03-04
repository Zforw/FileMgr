package com.zforw.filemgr.main;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.zforw.filemgr.file.File;
import com.zforw.filemgr.file.FileInfo;
import com.zforw.filemgr.file.FileStore;
import com.zforw.filemgr.file.FileType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;

/**
 * @version: 1.0
 * @author: zforw
 * @date: 2022/02/22 7:19 下午
 * @project: FIleMgr
 * @description:
 */
public class InfoShell {
    protected static Collection collection;
    private static void updTable(Table table, int index, int type) {
        collection = Main.fileStore.getIter(index);
        table.removeAll();
        for (Object o : collection) {
            FileInfo fileInfo = (FileInfo) o;
            if(fileInfo.type.getCode() != type) {
                continue;
            }
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(fileInfo.name);
        }
    }
    public static void initShell() {
        Main.infoShell.setBounds(570 - 75, 200 - 50, 600, 500);
        Main.infoShell.setText("主界面");

        Label label1 = new Label(Main.infoShell, SWT.NONE);
        label1.setText("文件类别");
        label1.setBounds(40, 24, 90, 30);
        Label label2 = new Label(Main.infoShell, SWT.NONE);
        label2.setText("文件格式");
        label2.setBounds(40, 74, 90, 30);
        Label label3 = new Label(Main.infoShell, SWT.NONE);
        label3.setText("已选择文件: ");
        label3.setBounds(420, 24, 90, 30);
        Label sltFile = new Label(Main.infoShell, SWT.CENTER);
        sltFile.setBounds(350, 44, 220, 30);
        //op.setBackground(new Color(255, 255, 255));
        Text op = new Text(Main.infoShell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL  | SWT.READ_ONLY);
        //滚动条
        op.setEditable(false);
        op.setText("登录成功");
        op.setBounds(40, 250, 250, 170);
        op.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent modifyEvent) {
                op.setTopIndex(op.getLineHeight());
            }
        });

        Combo fileClass = new Combo(Main.infoShell, SWT.DROP_DOWN | SWT.READ_ONLY);
        fileClass.setItems("行政档案", "合同档案", "企业影像档案", "财务档案", "食品许可档案", "执法档案", "市场监管档案", "其他档案");
        fileClass.setBounds(124, 20, 90, 30);
        fileClass.select(0);
        Combo fileType = new Combo(Main.infoShell, SWT.DROP_DOWN | SWT.READ_ONLY);
        fileType.setItems("图像", "音频", "文本", "视频");
        fileType.setBounds(124, 70, 90, 30);
        fileType.select(0);
        Table table = new Table(Main.infoShell, SWT.BORDER);
        table.setBounds(350, 70, 220, 350);
        TableColumn tc1 = new TableColumn(table, SWT.CENTER);
        tc1.setText("文件名");
        tc1.setWidth(215);
        table.setHeaderVisible(true);

        final String[] selectFileName = new String[1];
        /**
         * @description: 选中文件
         * @param: []
         * @return:
         */
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if(table.getSelectionCount() == 0) {
                    return;
                }
                TableItem t = table.getItem(table.getSelectionIndex());
                selectFileName[0] = t.getText(0);
                sltFile.setText(selectFileName[0]);
            }
        });

        updTable(table, 0, 0);

        fileClass.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updTable(table, fileClass.getSelectionIndex(), fileType.getSelectionIndex());
            }
        });

        fileType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updTable(table, fileClass.getSelectionIndex(), fileType.getSelectionIndex());
            }
        });

        Button loadButton = new Button(Main.infoShell,SWT.NONE);
        Button downButton = new Button(Main.infoShell, SWT.NONE);
        Button updButton = new Button(Main.infoShell, SWT.NONE);
        Button delButton = new Button(Main.infoShell, SWT.NONE);
        loadButton.setText("读取文件");
        downButton.setText("下载文件");
        updButton.setText("上传文件");
        delButton.setText("删除文件");
        loadButton.setBounds(30, 135, 80, 25);
        downButton.setBounds(130, 135, 80, 25);
        updButton.setBounds(30, 195, 80, 25);
        delButton.setBounds(130, 195, 80, 25);
        /* 根据权限决定显示哪些按钮 */
        //deleteButton.setVisible(auth.delete());删除
        //updButton.setVisible(auth.addEmp());上传
        downButton.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e){
                String tmp = op.getText();
                DirectoryDialog dialog = new DirectoryDialog(Main.infoShell, SWT.OPEN);
                dialog.open();
                String path = dialog.getFilterPath();
                System.out.println("下载文件保存路径: " + path);
                try {
                    Main.fileStore.download(new FileInfo(selectFileName[0], File.classOfFileEN[fileClass.getSelectionIndex()], FileType.getType(fileType.getSelectionIndex())), path);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                try {
                    if (Main.OS.indexOf("windows") > 0) {
                        Runtime.getRuntime().exec("explorer.exe" + path);
                    } else {
                        Runtime.getRuntime().exec("open " + path);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                tmp = tmp + "\n下载 " + selectFileName[0] + " 到 " + path;
                op.setText(tmp);
            }
        });
        updButton.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                String tmp = op.getText();
                String temp;
                FileDialog dialog = new FileDialog(Main.infoShell, SWT.OPEN);
                if (fileType.getSelectionIndex() == 2) {
                    dialog.setFilterExtensions(new String[]{"*.txt", "*.docx", "*.doc"});
                } else if(fileType.getSelectionIndex() == 0) {
                    dialog.setFilterExtensions(new String[]{"*.jpeg", "*.png", "*.tif", "*.bmp", "*.gif", "*.webp", "*.exif", "*.*"});
                } else if(fileType.getSelectionIndex() == 1) {
                    dialog.setFilterExtensions(new String[]{"*.mp3", "*.wav", "*.flac", "*.aac"});
                } else {
                    dialog.setFilterExtensions(new String[]{"*.mp4", "*.flv", "*.avi", "*.mov", "*.mkv"});
                }
                dialog.open();
                String path = dialog.getFilterPath();
                temp = dialog.getFileName();
                if(temp.isEmpty()) {
                    return;
                }
                try {
                    Main.fileStore.upload(new FileInfo(dialog.getFileName(), File.classOfFileEN[fileClass.getSelectionIndex()], FileType.getType(fileType.getSelectionIndex())), path + "/" + dialog.getFileName());
                } catch (SQLException | ClassNotFoundException | ParseException | IOException ex) {
                    ex.printStackTrace();
                }
                FileStore.loadDatabase(Main.filePath);
                collection = Main.fileStore.getIter(fileClass.getSelectionIndex());
                table.removeAll();
                for (Object o : collection) {
                    FileInfo fileInfo = (FileInfo) o;
                    if(fileInfo.type.getCode() != fileType.getSelectionIndex()) {
                        continue;
                    }
                    TableItem item = new TableItem(table, SWT.NONE);
                    item.setText(fileInfo.name);
                }
                tmp = tmp + "\n上传 " + temp + " 到" + File.classOfFileCN[fileClass.getSelectionIndex()];
                op.setText(tmp);
                updTable(table, fileClass.getSelectionIndex(), fileType.getSelectionIndex());
            }
        });
        delButton.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e){
                if(table.getSelectionIndex() < 0) {
                    return;
                }
                String temp = table.getItem(table.getSelectionIndex()).getText();
                MessageBox messageBox = new MessageBox(Main.infoShell, SWT.OK | SWT.CANCEL | SWT.ICON_WARNING);
                messageBox.setText("是否要删除 ");
                if(messageBox.open() != SWT.OK) {
                    return;
                }
                try {
                    Main.fileStore.delete(new FileInfo(table.getItem(table.getSelectionIndex()).getText(),
                            File.classOfFileCN[fileClass.getSelectionIndex()], FileType.getType(fileType.getSelectionIndex())));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                String tmp = op.getText();
                tmp = tmp + "\n删除 " + temp;
                op.setText(tmp);
                updTable(table, fileClass.getSelectionIndex(), fileType.getSelectionIndex());
                //更新
            }
        });

        /*
        Button okButton = new Button(Main.infoShell, SWT.NONE);
        operButton.setText("分类正确确定上传");
        operButton.setBounds(10, 200, 100, 25);
         */


        /*
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if("读取文件".equals(operation)) {

                } else if("下载文件".equals(Main.operation)) {


                } else if("上传文件".equals(Main.operation)) {

                } else {
                    //确定上传
                }
            }
        });
         */
    }
}
