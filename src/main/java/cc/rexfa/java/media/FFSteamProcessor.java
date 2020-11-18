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

/**
 * @author Rex Zhang
 * 摄像头转码主类，一个工程请仅使用一个主类
 * 一个高清1080p摄像头的码率应该会占用8-10mbit带宽
 * 一个转出的高清1080p码率一般也会占用出口8-10mbit带宽
 * 摄像头支持rtsp协议，服务器请支持rtmp协议
 * 跟随组件提供的基于nginx-rtmp-module模块的nginx需要使用硬盘进行缓存，请配合使用高速硬盘
 */
public class FFSteamProcessor {

    private static final Logger log = LoggerFactory.getLogger(FFSteamProcessor.class);
    TimerJobUtil timerJobUtil = new TimerJobUtil();

    /**
     * 实例化处理器
     */
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

    /**
     * 创建工作线程
     * 		// 声明摄像头信息
     * 		CameraInfo cameraInfo0 = new CameraInfo();
     * 		cameraInfo0.setIp("192.168.1.64"); //摄像头IP
     * 		cameraInfo0.setChannel("main");	//摄像头视频流通道，一般分main高清流和sub一般流
     * 		cameraInfo0.setPort("554");	//摄像头端口号
     * 		cameraInfo0.setUsername("admin"); //摄像头用户名
     * 		cameraInfo0.setPassword("12345qwert"); //摄像头密码
     * 		cameraInfo0.setRtsp("rtsp://admin:12345qwert@192.168.1.64:554/h264/ch1/main/av_stream"); //摄像头RTSP连接字符串
     * 		cameraInfo0.setUrl("rtsp://admin:12345qwert@192.168.1.64:554/h264/ch1/main/av_stream");  //摄像头RTSP的URL 可不填
     * 	    // 声明服务器信息
     * 	    ServerInfo serverInfo0 = new ServerInfo();
     * 		serverInfo0.setIp("127.0.0.1");	//视频服务器IP
     * 		serverInfo0.setPort("1935");   //视频服务器端口
     * 		serverInfo0.setPassword("");	//视频服务器密码
     * 		serverInfo0.setUsername("");	//视频服务器用户名
     * 		serverInfo0.setServiceappname("live"); //视频服务器的应用名称
     * 		serverInfo0.setRtmp("rtmp://127.0.0.1/live/"); //视频服务器的rtmp地址，注意带应用名称,如果服务器需要用户名密码请注意使用rtmp://用户名:密码@IP的格式
     * 		serverInfo0.setUrl("rtmp://127.0.0.1/live/"); //视频服务器的rtmpURL 可不填
     * @param cameraInfo 摄像头信息，请尽量填写正确和全面
     * @param serverInfo 流媒体服务器信息，请尽量填写正确和全面
     * @return 成功创建工作线程后返回线程的唯一索引Token
     * @throws Exception 一些简单参数的检查，异常后抛出
     */
    public String initializeJob(CameraInfo cameraInfo,ServerInfo serverInfo) throws Exception {
        if(cameraInfo==null || serverInfo ==null)
        {
            throw new Exception("参数不可以为空");
        }
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
        return token;
    }

    /**
     * 取得所有存活线程的唯一Token
     * @return 返回Token的Set,注意只是当前的，不代表返回后token是否还有效。
     */
    public Set<String> getAllTokens(){
        return FFThreadIndexUtil.FFThreadMAP.keySet();
    }

    /**
     * 通过token关闭指定线程
     * @param token 请输入有效的token
     * @throws Exception 在线程不存在的时候，返回异常
     */
    public void interruptJob(String token) throws Exception{
        FFThreadManagement.FFRunnable job = FFThreadIndexUtil.FFThreadMAP.get(token);
        if(job!=null){
            job.setInterrupted();
            // 清除缓存
            ServiceCache.STREAMMAP.remove(token);
            FFThreadIndexUtil.FFThreadMAP.remove(token);
        }
        else {
            throw new Exception("No corresponding thread found");
        }
    }

    /**
     * 整个模块退出时候进行资源释放，此方法大多数情况会自动调用
     */
    @PreDestroy
    public void destroyer(){
        // 关闭线程池
        FFThreadManagement.FFRunnable.executorService.shutdownNow();
        // 销毁定时器
        TimerJobUtil.timer.cancel();
    }
}
