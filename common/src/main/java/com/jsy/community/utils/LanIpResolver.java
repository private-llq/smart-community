package com.jsy.community.utils;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过mac地址获取局域网IP 避免同事的局域网ip因为 自动分配的原因 总是找不到
 * 只在开发环境下生效
 * @author YuLF
 * @since 2021-01-13 10:37
 */
@Slf4j
@ConditionalOnProperty(value = "jsy.profiles.active", havingValue = "dev")
public class LanIpResolver {


    public LanIpResolver() {
    }

    private static final Pattern IP_PATTERN = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
    private static final Pattern MAC_PATTERN = Pattern.compile("([0-9A-Fa-f]{2})(-[0-9A-Fa-f]{2}){5}");

    /**
     * 获取本机网内地址
     */
    public static InetAddress getNet4Address() {
        try {
            //获取所有网络接口
            Enumeration<NetworkInterface> allNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            //遍历所有网络接口
            while (allNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = allNetworkInterfaces.nextElement();
                //如果此网络接口为 回环接口 或者 虚拟接口(子接口) 或者 未启用 或者 描述中包含VM
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp() || networkInterface.getDisplayName().contains("VM")) {
                    continue;
                }
                for (Enumeration<InetAddress> netAddressEnumeration = networkInterface.getInetAddresses(); netAddressEnumeration.hasMoreElements(); ) {
                    InetAddress inetAddress = netAddressEnumeration.nextElement();
                    if (inetAddress != null) {
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress;
                        }
                    }
                }
            }
            return null;
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过mac地址获取局域网ip
     * @author YuLF
     * @since  2021/1/15 16:24
     */
    public static String getLanIpByMac(String macAddr){
        InetAddress net4Address = getNet4Address();
        assert net4Address != null;
        String hostAddress = net4Address.getHostAddress();
        log.info("局域网IP：{}", hostAddress);
        Map<String, String> ipMap = new HashMap<>(32);
        try {
            Process process = Runtime.getRuntime().exec("arp -a");
            @Cleanup InputStream is = process.getInputStream();
            @Cleanup InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String mac = getMac(line);
                if(mac != null){
                    ipMap.put(mac, getIp(line));
                }
            }
            br.close();
        } catch (IOException e) {
            log.info("com.jsy.community.utils.LanIpResolver.getLanIpByMac：获取局域网IP失败：{}", e.getMessage());
            return null;
        }
        return ipMap.get(macAddr);
    }

    public static void main(String[] args) {
        System.out.println("陈春利的IP："+getLanIpByMac("3c-7c-3f-4b-c0-a0"));
    }

    private static String getIp(String ipStr) {
        Matcher matcher = IP_PATTERN.matcher(ipStr);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    private static String getMac(String ipStr){
        Matcher matcher = MAC_PATTERN.matcher(ipStr);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }
}

