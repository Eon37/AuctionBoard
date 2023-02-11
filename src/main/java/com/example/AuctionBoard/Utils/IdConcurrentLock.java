package com.example.AuctionBoard.Utils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IdConcurrentLock {
    public static final String BET_LOCK = "BET_LOCK_";

    private static final Set<String> usedKeys = ConcurrentHashMap.newKeySet();

    public static boolean tryLock(String key) {
        return usedKeys.add(key);
    }

    public static void unlock(String key) {
        usedKeys.remove(key);
    }
}
