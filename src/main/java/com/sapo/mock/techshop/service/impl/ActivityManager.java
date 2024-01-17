package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.schedules.TestJob;
import com.sapo.mock.techshop.service.TestService;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityManager {

    private final CuratorFramework client;
    private final TestService testService;

    public static final String ACTIVITY_PATH = "/schedule/";
    public static final String ERROR_PATH = "/error/";

    public void performActivity(String activityName) throws Exception {
        String activityPath = ACTIVITY_PATH + activityName;
        String errorPath = ERROR_PATH + activityName;

        this.createPathIfNeeded(activityPath);

        try {
            switch (activityName) {
                case "scheduleTask":
                    testService.test("1");
            }
            client.delete().forPath(activityPath);
            this.resetCounter(errorPath);
        } catch (Exception e) {
            // Tăng counter nếu xảy ra lỗi
            this.incrementErrorCounter(errorPath);
        }
    }

    public void createPathIfNeeded(String path) throws Exception {
        if (client.checkExists().forPath(path) == null) {
            client.create().creatingParentsIfNeeded().forPath(path);
        }
    }

    public void incrementErrorCounter(String errorPath) throws Exception {
        DistributedAtomicInteger counter = new DistributedAtomicInteger(
                client, errorPath, new RetryNTimes(10, 1000));
        counter.increment();
    }

    public void resetCounter(String errorPath) throws Exception {
        DistributedAtomicInteger counter = new DistributedAtomicInteger(
                client, errorPath, new RetryNTimes(10, 1000));
        counter.forceSet(0);
    }
}
