package usyd.mingyi.animalcare.utils;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.TimeUnit;

public class Verification {
    private static ExpiringMap<Object, String> CodeCache = ExpiringMap.builder()
            .maxSize(200)
            .expiration(5, TimeUnit.MINUTES)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .variableExpiration()
            .build();
    public static void putCode(String userName,String code){
        CodeCache.put(userName,code);
    }
    public static boolean hasUser(String userName){
        return CodeCache.containsKey(userName);
    }
    public static String getCode(String code){
        return CodeCache.get(code);
    }

}
