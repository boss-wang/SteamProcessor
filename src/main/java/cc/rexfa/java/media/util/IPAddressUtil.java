package cc.rexfa.java.media.util;
import java.net.InetAddress;
import java.net.UnknownHostException;
public class IPAddressUtil {
    public static String GetIPAddress(String domainName) {
        String ip = domainName;
        try {
            ip = InetAddress.getByName(domainName).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return domainName;
        }
        return ip;
    }
}
