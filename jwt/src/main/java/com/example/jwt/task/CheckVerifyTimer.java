package com.example.jwt.task;


import com.example.jwt.status.UserCache;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

@Slf4j
public class CheckVerifyTimer extends TimerTask {

    public CheckVerifyTimer() {

    }

    public void run() {
        Map<String, Date> map = UserCache.getInstance().getUserMap();

        Iterator<Map.Entry<String, Date>> itr = map.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, Date> entry = itr.next();
            Date date = new Date();
            if (entry.getValue().before(date)) {
                System.out.println("Key : " + entry.getKey() + " Removed.");
                itr.remove();  // Call Iterator's remove method.
            }
        }
        UserCache.getInstance().setUserMap(map);
        log.info("map--->" + map.toString());
    }
}