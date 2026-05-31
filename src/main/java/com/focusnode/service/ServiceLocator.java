package com.focusnode.service;

import com.focusnode.repository.DatabaseManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceLocator {
    private static AppDataService appDataService;
    private static final ExecutorService asyncExecutor = Executors.newVirtualThreadPerTaskExecutor();

    static {
        // Initialize database and switch to SQL Server implementation
        DatabaseManager.initialize();
        appDataService = new SqlAppDataService();
    }

    public static AppDataService getAppDataService() {
        return appDataService;
    }

    public static void setAppDataService(AppDataService service) {
        appDataService = service;
    }

    public static ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }
}
