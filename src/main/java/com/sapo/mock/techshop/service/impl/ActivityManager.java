//package com.sapo.mock.techshop.service.impl;
//
//import com.sapo.mock.techshop.service.TestService;
//import lombok.RequiredArgsConstructor;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
//import org.apache.curator.retry.RetryNTimes;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ActivityManager {
//
//    private final CuratorFramework client;
//    private final TestService testService;
//
//    public static final String ACTIVITY_PATH = "/schedule/";
//    public static final String ERROR_PATH = "/error/";
//
//    public void performActivity(String activityName, boolean retry) throws Exception {
//        String activityPath = ACTIVITY_PATH + activityName;
//        String errorPath = ERROR_PATH + activityName;
//
//        try {
//            if (retry) {
//                client.delete().forPath(activityPath);
//            }
//        } catch (Exception e) {
//            System.out.println("Retry on another node");
//        }
//
//        try {
//            if (this.createPathIfNeeded(activityPath)) {
//                switch (activityName) {
//                    case "scheduleTask":
//                        testService.test("1");
//                }
//                client.delete().forPath(activityPath);
//                this.resetCounter(errorPath);
//            } else {
//                System.out.println("Job runing on another node");
//            }
//        } catch (Exception e) {
//            // Tăng counter nếu xảy ra lỗi
//            this.incrementErrorCounter(errorPath);
//        }
//    }
//
//    public boolean createPathIfNeeded(String path) {
//        try {
//            client.create().creatingParentsIfNeeded().forPath(path);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public void incrementErrorCounter(String errorPath) throws Exception {
//        DistributedAtomicInteger counter = new DistributedAtomicInteger(
//                client, errorPath, new RetryNTimes(0, 1000));
//        counter.increment();
//    }
//
//    public void resetCounter(String errorPath) throws Exception {
//        DistributedAtomicInteger counter = new DistributedAtomicInteger(
//                client, errorPath, new RetryNTimes(0, 1000));
//        counter.forceSet(0);
//    }
//}
