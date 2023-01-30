package com.srlab.basic.serverside.schedulers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    private final Logger LOG = LogManager.getLogger(Scheduler.class);

//    @Scheduled(cron = "0 0/1 * * * ?")	//every minute process
    private void scheduleProcess() {
        try {
            //content
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
