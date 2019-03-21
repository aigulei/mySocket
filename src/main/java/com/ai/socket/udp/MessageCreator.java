package com.ai.socket.udp;

/**
 * 消息创建
 * @author Administrator
 *
 */
public class MessageCreator {
	private static final String SN_HEADER ="收到暗号，我是(SN):";
	private static final String PORT_HEARER = "这是暗号，请回电端口(Port):";
	
	public static String buildWithPort(int port) {
		return PORT_HEARER + port;
	}
	
	public static int parsePort(String data) {
		if(data.startsWith(PORT_HEARER)) {
			return Integer.parseInt(data.substring(PORT_HEARER.length()));
		}
		return -1;
	}
	
	public static String buildWithSn(String sn) {
		return SN_HEADER + sn;
	}
	
	public static String parseSn(String data) {
		if(data.startsWith(SN_HEADER)) {
			return data.substring(SN_HEADER.length());
		}
		return null;
	}
}
