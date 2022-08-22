package com.eaxmple.util;

import com.eaxmple.config.ServerConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: 满熊
 * @date: 2022/8/18 9:44
 */
public class ServerUtil {

    /**
     * 从字符串中获取IP地址
     * @param str
     * @return
     * @throws IOException
     */
    public static String getIP(String str) {
        //获取其中的ip地址
        String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
        Pattern pattern = Pattern.compile(ipv4Pattern);
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            String IP = matcher.group();
            return IP;
        }
        return null;
    }

    /**
     * 从数据中获取单个数据帧
     * @param str
     * @return
     */
    public static ArrayList<String> getSingleData(String str) {
        ArrayList<String> datas = new ArrayList();
        //获取其中的ip地址
        String ipv4Pattern = ServerConfig.firstCharacter + "[^"+ServerConfig.lastCharacter+"]+"+ServerConfig.lastCharacter;
        System.out.println(ServerConfig.firstCharacter);
        System.out.println(ipv4Pattern);
        Pattern pattern = Pattern.compile(ipv4Pattern);
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            String IP = matcher.group();
            datas.add(IP);
        }
        if (datas.size() != 0){
            return datas;
        }
        return null;
    }
}
