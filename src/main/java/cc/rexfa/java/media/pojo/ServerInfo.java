package cc.rexfa.java.media.pojo;

import java.io.Serializable;

public class ServerInfo implements Serializable {

    private static final long serialVersionUID = 4132643091120732933L;
    private String username;// 账号
    private String password;// 密码
    private String ip;// ip
    private String port;//port
    private String serviceappname;// 服务器App名称
    //private String rtsp;// rtsp地址
    private String steamToken;
    private String rtmp;// rtmp地址
    private String url;// 推流方向地址
    private String startTime;// 推流开始时间
    private String endTime;// 推流停止时间

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }

    public String getServiceappname() {
        return serviceappname;
    }

    public void setServiceappname(String serviceappname) {
        this.serviceappname = serviceappname;
    }
/*    public String getRtsp() {
        return rtsp;
    }

    public void setRtsp(String rtsp) {
        this.rtsp = rtsp;
    }*/

    public String getRtmp() {
        return rtmp;
    }

    public void setRtmp(String rtmp) {
        this.rtmp = rtmp;
    }

    public String getSteamToken(){ return steamToken; }
    public void setSteamToken(String steamToken) {this.steamToken = steamToken;}


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUrl() {
        if(url==null)
        {
            url = "rtmp://"+ip+":"+port+"/"+serviceappname+"/"+steamToken;
        }
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

}
