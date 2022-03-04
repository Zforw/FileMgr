package com.zforw.filemgr.file;

import com.zforw.filemgr.main.Func;
import com.zforw.filemgr.main.Main;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TableItem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

/**
 * @version: 1.0
 * @author: zforw
 * @date: 2022/02/23 9:01 上午
 * @project: FIleMgr
 * @description:
 */
public class FileStore {
    static final String DB_URL = "jdbc:mysql://localhost:3306/FILEMGR";
    static final String USERNAME = "root";
    static final String PASSWORD = "zfwixeon";
    public static String localFile;
    protected static String Status = "insert";
    protected static ArrayList<TreeMap<String, FileInfo>> fileIndex = new ArrayList<TreeMap<String, FileInfo>>(8);
    protected static String[] root;

    public FileStore() {
        for(int i = 0;i < 8;i++) {
            TreeMap<String, FileInfo> treeMap = new TreeMap<String, FileInfo>();
            fileIndex.add(treeMap);
        }
    }

    public Collection getIter(int index) {
        if(index == -1) {
            return null;
        }
        return fileIndex.get(index).values();
    }

    private static void updStat() throws IOException {
        OutputStream os = new FileOutputStream(localFile);
        PrintWriter pw = new PrintWriter(os);
        for (int i = 0; i < 8; i++) {
            pw.println(File.classOfFileCN[i] + "统计如下：");
            int cnt[] = {0, 0, 0, 0};
            Collection collection = fileIndex.get(i).values();
            for (Object o : collection) {
                FileInfo fileInfo = (FileInfo) o;
                cnt[fileInfo.type.getCode()]++;
            }
            for(int j = 0;j < 4;j++) {
                pw.println(FileType.getType(j).getStr() + ": " + cnt[j]);
            }
        }
        pw.close();
        os.close();
    }
    /**
     * @description: 
     * @param: [fileInfo, pathName]
     * @return: 
     */
    public void upload(FileInfo fileInfo, String pathName) throws SQLException, IOException, ParseException, ClassNotFoundException {
        FileInputStream in = new FileInputStream(pathName);
        //1.注册JDBC驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        //2.获取数据库连接
        Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        String sql = "insert into " + fileInfo.fileClass + " (name, type, file) values(?,?,?) ";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, fileInfo.name);
        statement.setInt(2, fileInfo.type.getCode());
        statement.setBinaryStream(3, in);
        statement.executeUpdate();//处理sql语句
        statement.close();
        connection.close();
        fileIndex.get(File.fileClass(fileInfo.fileClass)).put(fileInfo.name, fileInfo);
        updStat();
    }

    /**
     * @description: 
     * @param: [file, pathName]
     * @return: 
     */
    public void download(FileInfo file, String pathName) throws FileNotFoundException {
        try{
            //1.注册JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //2.获取数据库连接
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            //3.操作数据库
            Statement statement = connection.createStatement();
            //定义数据库语句
            String sql = "select * from " + File.classOfFileEN[File.fileClass(file.fileClass)];
            //执行数据库语句获取结果集
            ResultSet resultSet = statement.executeQuery(sql);
            Blob blob = null;
            while (resultSet.next()) {
                if(resultSet.getString("name").equals(file.name)) {
                    blob = resultSet.getBlob("file");
                    break;
                }
            }
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("downloaded " + File.classOfFileEN[File.fileClass(file.fileClass)] + "/" + file.type.getStr() + "/" + file.name);
            BufferedInputStream in = new BufferedInputStream(blob.getBinaryStream(), 1024);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(pathName + "/" + file.name));//位置可以自己改一下
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            in.close();
            out.close();
            //4.关闭结果集，数据库操作对象，数据库连接
        } catch (ClassNotFoundException | SQLException | IOException exception) {
            exception.printStackTrace();
        }
    }

    public void delete(FileInfo info) throws SQLException, ClassNotFoundException {
        try {
            //1.注册JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //2.获取数据库连接
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            //3.操作数据库
            Statement statement = connection.createStatement();
            //定义数据库语句
            String sql = "delete from " + File.classOfFileEN[File.fileClass(info.fileClass)] + " where name= '" + info.name + "'";
            System.out.println(sql);
            PreparedStatement pStmt = connection.prepareStatement(sql);
            fileIndex.get(File.fileClass(info.fileClass)).remove(info.name);
            pStmt.executeUpdate();
            pStmt.close();
            statement.close();
            connection.close();
            updStat();
        } catch (SQLException | ClassNotFoundException | IOException e) {

        }
    }

    /**
     * @description: 从数据库中加载数据索引到本地
     * @param: [fileName] 本地数据索引文件
     * @return:
     */
    public static void loadDatabase(String fileName) {
        try{
            OutputStream os = new FileOutputStream(fileName);
            PrintWriter pw = new PrintWriter(os);
            //1.注册JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //2.获取数据库连接
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            //3.操作数据库
            Statement statement = connection.createStatement();
            //定义数据库语句
            for(int i = 0;i < 8;i++) {
                //int cnt[] = {0, 0, 0, 0};
                String sql = "select name, type from " + File.classOfFileEN[i];

                //执行数据库语句获取结果集
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    System.out.println("class: " + File.classOfFileCN[i] +
                            ", type: " + FileType.getType(resultSet.getInt("type")).getStr() + ", name: " + resultSet.getString("name"));
                    //cnt[resultSet.getInt("type")]++;
                    fileIndex.get(i).put(resultSet.getString("name"),
                            new FileInfo(resultSet.getString("name"), File.classOfFileCN[i], FileType.getType(resultSet.getInt("type"))));
                }
                //for(int j = 0;j < 4;j++) {
                  //  pw.println(FileType.getType(j).getStr() + ": " + cnt[j]);
                //}
                resultSet.close();
            }
            System.out.println("数据库扫描完毕");
            updStat();
            pw.close();
            os.close();
            statement.close();
            connection.close();
            //4.关闭结果集，数据库操作对象，数据库连接
        } catch (ClassNotFoundException | IOException | SQLException exception) {
            exception.printStackTrace();
        }
    }
    /**
     * @description: 在本地文件/数据结构中查找是否存在文件，并从数据库加载文件
     *
     * @param: [fileName, type]
     * @return: 返回对应的文件数据
     */
    public static Blob loadDbFile(String fileName, String type) {
        try{
            if (!fileIndex.get(File.fileClass(type)).containsKey(fileName)) {
                return null;
            }
            //1.注册JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //2.获取数据库连接
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            //3.操作数据库
            Statement statement = connection.createStatement();
            //定义数据库语句
            String sql = "select * from " + type;
            //执行数据库语句获取结果集
            ResultSet resultSet = statement.executeQuery(sql);
            Blob blob = null;
            while (resultSet.next()) {
                if(resultSet.getString("name").equals(fileName)) {
                    blob = resultSet.getBlob("file");
                    break;
                }
            }
            resultSet.close();
            statement.close();
            connection.close();
            return blob;
            //4.关闭结果集，数据库操作对象，数据库连接
        } catch (ClassNotFoundException | SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }
    /**
     * @description: 从文件加载数据
     * @param: [fileName]
     * @return:
     */
    /*
    public static void loadFile(String fileName) throws IOException {
        java.io.File file = new File(fileName);
        if(!file.exists()) {
            file.createNewFile();
            Func.log("data file lost, create from database");
            root = new String[]{"su", "123"};//默认密码
            loadDatabase(fileName);
        }
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        try {
            Status = "init";
            root = Func.Split(Func.decrypt(br.readLine()));
            while ((line = br.readLine()) != null) {
                add(Func.decrypt(line));
            }
            Status = "insert";
            //先将Status记录为'初始化'，再记录为'添加'
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException | UnsupportedEncodingException exception) {
            //文件损坏则从数据库加载
            emp.clear();
            Func.log("data file damaged, create from database");
            loadDatabase(fileName);
            loadFile(fileName);
        }
        br.close();
    }
    */
    /**
     * @description: 将数据保存到文件并更新数据库
     * @param: [fileName]
     * @return:
     */
    /*
    public void saveFile(String fileName) throws IOException, SQLException, ClassNotFoundException {
        if (changelist.isEmpty()) {
            return;//如果没有改变就直接退出
        }
        OutputStream os = new FileOutputStream(fileName);
        PrintWriter pw = new PrintWriter(os);//覆盖原文件
        pw.println(Func.encrypt(root[0] + " " + root[1]));//输出超级用户账号密码
        for(Integer id : emp.keySet()) {
            pw.println(Func.encrypt(emp.get(id).getInfo()));//输出每个员工的信息
        }
        pw.close();
        os.close();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DB_url, username, password);
            Statement statement = connection.createStatement();
            String sql;
            // 根据changelist更新数据库
            for (String s : changelist) {
                System.out.println(s);
                String[] inst = Func.Split(s);
                // 添加员工
                if (inst[0].equals("insert")) {
                    sql = "insert into info(a,b,c,d,e,f,g)value (?,?,?,?,?,?,?)";
                    PreparedStatement pStmt = connection.prepareStatement(sql);
                    pStmt.setString(1, inst[1]);
                    pStmt.setString(2, inst[2]);
                    pStmt.setString(3, inst[3]);
                    pStmt.setString(4, inst[4]);
                    pStmt.setString(5, inst[5]);
                    pStmt.setString(6, inst[6]);
                    pStmt.setString(7, inst[7]);
                    pStmt.executeUpdate();
                    pStmt.close();
                } else if (inst[0].equals("update")) {
                    // 修改员工
                    sql = "update info set d=?, e=?, f=?, g=? where c = '" + inst[3] + "'";
                    PreparedStatement pStmt = connection.prepareStatement(sql);
                    pStmt.setString(1, inst[4]);
                    pStmt.setString(2, inst[5]);
                    pStmt.setString(3, inst[6]);
                    pStmt.setString(4, inst[7]);
                    pStmt.executeUpdate();
                    pStmt.close();
                } else {
                    // 删除员工
                    sql = "delete from info where c='" + inst[3] + "'";
                    PreparedStatement pStmt = connection.prepareStatement(sql);
                    pStmt.executeUpdate();
                    pStmt.close();
                }
            }
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException exception) {
            exception.printStackTrace();
        }
    }
     */

    /*
    统计
    SELECT
    列名
    count(1) AS 结果的列名
    FROM
    表
    GROUP BY
    列名
    */
}
