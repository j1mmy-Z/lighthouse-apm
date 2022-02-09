package com.jimmy.lighthouse.apm.agent.util;

import com.jimmy.lighthouse.apm.common.constant.LighthouseConstants;
import org.apache.commons.lang3.StringUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-04
 */
public class LighthouseUtil {

    public static String IP = getLocalInetAddress();

    public static String getIP() {
        return IP;
    }

    /**
     * 获取本机ip地址
     */
    public static String getLocalInetAddress() {
        // 环境变量中读取
        String localIp = System.getProperty("local.ip");
        if (StringUtils.isNotBlank(localIp) && localIp.length() >= 7
                && Character.isDigit(localIp.charAt(0))
                && Character.isDigit(localIp.charAt(localIp.length() - 1))) {
            return localIp;
        }

        // 从网卡中读
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address;
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && !address.isSiteLocalAddress()
                            && StringUtils.indexOf(address.getHostAddress(), ":") > -1) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException ignored) {
        }

        return LighthouseConstants.DEFAULT_LOCAL_IP;
    }

    public static String getIp16(String address) {
        String[] parts = address.split("\\.");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            String hex = Integer.toHexString(Integer.parseInt(part));
            if (hex.length() == 1) {
                builder.append('0').append(hex);
            } else {
                builder.append(hex);
            }
        }
        return builder.toString();
    }

    public static String getIpInt(String address) {
        return StringUtils.replaceAll(address, ".", "");
    }

    /**
     * 获取当前pid
     */
    public static int getCurrentPid() {
        String pidStr = System.getProperty("local.pid");
        if (StringUtils.isNotBlank(pidStr) && StringUtils.isNumeric(pidStr)) {
            try {
                long pid = Long.parseLong(pidStr);
                return (int) Math.abs(pid);
            } catch (Exception ignored) {
            }
        }

        try {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            String name = runtimeMXBean.getName();
            return Integer.parseInt(StringUtils.substring(name, 0, name.indexOf("@")));
        } catch (Exception ignored) {
        }
        return LighthouseConstants.DEFAULT_LOCAL_PID;
    }

    public static String getHexPid(int pid) {
        if (pid < 0) {
            pid = 0;
        } else if (pid > 65535) {
            pid = pid % 60000;
        }
        StringBuilder pidStr = new StringBuilder(Integer.toHexString(pid));
        while (pidStr.length() < 4) {
            pidStr.insert(0, "0");
        }
        return pidStr.toString();
    }

}