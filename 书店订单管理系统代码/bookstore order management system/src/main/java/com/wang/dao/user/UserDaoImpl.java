package com.wang.dao.user;


import com.mysql.jdbc.StringUtils;
import com.wang.dao.BaseDao;
import com.wang.pojo.Role;
import com.wang.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * dao层抛出异常，让service层去捕获处理
 *
 * @author Administrator
 */
public class UserDaoImpl implements UserDao {

    /**
     * 得到要登录的用户
     *
     * @param connection
     * @param userCode
     * @return
     */
    public User getLoginUser(Connection connection, String userCode) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;
        try {
            if (null != connection) {
                String sql = "select * from `bookstore order management system_user` where userCode=?";
                Object[] params = {userCode};
                rs = BaseDao.execute(connection, pstm, rs, sql, params);

                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUserCode(rs.getString("userCode"));
                    user.setUserName(rs.getString("userName"));
                    user.setUserPassword(rs.getString("userPassword"));
                    user.setGender(rs.getInt("gender"));
                    user.setBirthday(rs.getDate("birthday"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setUserRole(rs.getInt("userRole"));
                    user.setCreatedBy(rs.getInt("createdBy"));
                    user.setCreationDate(rs.getTimestamp("creationDate"));
                    user.setModifyBy(rs.getInt("modifyBy"));
                    user.setModifyDate(rs.getTimestamp("modifyDate"));
                }
            }
            //connection不用关，可能存在业务，我们需要到事务层，就是调业务的时候，
            BaseDao.closeResource(null, pstm, rs);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    public int add(Connection connection, User user) throws Exception {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (null != connection) {
            String sql = "insert into `bookstore order management system_user` (userCode,userName,userPassword," +
                    "userRole,gender,birthday,phone,address,creationDate,createdBy) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {user.getUserCode(), user.getUserName(), user.getUserPassword(),
                    user.getUserRole(), user.getGender(), user.getBirthday(),
                    user.getPhone(), user.getAddress(), user.getCreationDate(), user.getCreatedBy()};
            updateRows = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }

    //获取用户列表-userList
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<User>();
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from `bookstore order management system_user` u,`bookstore order management system_role` r where u.userRole = r.id");
            List<Object> list = new ArrayList<Object>();
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                list.add("%" + userName + "%");
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }

            //在mysql中，分页使用 limit startIndex，pageSize ; 总数   DESC降序排列数据
            sql.append(" order by creationDate DESC limit ?,?");
            //当前页=(当前页-1)*页面大小
            currentPageNo = (currentPageNo - 1) * pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            Object[] params = list.toArray();
            System.out.println("sql ----> " + sql.toString());
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            while (rs.next()) {
                User _user = new User();
                _user.setId(rs.getInt("id"));
                _user.setUserCode(rs.getString("userCode"));
                _user.setUserName(rs.getString("userName"));
                _user.setGender(rs.getInt("gender"));
                _user.setBirthday(rs.getDate("birthday"));
                _user.setPhone(rs.getString("phone"));
                _user.setUserRole(rs.getInt("userRole"));
                _user.setUserRoleName(rs.getString("userRoleName"));
                userList.add(_user);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return userList;
    }

    //根据用户名或者角色查询用户总数
    public int getUserCount(Connection connection, String userName, int userRole) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from `bookstore order management system_user` u,`bookstore order management system_role` r where u.userRole = r.id");
            //List用来储存我们的参数
            List<Object> list = new ArrayList();
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                list.add("%" + userName + "%");
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }

            Object[] params = list.toArray();
            System.out.println("sql ----> " + sql.toString());
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            if (rs.next()) {
                //从结果集中获取最终的数量
                count = rs.getInt("count");
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return count;
    }
    //删除用户
    public int deleteUserById(Connection connection, Integer delId) throws Exception {
        PreparedStatement pstm = null;
        int flag = 0;
        if (null != connection) {
            String sql = "delete from `bookstore order management system_user` where id=?";
            Object[] params = {delId};
            flag = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return flag;
    }

    public User getUserById(Connection connection, String id) throws Exception {
        User user = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if (null != connection) {
            String sql = "select u.*,r.roleName as userRoleName from `bookstore order management system_user` u,`bookstore order management system_role` r where u.id=? and u.userRole = r.id";
            Object[] params = {id};
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
                user.setUserRoleName(rs.getString("userRoleName"));
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        System.out.println(user);
        return user;
    }

    public int modify(Connection connection, User user) throws Exception {
        int flag = 0;
        PreparedStatement pstm = null;
        if (null != connection) {
            String sql = "update `bookstore order management system_user` set userName=?," +
                    "gender=?,birthday=?,phone=?,address=?,userRole=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {user.getUserName(), user.getGender(), user.getBirthday(),
                    user.getPhone(), user.getAddress(), user.getUserRole(), user.getModifyBy(),
                    user.getModifyDate(), user.getId()};
            flag = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return flag;
    }

    /**
     * 修改当前用户密码
     *
     * @param connection
     * @param id
     * @param pwd
     * @return
     * @throws Exception
     */
    public int updatePwd(Connection connection, int id, String pwd) throws Exception {
        int flag = 0;
        PreparedStatement pstm = null;
        if (connection != null) {
            String sql = "update `bookstore order management system_user` set userPassword= ? where id = ?";
            Object[] params = {pwd, id};//参数封装成数组
            flag = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return flag;
    }
}
