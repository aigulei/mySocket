package com.ai.socket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(2000);
		System.out.println("服务器准备就绪~");
		System.out.println("服务器信息："+server.getInetAddress()+"P:"+server.getLocalPort());
		
		//等待客户端连接
		for(;;) {
			//得到客户端
			Socket client = server.accept();
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
				PrintStream socketOutput = new PrintStream(socket.getOutputStream());
				BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				do {
					//客户端拿到一条数据
					String str = socketInput.readLine();
					if("bye".equalsIgnoreCase(str)) {
						flag = false;
						socketOutput.println("bye");
					}else {
						System.out.println(str);
						socketOutput.println("回送:"+str.length());
					}
				}while(flag);
				socketInput.close();
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
}
