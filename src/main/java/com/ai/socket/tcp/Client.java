package com.ai.socket.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.ai.socket.util.Tools;

public class Client {
	private static final int PORT = 20000;
	private static final int LOCAL_PORT = 20001;
	
	public static void main(String[] args)throws Exception  {
		Socket socket = createSocket();
		initSocket(socket);
		
		//链接到本地20000端口，超时时间3秒，超过则抛出超时异常
		socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), PORT),3000);
		
		System.out.println("已发起服务器连接");
		System.out.println("客户端信息："+socket.getLocalAddress()+"P:"+socket.getLocalPort());
		System.out.println("服务器信息："+socket.getInetAddress()+"P:"+socket.getPort());
		
		try {
			todo(socket);
		} catch (Exception e) {
			System.out.println("异常关闭");
		}
		
		socket.close();
		System.out.println("客户端已退出~");
	}
	private static void todo(Socket client) throws IOException {
		//得到Socket输出流，并转换为打印流
		OutputStream outputStream = client.getOutputStream();
		
		//得到Socket输入流
		InputStream inputStream = client.getInputStream();
		byte[] buffer = new byte[256];
		ByteBuffer byteBuffer =  ByteBuffer.wrap(buffer);
		//byte
		byteBuffer.put((byte)126);
		//char
		char c = 'a';
		byteBuffer.putChar(c);
		//int
		int i = 2323123;
		byteBuffer.putInt(i);
		//boolean
		boolean b = true;
		byteBuffer.put(b?(byte)1:(byte)0);
		//Long
		long  l = 123333123L;
		byteBuffer.putLong(l);
		//float
		float f = 12.345f;
		byteBuffer.putFloat(f);
		//double 
		double d = 1231313.1231313141;
		byteBuffer.putDouble(d);
		//String
		String s = "Hello您好！";
		byteBuffer.put(s.getBytes());
		
		//发送到服务器
		outputStream.write(buffer,0,byteBuffer.position()+1);
		//接受服务器返回
		int read = inputStream.read(buffer);
		System.out.println("收到数量："+read);
		
		inputStream.close();
		outputStream.close();
	}
	private static Socket createSocket() throws Exception {
		/**
		//无代理模式，等效于空构造函数
		Socket socket = new Socket(Proxy.NO_PROXY);
		//新建一份具有HTTP代理的套接字，传输数据将通过www.baidu.com:8080端口转发
		Proxy proxy = new Proxy(Proxy.Type.HTTP
				,new InetSocketAddress(Inet4Address.getByName("www.baidu.com"), 8800));
		socket = new Socket(proxy);
		//新建一个套接字，并且直接链接到本地20000的服务器上
		socket = new Socket("localhost",PORT);
		//新建一个套接字，并且直接链接到本地20000的服务器上
		socket = new Socket(Inet4Address.getLocalHost(), PORT);
		//新建一个套接字，并且直接链接到本地20000的服务器上，并且绑定到20001端口上
		socket = new Socket("localhost", PORT, Inet4Address.getLocalHost(), LOCAL_PORT);
		socket = new Socket(Inet4Address.getLocalHost(),PORT,Inet4Address.getLocalHost(),LOCAL_PORT);
		**/
		Socket socket = new Socket();
		//绑定到本地20001端口
		socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), LOCAL_PORT));
		return socket;
	}
	private static void initSocket(Socket socket) throws Exception {
		//设置读取超时事件为3秒
		socket.setSoTimeout(3000);
		
		//是否复用未完全关闭的Socket地址，对于指定bind操作后的套接字有效
		socket.setReuseAddress(true);
		
		//是否开启Nagle算法
		socket.setTcpNoDelay(true);
		
		//是否需要在长时无数据响应时发送确认数据（类似心跳包），时间大约2小时
		socket.setKeepAlive(true);
		
		//对于close关闭操作行为进行怎样的处理；默认为false,0
		//fasle,0 :默认情况，关闭时立即返回，底层系统接管输出流，将缓冲区内的数据发送完成
		//true,0:关闭时立即返回，缓冲区数据抛弃，直接发送RST接收命令到对方，无须经过2MSL等待
		//true,200:关闭时最长阻塞200毫秒，随后按第2种情况处理
		socket.setSoLinger(true, 200);
		
		//是否让紧急数据内敛，默认为false;紧急数据通过socket.sendUrgentData(1)发送
		socket.setOOBInline(true);
		
		//设置接收发送缓冲器大小
		socket.setReceiveBufferSize(64*1024*1024);
		socket.setSendBufferSize(64*1024*1024);
		
		//设置性能参数：短链接、延迟、贷款的相对重要性
		socket.setPerformancePreferences(1, 1, 1);
		
	}
}
