package cc.rexfa.java.media.util;
import java.util.Set;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.rexfa.java.media.cache.ServiceCache;

public class TimerJobUtil{
    public static Timer timer;
    private final static Logger logger = LoggerFactory.getLogger(TimerJobUtil.class);
    public void StartTimer() throws Exception {
        // 超过5分钟，结束推流
        timer = new Timer("automaticMaintenanceTimer");
        timer.schedule(new TimerTask() {
            public void run() {
                logger.info("******   执行定时任务       BEGIN   ******");
                // 管理缓存
                if (null != ServiceCache.STREAMMAP && 0 != ServiceCache.STREAMMAP.size()) {
                    Set<String> keys = ServiceCache.STREAMMAP.keySet();
                    logger.info("****** 已经启动的封装线程数量"+keys.size() +"  ******");
                    for (String key : keys) {
                        try {
                            // 最后打开时间
                            long openTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .parse(ServiceCache.STREAMMAP.get(key).getOpenTime()).getTime();
                            // 当前系统时间
                            long newTime = new Date().getTime();
                            // 如果通道使用人数为0，则关闭推流
                            if (ServiceCache.STREAMMAP.get(key).getCount() == 0) {
                                // 结束线程
                                FFThreadIndexUtil.FFThreadMAP.get(key).setInterrupted();
                                // 清除缓存
                                ServiceCache.STREAMMAP.remove(key);
                                FFThreadIndexUtil.FFThreadMAP.remove(key);
                            }/*
                            else if ((newTime - openTime) / 1000 / 60 > Integer.valueOf(5)) {
                                FFThreadIndexUtil.FFThreadMAP.get(key).setInterrupted();
                                logger.debug("[定时任务：]  结束： " + ServiceCache.STREAMMAP.get(key).getRtsp() + "  推流任务！");
                                FFThreadIndexUtil.FFThreadMAP.remove(key);
                                ServiceCache.STREAMMAP.remove(key);
                            }*/
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                logger.info("******   执行定时任务       END     ******");
            }
        }, 10, 1000 * 60);
    }
}
