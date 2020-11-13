package cc.rexfa.java.media;
import static org.bytedeco.ffmpeg.global.avcodec.av_packet_unref;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;

import cc.rexfa.java.media.pojo.ServerInfo;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cc.rexfa.java.media.pojo.CameraInfo;
import cc.rexfa.java.media.util.IPAddressUtil;
public class FFProcessor {

    private final static Logger logger = LoggerFactory.getLogger(FFProcessor.class);
    protected FFmpegFrameGrabber grabber = null;// 采集器解码器
    protected FFmpegFrameRecorder record = null;// 编码器
    // 画面参数
    protected int frameWidth;// 视频像素宽
    protected int frameHeight;// 视频像素高
    protected int codecid;
    protected double frameRate;// 帧率
    protected int bitRate;// 比特率
    // 音频参数
    protected int audioCodecid;
    private int audioChannels;
    private int audioBitRate;
    private int sampleRate=22050; //缺省给一个FM级别

    // 流源头的设备信息
    private CameraInfo cameraInfo;
    // 要推送到的服务器
    private ServerInfo serverInfo;

    // cor
    /*
    public FFProcessor() {
        super();
    }
    */
    public FFProcessor(CameraInfo cameraInfo,ServerInfo serverInfo) {
        this.cameraInfo = cameraInfo;
        this.serverInfo = serverInfo;
    }
    /**
     * 选择视频源
     *
     * @throws org.bytedeco.javacv.FrameGrabber.Exception
     * @throws org.bytedeco.javacv.FrameGrabber.Exception
     * @throws org.bytedeco.javacv.FrameGrabber.Exception
     * @throws Exception
     */
    public FFProcessor from() throws Exception {
        grabber = new FFmpegFrameGrabber(cameraInfo.getRtsp());
        Socket rtspSocket = new Socket();
        Socket rtmpSocket = new Socket();
        // 测试连接，如果可以连接则继续，如果有连接问题，则退出结束
        try {
            rtspSocket.connect(new InetSocketAddress(cameraInfo.getIp(), 554), 1000);
        } catch (IOException e) {
            grabber.stop();
            grabber.close();
            rtspSocket.close();
            logger.error("与拉流IP：   " + cameraInfo.getIp() + "   端口：   554    建立TCP连接失败！");
            return null;
        }
        try {
            rtmpSocket.connect(new InetSocketAddress(IPAddressUtil.GetIPAddress(serverInfo.getIp()),
                    Integer.parseInt(serverInfo.getPort())), 1000);
        } catch (IOException e) {
            grabber.stop();
            grabber.close();
            rtspSocket.close();
            logger.error("与推流IP：   " + serverInfo.getIp() + "   端口：   " + serverInfo.getPort() + " 建立TCP连接失败！");
            return null;
        }

        //logger.debug("******   TCPCheck    END     ******");
        if (cameraInfo.getRtsp().indexOf("rtsp") >= 0) {
            grabber.setOption("rtsp_transport", "tcp"); // tcp用于解决丢包问题
        }
        // 设置采集器 Protocols -> RTSP 时候的Set socket TCP I/O timeout in microseconds.
        grabber.setOption("stimeout", "2000000");
        try {
            //logger.debug("******   grabber.start()    BEGIN   ******");
//          后期优化
/*            if ("sub".equals(cameraInfo.getStream())) {
                grabber.start(config.getSub_code());
            } else if ("main".equals(cameraInfo.getStream())) {
                grabber.start(config.getMain_code());
            } else {
                grabber.start(config.getMain_code());
            }*/
            grabber.start();
            //logger.debug("******   grabber.start()    END     ******");

            // 开始之后ffmpeg会采集视频信息，之后就可以获取音视频信息
            frameWidth = grabber.getImageWidth();
            frameHeight = grabber.getImageHeight();
            // 若视频像素值为0，说明拉流异常，程序结束
            if (frameWidth == 0 && frameHeight == 0) {
                //logger.error(cameraInfo.getRtsp() + "  拉流异常！");
                grabber.stop();
                grabber.close();
                return null;
            }
            // 视频参数

            codecid = grabber.getVideoCodec();
            frameRate = grabber.getVideoFrameRate();// 帧率
            bitRate = grabber.getVideoBitrate();// 比特率
            // 音频参数
            // 注意音频参数必须大于零,且不能为空,才能保存音频数据
            audioCodecid = grabber.getAudioCodec();
            audioChannels = grabber.getAudioChannels();
            audioBitRate = grabber.getAudioBitrate();
            if (audioBitRate < 1) {
                audioBitRate = 128 * 1000;// 默认音频比特率
            }
        } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
            logger.error("ffmpeg错误信息：", e);
            grabber.stop();
            grabber.close();
            return null;
        }

        return this;

    }

    /**
     * 选择输出
     *
     * @throws Exception
     */
    public FFProcessor to() throws Exception {
        // 录制/推流器
        record = new FFmpegFrameRecorder(serverInfo.getRtmp(), frameWidth, frameHeight);
        record.setVideoOption("crf", "28");// 画面质量参数，0~51；18~28是一个合理范围
        record.setGopSize(2);
        record.setFrameRate(frameRate);
        record.setVideoBitrate(bitRate);

        record.setAudioChannels(audioChannels);
        record.setAudioBitrate(audioBitRate);
        record.setSampleRate(sampleRate);
        AVFormatContext fc = null;
        if (serverInfo.getRtmp().indexOf("rtmp") >= 0 || serverInfo.getRtmp().indexOf("flv") > 0) {
            // 封装格式flv
            record.setFormat("flv");
            record.setAudioCodecName("aac");
            record.setVideoCodec(codecid);
            fc = grabber.getFormatContext();
        }
        try {
            record.start(fc);
        } catch (Exception e) {
            logger.error(cameraInfo.getRtsp() + "  推流异常！");
            logger.error("ffmpeg错误信息：", e);
            grabber.stop();
            grabber.close();
            record.stop();
            record.close();
            return null;
        }
        return this;

    }

    /**
     * 转封装
     *
     * @throws org.bytedeco.javacv.FrameGrabber.Exception
     * @throws org.bytedeco.javacv.FrameRecorder.Exception
     * @throws InterruptedException
     */
    public FFProcessor go(Thread nowThread)
            throws org.bytedeco.javacv.FrameGrabber.Exception, org.bytedeco.javacv.FrameRecorder.Exception {
        long err_index = 0;// 采集或推流导致的错误次数
        // 连续五次没有采集到帧则认为视频采集结束，程序错误次数超过5次即中断程序
        //logger.info(cameraPojo.getRtsp() + " 开始推流...");
        // 释放探测时缓存下来的数据帧，避免pts初始值不为0导致画面延时
        grabber.flush();
        for (int no_frame_index = 0; no_frame_index < 5 || err_index < 5;) {
            try {
                // 用于中断线程时，结束该循环
                nowThread.sleep(1);
                AVPacket pkt = null;
                // 获取没有解码的音视频帧
                pkt = grabber.grabPacket();
                if (pkt == null || pkt.size() <= 0 || pkt.data() == null) {
                    // 空包记录次数跳过
                    no_frame_index++;
                    err_index++;
                    continue;
                }
                // 不需要编码直接把音视频帧推出去
                err_index += (record.recordPacket(pkt) ? 0 : 1);

                av_packet_unref(pkt);
            } catch (InterruptedException e) {
                // 销毁构造器
                grabber.stop();
                grabber.close();
                record.stop();
                record.close();
                logger.info(cameraInfo.getRtsp() + " 中断推流成功！");
                break;
            } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
                err_index++;
            } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                err_index++;
            }
        }
        // 程序正常结束销毁构造器
        grabber.stop();
        grabber.close();
        record.stop();
        record.close();
        logger.info(cameraInfo.getRtsp() + " 推流结束...");
        return this;
    }
}
