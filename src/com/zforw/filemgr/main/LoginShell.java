package com.zforw.filemgr.main;

import com.zforw.filemgr.file.FileStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

/**
 * @version: 1.0
 * @author: zforw
 * @date: 2021/05/27 7:09 下午
 * @project: Basic
 * @description:
 */
public class LoginShell {
    protected static void login() {
        Main.logShell.setBounds(570, 200, 350, 300);
        Main.logShell.setText("登录界面");

        // ---------------------登录界面代码------------------------
        Button okButton = new Button(Main.logShell,SWT.NONE);
        Button exitButton = new Button(Main.logShell, SWT.NONE);

        okButton.setText("确定");
        exitButton.setText("退出");
        exitButton.setBounds(180, 235, 70, 25);
        okButton.setBounds(80, 235, 70, 25);

        final Text nameText = new Text(Main.logShell, SWT.NONE);
        final Text passNumber = new Text(Main.logShell, SWT.PASSWORD);
        nameText.setFocus();

        nameText.setBounds(120, 100, 110, 20);
        passNumber.setBounds(120, 160, 110, 20);

        Label nameLabel = new Label(Main.logShell, SWT.NONE);
        nameLabel.setText("用户名：");
        nameLabel.setBounds(65, 100, 40,20);

        Label passLabel = new Label(Main.logShell, SWT.NONE);
        passLabel.setText("密   码：");
        passLabel.setBounds(65, 165, 40, 20);


        // 为输入框关联Enter键切换到密码框
        nameText.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.keyCode == 13) {
                    passNumber.setFocus();
                }
            }
        });
        // 为密码框关联Enter键实现登录
        passNumber.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.keyCode == 13) {
                    onLogin(nameText.getText(), passNumber.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });
        // 为确定按钮设定监听
        okButton.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                onLogin(nameText.getText(), passNumber.getText());
            }
        });
        // 为退出按钮设定监听
        exitButton.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e){
                Main.display.close();
            }
        });

    }

    /**
     * @description: 登录代码，判断账号密码是否正确
     * @param: [name, pass]
     * @return: void
    */
    private static void onLogin(String name, String pass) {
        // 判断账号密码是{否正确
        if ("admin".equals(name) && "admin".equals(pass)) {
            MessageBox msg = new MessageBox(Main.logShell, SWT.ICON_INFORMATION | SWT.YES );
            msg.setMessage("欢迎" + name + "登录!");
            msg.open();

            Func.log(name + " logged in");
            System.out.println(name + " login success ");

            FileStore.localFile = Main.filePath;
            FileStore.loadDatabase(Main.filePath);
            Main.isLogin = true;

            Main.infoShell.open();
            InfoShell.initShell();
            Main.logShell.dispose();

            while(!Main.infoShell.isDisposed()) {
                if(!Main.display.readAndDispatch()) {
                    Main.display.sleep();
                }
            }
            //Main.display.dispose();
        } else {
            MessageBox msg = new MessageBox(Main.logShell, SWT.ICON_WARNING | SWT.YES );
            msg.setMessage("用户名或密码错误！");
            msg.open();
        }
    }

}
