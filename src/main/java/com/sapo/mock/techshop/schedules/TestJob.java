package com.sapo.mock.techshop.schedules;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TestJob {

    @Autowired
    private CuratorFramework client;
    private final String lockPath = "/schedule/";

    @Scheduled(fixedRate = 30000, initialDelay = 10000)
    public void scheduleTask() {
        InterProcessSemaphoreMutex sharedLock = new InterProcessSemaphoreMutex(client, lockPath + "scheduleTask");
//        LeaderSelector leaderSelector = this.getLeaderSelector("scheduleTask");
        try {
//            if (this.acquiredLock("scheduleTask")) {
            sharedLock.acquire();
            System.out.println("Running task");
//            releaseLock("scheduleTask");
            sharedLock.release();
        } catch (Exception e) {
            System.out.println("Loi khi chay");
        }

    }

    private boolean acquiredLock(String job) {
        try {
            LeaderSelector leaderSelector = new LeaderSelector(client,
                    lockPath + job,
                    new LeaderSelectorListener() {
                        @Override
                        public void stateChanged(
                                CuratorFramework client,
                                ConnectionState newState) {
                        }

                        @Override
                        public void takeLeadership(
                                CuratorFramework client) throws Exception {
                        }
                    });
            leaderSelector.start();
            return true;
        } catch (Exception e) {
            System.out.println("------");
            return false;
        }
    }

    private void releaseLock(String job) throws Exception {
        client.delete().forPath(lockPath + job);
    }

    private LeaderSelector getLeaderSelector(String job) {
        return new LeaderSelector(client,
                lockPath + job,
                new LeaderSelectorListener() {
                    @Override
                    public void stateChanged(
                            CuratorFramework client1,
                            ConnectionState newState) {
                    }

                    @Override
                    public void takeLeadership(
                            CuratorFramework client1) throws Exception {
                    }
                });
    }
}
