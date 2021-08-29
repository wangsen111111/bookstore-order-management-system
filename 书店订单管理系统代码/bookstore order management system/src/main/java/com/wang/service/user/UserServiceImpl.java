package com.wang.service.user;

import com.wang.dao.BaseDao;
import com.wang.dao.user.UserDao;
import com.wang.dao.user.UserDaoImpl;
import com.wang.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * service层捕获异常，进行事务处理
 * 事务处理：调用不同dao的多个方法，必须使用同一个connection（connection作为参数传递）
 * 事务完成之后，需要在service层进行connection的关闭，在dao层关闭（PreparedStatement和ResultSet对象）
 *
 * @author Administrator
 */
public class UserServiceImpl implements UserService {
    //业务层调用Dao层，所以我们要引入Dao层
    private UserDao userDao;
    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    /**
     * 用户登录实现
     *
     * @param userCode
     * @param userPassword
     * @return
     */
    public User login(String userCode, String userPassword) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            //通过业务层调用对应的具体数据库操作
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }

        //匹配密码
        if (null != user) {
            if (!user.getUserPassword().equals(userPassword)) {
                user = null;
            }
        }
        return user;
    }
    @Test
    public void loginTest(){
        UserService userService=new UserServiceImpl();
        User admin=userService.login("admin","1234567");
        System.out.println(admin.getUserPassword());
    }
    // 根据条件查询用户列表
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> userList = null;
        System.out.println("queryUserName:"+queryUserName);
        System.out.println("queryUserRole:"+queryUserRole);
        System.out.println("pageSize:"+pageSize);
        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, queryUserName, queryUserRole, currentPageNo, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return userList;
    }
    //查询记录数
    public int getUserCount(String queryUserName, int queryUserRole) {
        Connection connection = null;
        int count = 0;
        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, queryUserName, queryUserRole);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return count;
    }
    @Test
    public void test(){
        UserServiceImpl userService=new UserServiceImpl();
        int userCount=userService.getUserCount(null,1);
        System.out.println(userCount);
    }

    public User selectUserCodeExist(String userCode) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }
    //删除用户
    public boolean deleteUserById(Integer delId) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if (userDao.deleteUserById(connection, delId) > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }
    //修改用户
    public User getUserById(String id) {
        // TODO Auto-generated method stub
        User user = null;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getUserById(connection, id);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            user = null;
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    public boolean updatePwd(int id, String pwd) {
        boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            //修改密码
            if (userDao.updatePwd(connection, id, pwd) > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    public boolean modify(User user) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if (userDao.modify(connection, user) > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }
    //添加用户
    public boolean add(User user) {
        boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务管理
            int updateRows = userDao.add(connection, user);
            connection.commit();
            if (updateRows > 0) {
                flag = true;
                System.out.println("add success!");
            } else {
                System.out.println("add failed!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                System.out.println("rollback==================");
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            //在service层进行connection连接的关闭
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }
}
