package com.ai.socket.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket();
		//超时时间
		socket.setSoTimeout(3000);
		//连接本地，端口2000，超时事件3000ms
		socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 2000),3000);
		
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
		//构建键盘输入流
		InputStream in = System.in;
		BufferedReader input = new BufferedReader(new InputStreamReader(in));
		
		//得到Socket输出流，并转换为打印流
		OutputStream outputStream = client.getOutputStream();
		PrintStream socketPrintStream = new PrintStream(outputStream);
		
		//得到Socket输入流
		InputStream inputStream = client.getInputStream();
		BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		boolean flag = true;
		do {
			//键盘读取一行
			String str = input.readLine();
			//发送到服务器
			socketPrintStream.println(str);
			socketPrintStream.flush();
			
			//从服务器读取一行
			String echo = socketBufferedReader.readLine();
			if("bye".equalsIgnoreCase(echo)) {
				flag = false;
			}else {
				System.out.println(echo);
			}
		}while(flag);
		
	}
}
