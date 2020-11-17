package cc.rexfa.java.media;

import cc.rexfa.java.media.cache.ServiceCache;
import cc.rexfa.java.media.pojo.*;
import cc.rexfa.java.media.util.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    // 存放任务 线程
    //public static Map<String, FFThreadManagement.FFRunnable> jobMap = new HashMap<String, FFThreadManagement.FFRunnable>();
    public static void main(String[] args) throws InterruptedException {
	// write your code here
        log.info("Main Start.");
        ServiceCache.SERVICESTARTTIME = new Date().getTime();
        //test

        // 生成token
        //String token = UUID.randomUUID().toString();

        CameraInfo cameraInfo0 = new CameraInfo();
        cameraInfo0.setIp("192.168.1.64");
        cameraInfo0.setChannel("main");
        cameraInfo0.setPort("554");
        cameraInfo0.setUsername("admin");
        cameraInfo0.setPassword("12345qwert");
        cameraInfo0.setRtsp("rtsp://admin:12345qwert@192.168.1.64:554/h264/ch1/main/av_stream");
        cameraInfo0.setUrl("rtsp://admin:12345qwert@192.168.1.64:554/h264/ch1/main/av_stream");
        log.info("Camer URL "+ cameraInfo0.getUrl());

        CameraInfo cameraInfo1 = new CameraInfo();
        cameraInfo1.setIp("192.168.1.64");
        cameraInfo1.setChannel("sub");
        cameraInfo1.setPort("554");
        cameraInfo1.setUsername("admin");
        cameraInfo1.setPassword("12345qwert");
        cameraInfo1.setRtsp("rtsp://admin:12345qwert@192.168.1.64:554/h264/ch1/sub/av_stream");
        cameraInfo1.setUrl("rtsp://admin:12345qwert@192.168.1.64:554/h264/ch1/sub/av_stream");
        log.info("Camer URL "+ cameraInfo1.getUrl());

        CameraInfo cameraInfo2 = new CameraInfo();
        cameraInfo2.setIp("192.168.1.64");
        cameraInfo2.setChannel("sub");
        cameraInfo2.setPort("554");
        cameraInfo2.setUsername("admin");
        cameraInfo2.setPassword("12345qwert");
        cameraInfo2.setRtsp("rtsp://admin:12345qwert@192.168.1.64:554/h264/ch1/main/av_stream");
        cameraInfo2.setUrl("rtsp://admin:12345qwert@192.168.1.64:554/h264/ch1/main/av_stream");
        log.info("Camer URL "+ cameraInfo2.getUrl());



        ServerInfo serverInfo0 = new ServerInfo();
        serverInfo0.setIp("127.0.0.1");
        serverInfo0.setPassword("");
        serverInfo0.setPort("1935");
        serverInfo0.setPassword("");
        serverInfo0.setUsername("");
        serverInfo0.setServiceappname("live");
        serverInfo0.setRtmp("rtmp://127.0.0.1/live/");
        serverInfo0.setUrl("rtmp://127.0.0.1/live/");

        ServerInfo serverInfo1 = new ServerInfo();
        serverInfo1.setIp("127.0.0.1");
        serverInfo1.setPassword("");
        serverInfo1.setPort("1935");
        serverInfo1.setPassword("");
        serverInfo1.setUsername("");
        serverInfo1.setServiceappname("live");
        serverInfo1.setRtmp("rtmp://127.0.0.1/live/");
        serverInfo1.setUrl("rtmp://127.0.0.1/live/");

        ServerInfo serverInfo2 = new ServerInfo();
        serverInfo2.setIp("127.0.0.1");
        serverInfo2.setPassword("");
        serverInfo2.setPort("1935");
        serverInfo2.setPassword("");
        serverInfo2.setUsername("");
        serverInfo2.setServiceappname("live");
        serverInfo2.setRtmp("rtmp://127.0.0.1/live/");
        serverInfo2.setUrl("rtmp://127.0.0.1/live/");

        FFSteamProcessor ffSteamProcessor = new FFSteamProcessor();
        String token0 = null;
        try {
            token0 = ffSteamProcessor.initializeJob(cameraInfo0,serverInfo0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Get Token0 "+token0);
        log.info("Server URL0 "+ serverInfo0.getUrl());
        Thread.sleep(500);
        String token1 = null;
        try {
            token1 = ffSteamProcessor.initializeJob(cameraInfo1,serverInfo1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Get Token "+token1);
        log.info("Server URL1 "+ serverInfo1.getUrl());
        Thread.sleep(500);
        String token2 = null;
        try {
            token2 = ffSteamProcessor.initializeJob(cameraInfo2,serverInfo2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Get Token "+token2);
        log.info("Server URL2 "+ serverInfo2.getUrl());
        Thread.sleep(500);
        //System.console("Server URL "+ serverInfo.getUrl());
        //FFThreadManagement.FFRunnable job = new FFThreadManagement.FFRunnable(cameraInfo,serverInfo);
        //FFThreadManagement.FFRunnable.executorService.execute(job);
        //jobMap.put(token, job);

    }
    @PreDestroy
    public void destory(){
        // 关闭线程池
        FFThreadManagement.FFRunnable.executorService.shutdownNow();
        // 销毁定时器
        TimerJobUtil.timer.cancel();
    }
}
