package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedissonManager;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedissonManager redissonManager;

    //@PreDestroy
    public void del(){
        RedisShardedPoolUtil.delete(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
    }
    //@Scheduled(cron = "0 */1 * * * ?")
    public void colseOrderv1(){
        log.info("关闭订单开始");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        //orderService.closeOrder(hour);
        log.info("关闭订单结束");
    }
    //@Scheduled(cron = "0 */1 * * * ?")
    public void colseOrderv2(){
        log.info("关闭订单开始v2");
        Long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout", "50000"));

        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeout));

        if (setnxResult!=null&&setnxResult.intValue()==1){
            close();
        }else{
            log.info("没有拿到分布式锁");
        }
    }
    //@Scheduled(cron = "0 */1 * * * ?")
    public void colseOrderv3(){
        log.info("关闭订单v3版本开始");
        Long lockTimeout = System.currentTimeMillis()+Long.valueOf(PropertiesUtil.getProperty("lock.timeout","50000"));

        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()+lockTimeout));

        if (setnxResult!=null&&setnxResult.intValue()==1){
            //close();
        }else{
            String maxLockTime = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

            if (maxLockTime!=null&&System.currentTimeMillis()>Long.valueOf(maxLockTime)){
                //已经超时了
                String previousTime = RedisShardedPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(lockTimeout));
                //getSet方法可能会导致两种情况
                //1.key不存在 返回的是nil 等于null 2.key存在 返回旧的值
                if (previousTime==null||previousTime!=null&&StringUtils.equals(maxLockTime,previousTime)){
                    //说明拿到了锁
                    //close();
                }else{
                    log.info("锁已经被其它线程拿到并且重新设置,旧时间和新时间不一致,本次失败");
                }

            }else{
                log.info("锁没有超时");
            }
        }

    }
    //@Scheduled(cron = "0 */1 * * * ?")
    public void colseOrderv4(){
        RLock lock = redissonManager.getRedisson().getLock(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        boolean flag = false;
        try {
            if(flag = lock.tryLock(2,50,TimeUnit.SECONDS)){
                log.info("获取到了锁,名称是{},当前线程是{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
                int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
                //orderService.closeOrder(hour);
            }
        } catch (InterruptedException e) {
            log.error("redission获取锁发生了异常");
            e.printStackTrace();
        }finally {
            if(!flag){
                return;
            }
            lock.unlock();
            log.info("Redission释放锁");
        }
    }

    private void close(){
        log.info("获取{},当前的线程是{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        RedisShardedPoolUtil.expire(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,50);
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        //orderService.closeOrder(hour);
        RedisShardedPoolUtil.delete(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("============================");
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        int i  = (int)5000L;
        System.out.println(System.currentTimeMillis()+i);
    }
}
