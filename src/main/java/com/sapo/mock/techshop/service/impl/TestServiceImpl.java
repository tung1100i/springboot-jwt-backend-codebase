package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Override
    public void test(String serviceName) {
        System.out.println("chay test " + serviceName);
    }
}
