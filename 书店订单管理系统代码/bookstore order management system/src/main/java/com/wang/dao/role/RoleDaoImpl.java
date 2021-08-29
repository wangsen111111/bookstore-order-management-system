package com.wang.dao.role;


import com.wang.dao.BaseDao;
import com.wang.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao {

    /**
     * 获取角色列表
     *
     * @param connection
     * @return
     * @throws Exception
     */
    public List<Role> getRoleList(Connection connection) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Role> roleList = new ArrayList();
        if (connection != null) {
            String sql = "select * from `bookstore order management system_role`";
            Object[] params = {};
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            while (rs.next()) {
                Role _role = new Role();
                _role.setId(rs.getInt("id"));
                _role.setRoleCode(rs.getString("roleCode"));
                _role.setRoleName(rs.getString("roleName"));
                roleList.add(_role);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return roleList;
    }


}
