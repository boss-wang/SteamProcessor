package cc.rexfa.java.media;

import cc.rexfa.java.media.pojo.CameraInfo;
import cc.rexfa.java.media.cache.ServiceCache;
import cc.rexfa.java.media.pojo.ServerInfo;
import cc.rexfa.java.media.util.FFThreadIndexUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FFThreadManagement {
    public static class FFRunnable implements Runnable {
        // 创建线程池
        public static ExecutorService executorService = Executors.newCachedThreadPool();
        private Thread currentThread;
        private CameraInfo currentCamera;
        private ServerInfo currentServer;
        public FFRunnable(CameraInfo currentCamera,ServerInfo currentServer) {
            this.currentCamera = currentCamera;
            this.currentServer = currentServer;
        }
        // 中断线程
        public void setInterrupted() {
            currentThread.interrupt();
        }
        @Override
        public void run() {
            // 直播流
            try {
                // 获取当前线程存入缓存
                currentThread = Thread.currentThread();
                ServiceCache.STREAMMAP.put(currentCamera.getToken(), currentCamera);
                // 执行转流推流任务
                FFProcessor push = new FFProcessor(currentCamera,currentServer).from();
                if (push != null) {
                    push.to().go(currentThread);
                }
                // 清除缓存
                ServiceCache.STREAMMAP.remove(currentCamera.getToken());
                FFThreadIndexUtil.FFThreadMAP.remove(currentCamera.getToken());
            } catch (Exception e) {
                System.err.println(
                        "当前线程：" + Thread.currentThread().getName() + " 当前任务：" + currentCamera.getRtsp() + "停止...");
                ServiceCache.STREAMMAP.remove(currentCamera.getToken());
                FFThreadIndexUtil.FFThreadMAP.remove(currentCamera.getToken());
                e.printStackTrace();
            }
        }
    }
}
