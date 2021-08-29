package com.wang.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 操作数据库的公共类
 */
public class BaseDao {

    private static String driver;
    private static String url;
    private static String username;
    private static String password;


    //静态代码块,在类加载的时候就初始化了
    static {
        Properties params = new Properties();
        String configFile = "db.properties";
        //通过类加载器读取对应的资源
        InputStream is = com.wang.dao.BaseDao.class.getClassLoader().getResourceAsStream(configFile);
        try {
            params.load(is);//加载流
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取
        driver = params.getProperty("driver");
        url = params.getProperty("url");
        username = params.getProperty("username");
        password = params.getProperty("password");
    }


    /**
     * 获取数据库连接
     * @return
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(driver);//固定写法，加载驱动
            //连接成功，数据库对象,connection代表数据库
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 查询数据库的公共操作
     *
     * @param connection
     * @param pstm
     * @param rs
     * @param sql
     * @param params
     * @return
     */
    public static ResultSet execute(Connection connection, PreparedStatement pstm, ResultSet rs, String sql, Object[] params) {
        try {
            //预编译sql
            pstm = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                //setObject占位符从1开始
                pstm.setObject(i + 1, params[i]);
            }
            rs = pstm.executeQuery();//执行SQL
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    /**
     * 编写增删改公共方法
     * @param connection
     * @param pstm
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public static int execute(Connection connection, PreparedStatement pstm, String sql, Object[] params) {
        //预编译的sql，在后面直接执行就可以了
        int updateRows = 0;
        try {
            pstm = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstm.setObject(i + 1, params[i]);
            }
            updateRows = pstm.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return updateRows;
    }

    /**
     * 释放资源
     * @param connection
     * @param pstm
     * @param rs
     * @return
     */
    public static boolean closeResource(Connection connection, PreparedStatement pstm, ResultSet rs) {
        boolean flag = true;
        if (rs != null) {
            try {
                rs.close();
                rs = null;//让GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;//关闭出现错误
            }
        }
        if (pstm != null) {
            try {
                pstm.close();
                pstm = null;//GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if (connection != null) {
            try {
                connection.close();
                connection = null;//GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        //都释放成功，返回true
        return flag;
    }
}
