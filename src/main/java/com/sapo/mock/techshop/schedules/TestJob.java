package com.sapo.mock.techshop.schedules;


import com.sapo.mock.techshop.service.TestService;
import com.sapo.mock.techshop.service.impl.ActivityManager;
import com.sapo.mock.techshop.service.impl.ErrorWatcher;
import com.sapo.mock.techshop.service.impl.TestServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestJob {

    private final TestService testService;
    private final CuratorFramework curatorFramework;

    //   @Scheduled(fixedRate = 30000, initialDelay = 10000)
    @Scheduled(cron = "0,15,30,45 * * * * *")
    public void scheduleTask() throws Exception {
        ActivityManager activityManager = new ActivityManager(curatorFramework, testService);
        ErrorWatcher watcher = new ErrorWatcher(activityManager, curatorFramework);

        // Đặt watcher cho counter
        watcher.setWatcher("scheduleTask");
        activityManager.performActivity("scheduleTask", false);
    }
}