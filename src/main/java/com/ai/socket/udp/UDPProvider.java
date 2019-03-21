package com.ai.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

public class UDPProvider {
	public static void main(String[] args) throws IOException {
		//生成唯一ID
		String sn = UUID.randomUUID().toString();
		Provider provider = new Provider(sn);
		provider.start();
		
		//讀取任意字符
		System.in.read();
		provider.exit();
	}

	private static class Provider extends Thread {
		private final String sn;
		private boolean done = false;
		private DatagramSocket ds = null;

		public Provider(String sn) {
			super();
			this.sn = sn;
		}

		@Override
		public void run() {
			System.out.println("UDPProvider Started...");
			try {
				// 作为接收者，指定一个端口用于数据接收
				// 监听20000端口
				ds = new DatagramSocket(20000);
				while (!done) {
					// 构建接收实体
					final byte[] buf = new byte[512];
					DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

					// 接收
					ds.receive(receivePacket);

					// 打印接收到的信心与发送者的信息
					// 发送者的IP地址
					String  ip = receivePacket.getAddress().getHostAddress();
					int port = receivePacket.getPort();
					int dataLen = receivePacket.getLength();
					String data = new String(receivePacket.getData(), 0, dataLen);
					System.out.println("UDPProvider receive from ip:" + ip + ",port:" + port + "data:" + data);

					// 解析端口号
					int responsePort = MessageCreator.parsePort(data);

					if (responsePort != -1) {
						// 构建一份回送数据
						String responseData = MessageCreator.buildWithSn(sn);
						byte[] responseDataBytes = responseData.getBytes();
						// 直接根据发送者构建一份回送信息
						DatagramPacket responsePacket = new 
								DatagramPacket(responseDataBytes, responseDataBytes.length,
								receivePacket.getAddress(), responsePort);

						ds.send(responsePacket);
					}
				}
			} catch (Exception e) {
			} finally {
				close();
			}
			
			//完成
			System.out.println("UDPProvider Finished.");
		}

		private void close() {
			if (ds != null) {
				ds.close();
				ds = null;
			}
		}

		/**
		 * 结束方法
		 */
		void exit() {
			done = true;
			close();
		}
	}
}
