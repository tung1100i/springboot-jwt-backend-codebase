package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.service.ConnectionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
@Log4j2
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
            log.info("Kết nối đến cơ sở dữ liệu thất bại.");
            e.printStackTrace();
        }
        return connection;
    }
}
