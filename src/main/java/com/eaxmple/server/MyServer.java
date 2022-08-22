package com.eaxmple.server;


import com.eaxmple.config.ServerConfig;
import com.eaxmple.util.ServerUtil;
import lombok.SneakyThrows;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author: 满熊
 * @date: 2022/8/14 14:38
 */
public class MyServer {

    public final static int PORT = Integer.parseInt(ServerConfig.port);
    private AsynchronousServerSocketChannel server;
    public final static String localIP = ServerConfig.localIP;
    public final static int maxConnection = Integer.parseInt(ServerConfig.maxConnection);
    HashMap<String,AsynchronousSocketChannel> channelHashMap = new HashMap<>();

    /**
     * 服务器的构造方法，设置了服务器的端口号，并开启了服务器
     * @throws IOException
     */
    public MyServer() throws IOException {
        server = AsynchronousServerSocketChannel.open().bind( new InetSocketAddress(PORT) );
    }

    /**
     * Callback方式 监听客户端连接，当监测到新连接后，加入服务器的客户端列表中
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public void startWithCompletionHandler(){
        server.accept(null,
                new CompletionHandler<AsynchronousSocketChannel, Object>() {
                    public void completed(AsynchronousSocketChannel result, Object attachment) {
                        server.accept(null, this);// 再次接收客户端连接
                        try {
                            addChannel(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        handleWithCompletionHandler(result);
                    }
                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        exc.printStackTrace();
                    }
                });
    }

    /**
     * 数据处理
     * @param channel
     */
    public void handleWithCompletionHandler(final AsynchronousSocketChannel channel) {
        try {
            final ByteBuffer buffer = ByteBuffer.allocate(2048);
            final long timeout = 30L;
            channel.read(buffer, timeout, TimeUnit.MINUTES, null, new CompletionHandler<Integer, Object>() {
                @SneakyThrows
                @Override
                public void completed(Integer result, Object attachment) {
                    if (result == -1) {
                        try {
                            channel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    buffer.flip();
                    String content = Charset.forName("UTF-8").decode(buffer).toString();
                    System.out.println("服务器接收数据:" + content );
                    //转发数据
                    isCombinationData(content,channel);
                    buffer.clear();
                    // 再次等待读取客户端消息
                    channel.read(buffer, timeout, TimeUnit.MINUTES, null, this);
                }

                @SneakyThrows
                @Override
                public void failed(Throwable exc, Object attachment) {
                    deleteChannel(channel);
                    exc.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证是否开启了组合帧模式
     * @param content
     * @param channel
     * @throws IOException
     * @throws InterruptedException
     */
    private void isCombinationData(String content,AsynchronousSocketChannel channel) throws IOException, InterruptedException {
        if(ServerConfig.combinationData == true){
            if (!content.startsWith(ServerConfig.firstCharacter) && !content.endsWith(ServerConfig.lastCharacter)){
                throw new ServerException("您发送的数据不符合服务器设定的数据校验规范！");
            }
            ArrayList<String> res = ServerUtil.getSingleData(content);
            if (res != null){
                for (String item : res){
                    forwardToOthers(item,channel);
                    Thread.sleep(ServerConfig.sendDataInterval);
                }
            }
        }else{
            forwardToOthers(content,channel);
        }
    }

    /**
     * 将新建立的连接加入到服务器的客户端列表中
     * @param result
     */
    public void addChannel(AsynchronousSocketChannel result) throws IOException {
        if (channelHashMap.size() >= maxConnection){
            System.err.println("-----------------------客户端连接数量已达上限，不可在添加客户端！--------------------------");
            return ;
        }
        String IP = ServerUtil.getIP(result.getRemoteAddress().toString());
        if ("127.0.0.1".equals(IP)){
            channelHashMap.put(localIP,result);
        }else{
            channelHashMap.put(IP,result);
        }
        System.out.println("-------------------"+result.getRemoteAddress()+"连接成功,目前已连接客户端数量: "+channelHashMap.size()+"----------------------");
    }

    /**
     * 从客户端列表中删除指定连接
     * @param channel
     * @throws IOException
     */
    public void deleteChannel(AsynchronousSocketChannel channel) throws IOException {
        String address = channel.getRemoteAddress().toString();
        if (address != null){
            String IP = ServerUtil.getIP(address);
            if (IP != null){
                channelHashMap.remove(IP);
                System.out.println("------------------------"+address+"-------------------------------");
            }
        }
    }

    /**
     * 向其他客户端转发数据
     * @param content
     * @param channel
     */
    public void forwardToOthers(String content,AsynchronousSocketChannel channel) throws IOException {
        if (content.contains("@")){
            String IP = ServerUtil.getIP(content);
            AsynchronousSocketChannel toChannel = channelHashMap.get(IP);
            if (toChannel != null){
                toChannel.write(ByteBuffer.wrap(content.substring(content.indexOf(":") + 1).getBytes(StandardCharsets.UTF_8)));
            }else{
                System.err.println("-------------没有IP="+IP+"的客户端---------------------");
            }
        }else{
            String IP = ServerUtil.getIP(channel.getRemoteAddress().toString());
            for(Map.Entry<String, AsynchronousSocketChannel> map : channelHashMap.entrySet()){
                if (map.getKey().equals(IP)){
                    continue;
                }
                AsynchronousSocketChannel toChannel = map.getValue();
                toChannel.write(ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8)));
            }
        }
    }
}

