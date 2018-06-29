package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisPoolUtil {
    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    //设置过期时间，中间的参数是秒
    public static String setEX(String key, String value, int time) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, time, value);
        } catch (Exception e) {
            log.error("set key:{} time:{} value:{} error", key, time, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    //设置多少秒后过期
    public static Long expire(String key, int time) {
        Jedis jedis = null;
        Long expire = null;

        try {
            jedis = RedisPool.getJedis();
            expire = jedis.expire(key,time);
        } catch (Exception e) {
            log.error("expire error key:{} time:{}", key, time, e);
            RedisPool.returnBrokenResource(jedis);
            return expire;
        }
        RedisPool.returnResource(jedis);
        return expire;
    }

    public static Long delete(String key) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del error key:{}", key, e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    @Test
    public void JedisTest() {
        String s = RedisShardedPoolUtil.setEX("name", "zs", 10);
        System.out.println(s);
    }
}
