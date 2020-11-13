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
    public static void main(String[] args) {
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
        //cameraInfo.setToken(token);
        log.info("Camer URL "+ cameraInfo0.getUrl());

        CameraInfo cameraInfo1 = new CameraInfo();

        cameraInfo1.setIp("192.168.1.64");
        cameraInfo1.setChannel("sub");
        cameraInfo1.setPort("554");
        cameraInfo1.setUsername("admin");
        cameraInfo1.setPassword("12345qwert");
        cameraInfo1.setRtsp("rtsp://admin:12345qwert@192.168.1.64:554/h264/ch1/sub/av_stream");
        cameraInfo1.setUrl("rtsp://admin:12345qwert@192.168.1.64:554/h264/ch1/sub/av_stream");
        //cameraInfo.setToken(token);
        log.info("Camer URL "+ cameraInfo1.getUrl());



        ServerInfo serverInfo0 = new ServerInfo();
        serverInfo0.setIp("127.0.0.1");
        serverInfo0.setPassword("");
        serverInfo0.setPort("1935");
        serverInfo0.setPassword("");
        serverInfo0.setUsername("");
        serverInfo0.setServiceappname("live");
        serverInfo0.setRtmp("rtmp://127.0.0.1/live/");
        serverInfo0.setUrl("rtmp://127.0.0.1/live/");
        //serverInfo.setSteamToken(token);

        ServerInfo serverInfo1 = new ServerInfo();
        serverInfo1.setIp("127.0.0.1");
        serverInfo1.setPassword("");
        serverInfo1.setPort("1935");
        serverInfo1.setPassword("");
        serverInfo1.setUsername("");
        serverInfo1.setServiceappname("live");
        serverInfo1.setRtmp("rtmp://127.0.0.1/live/");
        serverInfo1.setUrl("rtmp://127.0.0.1/live/");
        FFSteamProcessor ffSteamProcessor = new FFSteamProcessor();
        String token0 = null;
        try {
            token0 = ffSteamProcessor.initializeJob(cameraInfo0,serverInfo0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Get Token0 "+token0);
        log.info("Server URL0 "+ serverInfo0.getUrl());
        String token1 = null;
        try {
            token1 = ffSteamProcessor.initializeJob(cameraInfo1,serverInfo1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Get Token "+token1);
        log.info("Server URL1 "+ serverInfo1.getUrl());

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
