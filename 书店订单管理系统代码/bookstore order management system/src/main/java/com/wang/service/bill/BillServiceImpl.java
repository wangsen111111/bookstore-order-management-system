package com.wang.service.bill;


import com.wang.dao.BaseDao;
import com.wang.dao.bill.BillDao;
import com.wang.dao.bill.BillDaoImpl;
import com.wang.pojo.Bill;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BillServiceImpl implements BillService {

    private BillDao billDao;

    public BillServiceImpl() {
        billDao = new BillDaoImpl();
    }

    //添加订单
    public boolean add(Bill bill) {
        // TODO Auto-generated method stub
        boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务管理
            if (billDao.add(connection, bill) > 0)
                flag = true;
            connection.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                System.out.println("rollback==================");
                connection.rollback();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } finally {
            //在service层进行connection连接的关闭
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }
    //通过条件获取订单列表-模糊查询-billList
    public List<Bill> getBillList(Bill bill) {
        // TODO Auto-generated method stub
        Connection connection = null;
        List<Bill> billList = null;
        System.out.println("query productName ---- > " + bill.getProductName());
        System.out.println("query providerId ---- > " + bill.getProviderId());
        System.out.println("query isPayment ---- > " + bill.getIsPayment());

        try {
            connection = BaseDao.getConnection();
            billList = billDao.getBillList(connection, bill);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return billList;
    }
    //删除订单
    public boolean deleteBillById(String delId) {
        // TODO Auto-generated method stub
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if (billDao.deleteBillById(connection, delId) > 0)
                flag = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }
    //修改订单 ，查看订单信息
    public Bill getBillById(String id) {
        // TODO Auto-generated method stub
        Bill bill = null;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            bill = billDao.getBillById(connection, id);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            bill = null;
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return bill;
    }

    public boolean modify(Bill bill) {
        // TODO Auto-generated method stub
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if (billDao.modify(connection, bill) > 0)
                flag = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }
}
