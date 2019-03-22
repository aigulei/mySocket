package com.ai.socket.tcp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import com.ai.socket.util.Tools;


public class Server {
	private static final int PORT = 20000;
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = createServerSocket();
		initServerSocket(serverSocket);
		serverSocket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), PORT),50);
		System.out.println("服务器准备就绪~");
		System.out.println("服务器信息："+serverSocket.getInetAddress()+"P:"+serverSocket.getLocalPort());
		
		//等待客户端连接
		for(;;) {
			//得到客户端
			Socket client = serverSocket.accept();
			//客户端构建异步线程
			ClientHandler clientHandler = new ClientHandler(client);
			//启动线程
			clientHandler.start();
		}
	}
	private static class ClientHandler extends Thread{
		private Socket socket;
		private boolean flag = true;
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
			System.out.println("新客户端连接:"+socket.getInetAddress()+"p:"+socket.getPort());
			try {
				//得到套接字流
				OutputStream outputStream = socket.getOutputStream();
				InputStream inputStream = socket.getInputStream();
				byte[] buffer = new byte[256];
				int read = inputStream.read(buffer);
				ByteBuffer byteBuffer = ByteBuffer.wrap(buffer,0, read);
				
				//byte
				byte be = byteBuffer.get();
				
				//char
				char c = byteBuffer.getChar();
				
				//int
				int i = byteBuffer.getInt();
				
				//boolean
				boolean b = byteBuffer.get()==1;
				
				//long
				long l = byteBuffer.getLong();
				
				//float
				float f = byteBuffer.getFloat();
				
				//double 
				double d = byteBuffer.getDouble();
				
				//String
				int pos = byteBuffer.position();
				String str = new String(buffer,pos,read-pos-1);
				
				System.out.println("收到数量："+read+",数据:"
						+be+"\n"
						+c+"\n "
						+i+"\n "
						+b+"\n "
						+l+"\n "
						+f+"\n "
						+d+"\n "
						+str+"\n "
						);
				outputStream.write(buffer,0,read);
				outputStream.close();
				inputStream.close();
				
			} catch (Exception e) {
				System.out.println("连接异常："+e.getMessage());
			}finally {
				try {
					socket.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			System.out.println("客户端已退出:"+socket.getInetAddress()+"P："+socket.getPort());
		}
	}
	private static ServerSocket createServerSocket() throws Exception {
		//创建基础的ServerSocket
		ServerSocket serverSocket = new ServerSocket();
		//绑定到本地端口上
		//serverSocket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), PORT),50);
		
		//绑定到本地端口20000上，并且设置当前可允许等待链接的队列为50个
		//serverSocket = new ServerSocket(PORT);
		//等效于上面的方案，队列设置为50个
		//serverSocket = new ServerSocket(PORT, 50);
		//与上面等同
		//serverSocket = new ServerSocket(PORT, 50, Inet4Address.getLocalHost());
		return serverSocket;
	}
	private static void initServerSocket(ServerSocket serverSocket) throws Exception {
		//是否复用未完全关闭的地址端口
		serverSocket.setReuseAddress(true);
		
		//等效Socket#setReceiveBufferSize
		serverSocket.setReceiveBufferSize(64*1024*1024);
		
		//设置serverSocket#accpet超时事件
		//serverSocket.setSoTimeout(2000);
		
		//设置性能参数：短链接、延迟、带宽的相对重要性
		serverSocket.setPerformancePreferences(1, 1, 1);
		
	}
	
}
