package com.example.AuctionBoard.Utils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NoticeIdConcurrentLock {
    private static final Set<Long> usedKeys = ConcurrentHashMap.newKeySet();

    public static boolean tryLock(Long key) {
        return usedKeys.add(key);
    }

    public static void unlock(Long key) {
        usedKeys.remove(key);
    }
}
