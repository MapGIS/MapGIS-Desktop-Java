package com.zondy.mapgis.edit.util;

import java.util.concurrent.*;

/**
 * @author CR
 * @file ThreadPools.java
 * @brief 线程池
 * @create 2020-05-20.
 */
public final class ThreadPools {
    public static final int DEFAULT_POOL_SIZE = 50;
    private static final String DEFAULT_POOL_GROUP_NAME = "MAPGIS_DEFAULT_POOL";
    private static final ThreadPoolExecutor defaultPool;

    public ThreadPools() {
    }

    public static ExecutorService getDefaultExecutorService() {
        return defaultPool;
    }

    static {
        defaultPool = new ThreadPoolExecutor(DEFAULT_POOL_SIZE, DEFAULT_POOL_SIZE, 3L, TimeUnit.SECONDS, new LinkedBlockingQueue()) {
            @Override
            public void execute(Runnable command) {
                super.execute(command);
                if (DEFAULT_POOL_GROUP_NAME.equals(Thread.currentThread().getThreadGroup().getName())) {
                    boolean removed = this.remove(command);
                    if (removed) {
                        command.run();
                    }
                }
            }
        };
        defaultPool.allowCoreThreadTimeOut(true);
        defaultPool.setThreadFactory(new ThreadPools.DefaultPoolThreadFactory());
    }

    private static final class DefaultPoolThreadFactory implements ThreadFactory {
        private static final ThreadGroup DEFAULT_POOL_GROUP = new ThreadGroup(DEFAULT_POOL_GROUP_NAME);

        private DefaultPoolThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(DEFAULT_POOL_GROUP, r);
        }
    }
}
