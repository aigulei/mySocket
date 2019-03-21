package com.ai.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;



public class UDPSearcher {
	private static final int LISTEN_PORT = 30000;
	
	public static void main(String[] args) throws Exception {
		System.out.println("UDPSearcher Started....");
		
		Listener listener = listen();
		sendBroadcast();
		
		//读取任意检票信息后可以退出
		System.in.read();
		List<Device> devices = listener.getDevicesAndClose();
		devices.forEach(d ->{
			System.out.println(d);
		});
		System.out.println("UDPSearcher Finished....");
	}
	private static Listener listen() throws InterruptedException{
		System.out.println("UDPSearcher listener start...");
		CountDownLatch countDownLatch = new CountDownLatch(1);
		Listener listener = new Listener(LISTEN_PORT, countDownLatch);
		listener.start();
		countDownLatch.await();
		return listener;
	}
	private static void sendBroadcast() throws Exception {
		System.out.println("UDPSearcher sendBroadCast Started...");
		//作为搜索方，让系统自动分配端口
		DatagramSocket ds = new DatagramSocket();
		//构建一份回送数据
		String requestData = MessageCreator.buildWithPort(LISTEN_PORT);
		byte[] requestDataBytes = requestData.getBytes();
		//直接根据发送者构建一份回送信息
		DatagramPacket requestPacket = new DatagramPacket(requestDataBytes,requestDataBytes.length);
		//20000端口，广播地址
		requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
		requestPacket.setPort(20000);
		
		//发送
		ds.send(requestPacket);
		ds.close();
		System.out.println("UDPSearcher sendBroadCast end...");
	}
	private static class Device{
		final int port;
		final String ip;
		final String sn;
		private Device(int port, String ip, String sn) {
			super();
			this.port = port;
			this.ip = ip;
			this.sn = sn;
		}
		@Override
		public String toString() {
			return "Device [port=" + port + ", ip=" + ip + ", sn=" + sn + "]";
		}
		
	}
	private static class Listener extends Thread{
		private final int listenPort;
		private final CountDownLatch countDownLatch;
		private final List<Device> deviceList = new ArrayList<>();
		private boolean done = false;
		private DatagramSocket ds = null;
		public Listener(int listenPort,CountDownLatch countDownLatch) {
			this.listenPort = listenPort;
			this.countDownLatch = countDownLatch;
		}
		@Override
		public void run() {
			super.run();
			//通知已启动
			countDownLatch.countDown();
			try {
				ds = new DatagramSocket(listenPort);
				while(!done) {
					//构建接收实体
					final byte[] buf = new byte[512];
					DatagramPacket receivePacket = new DatagramPacket(buf,buf.length);
					//接收
					ds.receive(receivePacket);
					//打印接收到的信心与发送者的信息
					//发送者的IP地址
					String ip = receivePacket.getAddress().getHostAddress();
					int port = receivePacket.getPort();
					int dataLen = receivePacket.getLength();
					String data = new String(receivePacket.getData(),0, dataLen);
					System.out.println("UDPSearch receive from ip:"+ip+"\tport:"+port+"\tdata:"+data);
					String sn = MessageCreator.buildWithSn(data);
					if(sn!=null) {
						Device device = new Device(port, ip, sn);
						deviceList.add(device);
					}
				}
			} catch (Exception e) {
			}finally {
				close();
			}
			System.out.println("UDPSearcher listener finished");
		}
		private void close() {
			if(ds!=null) {
				ds.close();
				ds = null;
			}
		}
		List<Device> getDevicesAndClose(){
			done = true;
			close();
			return deviceList;
		}
	}
}
