package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedissonManager {
    private Config config = new Config();

    private Redisson redisson = null;

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port","6379"));

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port","6380"));


    public Redisson getRedisson() {
        return redisson;
    }

    @PostConstruct
    public void init(){
        try {
            config.useSingleServer().setAddress(new StringBuilder(redis1Ip).append(":").append(redis1Port).toString());
            redisson = (Redisson) Redisson.create(config);
            log.info("Redission初始化成功");
        } catch (Exception e) {
            log.error("Redission初始化失败");
            e.printStackTrace();
        }
    }
}
