package com.example.jwt.status;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserCache {
    private static UserCache instance = null;
    public static final int  USER_LOGIN_NUM = 2;
    private UserCache() {

    }

    public static UserCache getInstance() {
        if (instance == null) {
            synchronized (UserCache.class) {
                if (instance == null) {
                    instance = new UserCache();
                }
            }
        }
        return instance;
    }

    private Map<String, Date> userMap = new HashMap<>();

    public Map<String, Date> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, Date> userMap) {
        UserCache.getInstance().userMap = userMap;
    }

}
