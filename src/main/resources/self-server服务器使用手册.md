# self-server服务器使用手册

## 1. 使用流程

**1）查询服务器所在主机的局域网IP，cmd-->输入命令`ipconfig`,示例如下：**

![image-20220818110919413](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818110919413.png)

![image-20220818111002681](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818111002681.png)

`注意事项：` 输入ipconfig命令后会有很多ipv4的地址，选用上图标记的局域网地址（192.168.x.x），同时保证服务器与各客户端必须在`同一网段`下。

**2)修改配置服务器的ip，打开self-server服务器，找到application.proerties(如下图一)，找到server.ip,修改为步骤1获取的局域网地址，示例如下：**

![image-20220818111554494](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818111554494.png)

![image-20220818111951282](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818111951282.png)

**3）启动服务器，找到ServerApplication，右键运行：**

![image-20220818112101791](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818112101791.png)

## 2. 服务器文件说明

![image-20220818112706508](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818112706508.png)

**1）服务器的启动类--ServerApplication.java**

![image-20220818120648711](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818120648711.png)

就是实例化了Myserver服务器，然后创建了一个死循环，保证程序不会被终止。

**2）配置文件--applcation.properties**

![image-20220818183057821](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818183057821.png)

通过这个文件可以设置服务器的IP地址，以及端口号，还有最大连接数，这个IP和端口都要暴露给硬件设备和安卓程序

**3）服务器的配置类--ServerConfig.java**

![image-20220818183406713](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818183406713.png)

配置类的作用就是为了读取配置文件中的配置项，相当于将一个配置文件转成一个java类。

**4）服务器的工具类--ServerUtil.java**

![image-20220818184034417](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818184034417.png)

包含服务器的各个工具方法，目前只包含了一个工具方法，从字符串中获取IP。

**3）服务器的功能实现类---MyServer.java**

![image-20220818184914828](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818184914828.png)

主要包含了与客户端建立连接，一对一转发数据，一对所有转发数据功能。

## 3. 注意事项

1. 启动之前一定要设置服务器的ip和端口号，否则启动异常。

2. 本服务器无法监测网络调试助手是否断开连接。

3. 本一台注意只能使用一个客户端，多次连接会覆盖前一个客户端（IP+port）

4. 在使用时可能出现下述问题，这不是服务器的问题，而是客户端发送数据太快了，前面一条数据还没有发完，后面有发下一条数据（发慢点就好，比如线程休眠一下），或者是没有从读模式转成写模式（找到对应的数据区buffer，然后buffer.flip()）。

   ![image-20220818190011178](https://my-typroa-photos.oss-cn-guangzhou.aliyuncs.com/images/image-20220818190011178.png)
