package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.service.ConnectionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class ConnectionServiceImpl implements ConnectionService {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public Connection getConnection () {
        Connection connection = null;
        try {
            // Kết nối đến cơ sở dữ liệu PostgreSQL
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Kết nối đến cơ sở dữ liệu thất bại.");
            e.printStackTrace();
        }
        return connection;
    }
}
