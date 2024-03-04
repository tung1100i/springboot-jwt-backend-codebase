package com.sapo.mock.techshop.service;

import com.sapo.mock.techshop.model.Comment;

import java.io.IOException;
import java.util.List;

public interface TestService {
    void test(String serviceName);

    void getIndex(String keyword) throws IOException;

    void pushDocument() throws IOException;
}
