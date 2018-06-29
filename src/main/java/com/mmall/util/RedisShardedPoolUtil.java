package com.mmall.util;

import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisShardedPoolUtil {
    public static String set(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String getSet(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.getSet(key, value);
        } catch (Exception e) {
            log.error("getSet key:{} value:{} error", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    //设置过期时间，中间的参数是秒
    public static String setEX(String key, String value, int time) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setex(key, time, value);
        } catch (Exception e) {
            log.error("set key:{} time:{} value:{} error", key, time, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    //设置多少秒后过期
    public static Long expire(String key, int time) {
        ShardedJedis jedis = null;
        Long expire = null;

        try {
            jedis = RedisShardedPool.getJedis();
            expire = jedis.expire(key,time);
        } catch (Exception e) {
            log.error("expire error key:{} time:{}", key, time, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return expire;
        }
        RedisShardedPool.returnResource(jedis);
        return expire;
    }

    public static Long delete(String key) {
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del error key:{}", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long setnx(String key, String value) {
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setnx(key, value);
        } catch (Exception e) {
            log.error("setnx key:{} value:{} error", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    @Test
    public void JedisTest() {
        String s = RedisPoolUtil.setEX("name", "zs", 10);
        System.out.println(s);
    }
}
