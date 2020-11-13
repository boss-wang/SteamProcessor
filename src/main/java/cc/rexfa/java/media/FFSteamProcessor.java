package cc.rexfa.java.media;
import cc.rexfa.java.media.cache.ServiceCache;
import cc.rexfa.java.media.pojo.*;
import cc.rexfa.java.media.util.*;

import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PreDestroy;

public class FFSteamProcessor {

    private static final Logger log = LoggerFactory.getLogger(FFSteamProcessor.class);
    TimerJobUtil timerJobUtil = new TimerJobUtil();

    public FFSteamProcessor(){
        // write your code here
        log.info("FFSteamProcessor Start.");
        ServiceCache.SERVICESTARTTIME = new Date().getTime();
        try {
            timerJobUtil.StartTimer();
        } catch (Exception e) {
            //e.printStackTrace();
            log.error(e.getMessage());
        }

    }
    public String initializeJob(CameraInfo cameraInfo,ServerInfo serverInfo) throws Exception {
        if(cameraInfo.getRtsp()==null||cameraInfo.getIp()==null||cameraInfo.getChannel()==null){
            throw new Exception("cameraInfo数据不全");
        }
        if(serverInfo.getRtmp()==null||serverInfo.getIp()==null||serverInfo.getPort()==null){
            throw new Exception("serverInfo数据不全");
        }
        String token = UUID.randomUUID().toString();
        cameraInfo.setCount(1);
        String rtmp = serverInfo.getRtmp();
        if(rtmp.lastIndexOf("/")>=rtmp.length()-1){
            serverInfo.setRtmp(serverInfo.getRtmp()+token);
        }
        else {
            serverInfo.setRtmp(serverInfo.getRtmp()+"/"+token);
        }
        serverInfo.setUrl(serverInfo.getRtmp());
        serverInfo.setSteamToken(token);
        cameraInfo.setToken(token);
        String openTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime());
        cameraInfo.setOpenTime(openTime);
        FFThreadManagement.FFRunnable job = new FFThreadManagement.FFRunnable(cameraInfo,serverInfo);
        FFThreadManagement.FFRunnable.executorService.execute(job);
        FFThreadIndexUtil.FFThreadMAP.put(token, job);
        return  serverInfo.getRtmp();
    }

    public Set<String> getAllTokens(){
        return FFThreadIndexUtil.FFThreadMAP.keySet();
    }

    public void interruptJob(String token){
        FFThreadManagement.FFRunnable job = FFThreadIndexUtil.FFThreadMAP.get(token);
        if(job!=null){
            job.setInterrupted();
        }
    }


    @PreDestroy
    public void destroyer(){
        // 关闭线程池
        FFThreadManagement.FFRunnable.executorService.shutdownNow();
        // 销毁定时器
        TimerJobUtil.timer.cancel();
    }
}
