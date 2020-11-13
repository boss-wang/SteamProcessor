package cc.rexfa.java.media.util;

import cc.rexfa.java.media.FFThreadManagement;
import cc.rexfa.java.media.pojo.CameraInfo;

import java.util.HashMap;
import java.util.Map;

public final  class FFThreadIndexUtil {
    /**
     * 流数据索引
     */
    public static HashMap<String, FFThreadManagement.FFRunnable> FFThreadMAP = new HashMap<String, FFThreadManagement.FFRunnable>();
}
