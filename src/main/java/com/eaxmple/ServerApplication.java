package com.eaxmple;

import com.eaxmple.server.MyServer;

/**
 * @author: 满熊
 * @date: 2022/8/18 9:45
 */
public class ServerApplication {
    public static void main(String args[]) throws Exception {
        MyServer aioServer = new MyServer();
        aioServer.startWithCompletionHandler();
        System.out.println("------------------服务器启动成功！！！--------------------");
        while (true){
        }
    }
}
