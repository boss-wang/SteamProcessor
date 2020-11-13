package cc.rexfa.java.media.cache;

import cc.rexfa.java.media.pojo.CameraInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存服务的一些数据
 */
public final class ServiceCache {
    /**
     * 流数据索引
     */
    public static Map<String, CameraInfo> STREAMMAP = new HashMap<String, CameraInfo>();
    //服务启动时间
    public static long SERVICESTARTTIME;
}
