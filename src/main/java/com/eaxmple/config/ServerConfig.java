package com.eaxmple.config;

import com.eaxmple.biz.ServerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author: 满熊
 * @date: 2022/8/18 9:32
 */
public class ServerConfig {
    public static String localIP;
    public static String port;
    public static String maxConnection;
    public static boolean combinationData;
    public static String firstCharacter;
    public static String lastCharacter;
    public static Integer sendDataInterval;

    static {
        try {
            InputStream in = ClassLoader.getSystemResourceAsStream("application.properties");
            Properties p = new Properties();
            p.load(in);
            ServerConfig.localIP = p.getProperty("server.localIP");
            ServerConfig.port = p.getProperty("server.port");
            ServerConfig.maxConnection = p.getProperty("server.max-connection");
            ServerConfig.combinationData = Boolean.parseBoolean(p.getProperty("server.combination-data"));
            ServerConfig.firstCharacter = p.getProperty("server.first-character");
            ServerConfig.lastCharacter = p.getProperty("server.last-character");
            if (true == ServerConfig.combinationData
                    && (ServerConfig.firstCharacter == null || "".equals(ServerConfig.firstCharacter )
                    || ServerConfig.lastCharacter ==null || "".equals(ServerConfig.lastCharacter) )
                    || ServerConfig.firstCharacter.length() != 3 || ServerConfig.lastCharacter.length() != 3){
                throw  new ServerException("您以开启了组合帧模式，但未设置数据帧的首尾字符或者首尾字符格式异常（用单引号包含一个字符）");
            }
            ServerConfig.firstCharacter = ServerConfig.firstCharacter.replaceAll("'","");
            ServerConfig.lastCharacter = ServerConfig.lastCharacter.replaceAll("'","");
            ServerConfig.sendDataInterval = Integer.parseInt(p.getProperty("Server.send-data-interval"));
        } catch (IOException | ServerException e) {
            System.out.println("-------------------配置文件解析异常------------------------");
            e.printStackTrace();
        }
    }
}
