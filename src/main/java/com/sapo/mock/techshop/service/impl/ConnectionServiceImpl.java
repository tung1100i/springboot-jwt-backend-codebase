package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.service.ConnectionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    //Khai báo thông tin kết nối database
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public Connection getConnection() {
        Connection connection = null;
        try {
            //tạo kết nối database
            connection = DriverManager.getConnection(url, username, password);

//            //tạo statement để thực thi câu lệnh sql
//            Statement statement = connection.createStatement();
//            //Thực thi câu lệnh sql
//            statement.executeQuery("SELECT 1");
//            //nếu không có lỗi thì in ra kết nối thành công
//            if (statement.getResultSet().next()) {
//                System.out.println("Kết nối thành công " + statement.getResultSet().getInt(1));
//            }
        } catch (SQLException e) {
            //nếu có lỗi thì in ra kết nối thất bại
            System.out.println("Kết nối thất bại");
            e.printStackTrace();
        }
        //trả về kết nối
        return connection;
    }
}
