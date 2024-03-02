package com.sapo.mock.techshop.service;

import java.io.IOException;

public interface TestService {
    void test(String serviceName);

    void getIndex(String keyword) throws IOException;
}
