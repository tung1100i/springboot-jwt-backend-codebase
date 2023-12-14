package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

@Service
public class PropertyServiceImpl {

    @Autowired
    private ConnectionService connectionService;

    public void test() throws SQLException {
        Connection connection = connectionService.getConnection();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("Select 1;");
        if(resultSet.next()) {
            System.out.println("Kết nối đến cơ sở dữ liệu thành công --- " + resultSet.getInt(1));
        }

    }

}
