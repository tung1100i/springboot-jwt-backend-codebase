package com.sapo.mock.techshop.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

@Slf4j
public class ErrorWatcher implements Watcher {

    private final ActivityManager activityManager;
    private final CuratorFramework client;
    private String activityName;

    public ErrorWatcher(ActivityManager activityManager, CuratorFramework client) {
        this.activityManager = activityManager;
        this.client = client;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDataChanged) {
            try {
                // Kiểm tra giá trị của counter
                int counterValue = this.getCounterValue();
                if (counterValue <= 3 && counterValue != 0) {
                    log.info("Retry times: " + counterValue);
                    activityManager.performActivity(activityName, true);
                } else if (counterValue == 4) {
                    log.info("Retry max, stop retry");
                    this.resetActivityAndCounter();
                }

                this.setWatcher(activityName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getCounterValue() throws Exception {
        DistributedAtomicInteger counter = new DistributedAtomicInteger(
                client,
                ActivityManager.ERROR_PATH + activityName,
                new RetryNTimes(0, 1000));
        return counter.get().postValue();
    }

    private void resetActivityAndCounter() throws Exception {
        String activityPath = ActivityManager.ACTIVITY_PATH + activityName;
        String errorPath = ActivityManager.ERROR_PATH + activityName;

        client.delete().forPath(activityPath);
        activityManager.resetCounter(errorPath);
    }

    public void setWatcher(String jobName) throws Exception {
        this.activityName = jobName;
        client
                .getData()
                .usingWatcher(this)
                .forPath(ActivityManager.ERROR_PATH + activityName);
    }
}

