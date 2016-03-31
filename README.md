# AndroidNetMonitor
#Summary
用于获取流量来源的真实应用程序信息,采集手机发送和接收的报文简要信息（即源IP、源端口、目标IP、目标端口和传输层协议），并且根据socket记录每个报文对应哪个手机app，这些数据都以文件方式存储在手机的SD卡上。

文件的每一行需要记录的信息包括：每个网络套接字的5元组信息（即源IP、源端口、目标IP、目标端口和传输层协议）、记录时间、应用程序名称、已及对应事件（创建/销毁套接字等）。

#Technology
1.将tcpdump，lsof重新编译成适用于安卓的二进制(arm-linux-androideabi-g++)
2.用Adapter将Listview，CheckBox与List<Program(自定义类)>绑定
3.并发运行抓取Socket与Packet，使用`lsof +c 0 -i -F ctPnf 2>&1`与`tcpdump -v -s -w pcap`命令
4.读取和解析`/proc/net/tcp, tcp6,udp,udp6`文件， 利用socket inode以及app的pid建立起五元组与应用名称的获取和对应关系

#Implementation
（1）	app列表的获取
利用PackageManager管理器，检索所有的应用程序与数据，再用ActivityManager与PackagesInfo获取从上得到的所有app名称以及pid，并且使用listview布局展示出来。

（2）	/proc/pid/fd文件的读取和解析
在安卓中，使用runTime.exec(cmd);可以在android里运行cmd的命令，所以可以通过这个来获取运行命令后的结果输出. 因为/proc目录的读取与分析需要用到root权限，所以使用了runTime.exec(“su”);在安卓上提取root权限。
然后使用```ls -l /proc/(pid)/fd > /sdcard/fdres``` 将信息保存到fdres这个文件中，再使用cat /sdcard/fdres 提取信息这个折衷的办法。
在得到的信息中，使用```"socket:\\S(\\d+)\\S"```;的正则表达式，把socket的所有inode标号都抽取出来，放到ArrayList<Integer>中。

（3）	/proc/net/tcp, tcp6,udp,udp6文件的读取和解析（或者使用`lsof +c 0 -i -F ctPnf 2>&1`命令）
抽象读取tcp, tcp6,udp,udp6为PollData类，使用java自带的Scanner类去分析读取/proc/net/tcp等，得到的输出如下
```
* sl  local_address rem_address   st tx_queue rx_queue tr tm->when retrnsmt   uid  ...
* 0: 0100007F:13AD 00000000:0000 0A 00000000:00000000 00:00000000 00000000     0   ...
* 1: 00000000:15B3 00000000:0000 0A 00000000:00000000 00:00000000 00000000     0   ...
* 2: 0F02000A:15B3 0202000A:CE8A 01 00000000:00000000 00:00000000 00000000     0   ...
*
```

再使用由上得到的socket_inode在得到的信息中查找对应的具体信息，转换成10进制
得到的具体socket信息如下：
```
1.	
2.	    46: 010310AC:9C4C 030310AC:1770 01 
3.	   |      |      |      |      |   |--> connection state
4.	   |      |      |      |      |------> remote TCP port number
5.	   |      |      |      |-------------> remote IPv4 address
6.	   |      |      |--------------------> local TCP port number
7.	   |      |---------------------------> local IPv4 address
8.	   |----------------------------------> number of entry
9.	   00000150:00000000 01:00000019 00000000  
10.	      |        |     |     |       |--> number of unrecovered RTO timeouts
11.	      |        |     |     |----------> number of jiffies until timer expires
12.	      |        |     |----------------> timer_active (see below)
13.	      |        |----------------------> receive-queue
14.	      |-------------------------------> transmit-queue
15.	   1000        0 54165785 4 cd1e6040 25 4 27 3 -1
16.	    |          |    |     |    |     |  | |  | |--> slow start size threshold, 
17.	    |          |    |     |    |     |  | |  |      or -1 if the threshold
18.	    |          |    |     |    |     |  | |  |      is >= 0xFFFF
19.	    |          |    |     |    |     |  | |  |----> sending congestion window
20.	    |          |    |     |    |     |  | |-------> (ack.quick<<1)|ack.pingpong
21.	    |          |    |     |    |     |  |---------> Predicted tick of soft clock
22.	    |          |    |     |    |     |              (delayed ACK control data)
23.	    |          |    |     |    |     |------------> retransmit timeout
24.	    |          |    |     |    |------------------> location of socket in memory
25.	    |          |    |     |-----------------------> socket reference count
26.	    |          |    |-----------------------------> inode
27.	    |          |----------------------------------> unanswered 0-window probes
28.	    |---------------------------------------------> uid
```

（4）	五元组与应用名称的获取和对应关系建立
由(2),(3)所共有的socket inode以及app的pid建立起五元组与应用名称的获取和对应关系，将获取到的信息保存到默认的目录/sdcard/Android/data/com.xx的目录下 , 格式为
```
"The application name is, pid is, and socket is:"
"number_of_entry "+fields[i+0] + "\n";
"local_IPv4_address "+fields[i+1] + "\n";
"local_IPv4_address "+hexconvert.hexa2decIpAndPort(fields[i+1]) + "\n";
"remote_IPv4_address "+fields[i+2] + "\n";
"remote_IPv4_address "+hexconvert.hexa2decIpAndPort(fields[i+2]) + "\n";
"connection_state" + fields[i+3] + "\n";
"transmit_receive_queue"+ fields[i+4]+ "\n";
"timer_active"+fields[i+5]+ "\n";
"number_of_unrecovered_RTO_timeouts:"+fields[i+6]+ "\n";
"uid: "+fields[i+7]+ "\n";
"unanswered_0-window_probes: "+fields[i+8]+ "\n";
"inode : "+fields[i+9]+ "\n";
"socket_reference_count: "+fields[i+10]+ "\n";
"location_of_socket_in_memory:  "+fields[i+11]+ "\n";
"retransmit_timeout: "+fields[i+12]+ "\n";
"predicted_tick_of_soft_clock: "+ fields[i+13]+ "\n";
"ack"+ fields[i+14]+ "\n";
"sending_congestion_window: "+ fields[i+15]+ "\n";
    "slowstart: "+ fields[i+16]+ "\n\n";
```

（5）上传到服务器， 使用Python脚本解析
