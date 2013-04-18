/**
 * 
 */
package com.dorm.smartterminal.global.util;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.dorm.smartterminal.global.util.bean.WifiDhcpInfo;

/**
 * @author Andy
 * 
 */
public class WifiUtil {

    public static WifiDhcpInfo WifiDhcpInfo(Context ctx) {

        WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);

        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();

        WifiDhcpInfo wifiDhcpInfo = new WifiDhcpInfo();
        wifiDhcpInfo.ipAddress = Formatter.formatIpAddress(dhcpInfo.ipAddress);
        wifiDhcpInfo.serverAddress = Formatter.formatIpAddress(dhcpInfo.serverAddress);
        wifiDhcpInfo.netmask = Formatter.formatIpAddress(dhcpInfo.netmask);
        wifiDhcpInfo.gateway = Formatter.formatIpAddress(dhcpInfo.gateway);
        wifiDhcpInfo.DNS1 = Formatter.formatIpAddress(dhcpInfo.dns1);
        wifiDhcpInfo.DNS2 = Formatter.formatIpAddress(dhcpInfo.dns2);

        LogUtil.log("WifiUtil", "DHCP info ipAddress----->" + wifiDhcpInfo.ipAddress);
        LogUtil.log("WifiUtil", "DHCP info serverAddress----->" + wifiDhcpInfo.serverAddress);
        LogUtil.log("WifiUtil", "DHCP info netmask----->" + wifiDhcpInfo.netmask);
        LogUtil.log("WifiUtil", "DHCP info gateway----->" + wifiDhcpInfo.gateway);
        LogUtil.log("WifiUtil", "DHCP info DNS1----->" + wifiDhcpInfo.DNS1);
        LogUtil.log("WifiUtil", "DHCP info DNS2----->" + wifiDhcpInfo.DNS2);

        return wifiDhcpInfo;
    }

}
