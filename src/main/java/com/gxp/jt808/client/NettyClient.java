/**  
 * @Title:  NettyClient.java
 */
package com.gxp.jt808.client;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.util.Assert;

import com.gxp.jt808.common.TPMSConsts;
import com.gxp.jt808.service.codec.MsgEncoder;
import com.gxp.jt808.util.BCD8421Operater;
import com.gxp.jt808.util.BitOperator;
import com.gxp.jt808.util.DateUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;


/**
 * Netty客户端编写
 * @author Administrator
 *
 */
public class NettyClient {
	private static Logger log = LoggerFactory.getLogger(NettyClient.class);
	private EventLoopGroup group = null;
	private Bootstrap bootstrap = null;
	private Channel channel=null;
	private int port;
	private String host;
	private int readTimeout;//读超时（秒）
	private int heartTime;//心跳时间（秒）
	private boolean isConnect;
	private int connectTimes;//连接次数（秒）
	public NettyClient(String host,int port,int readTimeout,int connectTimes, int heartTime) {
		this.host=host;
		this.port=port;
		this.readTimeout=readTimeout;
		this.heartTime=heartTime;
		this.connectTimes=connectTimes;
	}

	/**
	 * 消息发送（异步)
	 * @author: gxp
	 * @date: 2019年11月29日 上午9:43:00 
	 * @param obj
	 * @throws Exception
	 */
	public  void sendMessage(byte[] data) throws Exception {
		if (data == null) {
			throw new IllegalArgumentException("data not null!");
		}
		sendMessage(Unpooled.copiedBuffer(data));
	}
	/**
	 * 发送消息
	 * @author: gxp
	 * @date: 2019年12月4日 上午9:51:14 
	 * @param msg
	 * @throws Exception
	 */
	public  void sendMessage(ByteBuf msg) throws Exception {
		if(!connect()) {
			throw new IOException("server is not connected! ");
		}
		channel.writeAndFlush(msg);
	}
	/**
	 * 消息发送，返回是否成功发送
	 * @author: gxp
	 * @date: 2019年11月29日 下午1:08:28 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public  boolean sendMessageSync(byte[] data) throws Exception {
		if(!connect()) {
			throw new IOException("server is not connected! ");
		}
		return  sendMessageSync(Unpooled.copiedBuffer(data));
	}
	/**
	 * 发送消息
	 * @author: gxp
	 * @date: 2019年12月4日 上午9:52:24 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public  boolean sendMessageSync(ByteBuf msg) throws Exception {
		if(!connect()) {
			throw new IOException("server is not connected! ");
		}
		return  channel.writeAndFlush(msg).sync().isSuccess();
	}

	/**
	 * 通道是否连接
	 * @author: gxp
	 * @date: 2019年11月28日 下午5:27:11 
	 * @return
	 */
	public boolean isConnect() {
		return isConnect && channel!=null && channel.isOpen() && channel.isActive();
	}
	/**
	 * 关闭通道
	 * @author: gxp
	 * @date: 2019年12月2日 下午5:52:00
	 */
	public void close() {
		try {
			//关闭客户端套接字
			if(channel!=null){
				channel.close();
			}
			//关闭客户端线程组
			if (group != null) {
				group.shutdownGracefully().await(0);
			}
			isConnect=false;
		} catch (Exception e) {

		}
	}
	/**
	 * 连接服务器
	 * @author: gxp
	 * @date: 2019年11月28日 下午4:36:15
	 */
	public synchronized boolean connect() {
		int times=0;//连接次数（秒）
		while(!isConnect() && (connectTimes<1 || times<connectTimes)) {
			times++;
			try {
				if(times>1) {
					Thread.sleep(500);
				}
				if(!isConnect()) {
					close();
					bootstrap=new Bootstrap();
					group = new NioEventLoopGroup();
					bootstrap.group(group );
					//第2步 绑定客户端通道
					bootstrap.channel(NioSocketChannel.class);
					//messageDecoder=new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]);
					//第3步 给NIoSocketChannel初始化handler， 处理读写事件
					bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {  //通道是NioSocketChannel
						@Override
						protected void initChannel(NioSocketChannel ch) throws Exception {
							//读取超时 在设置时间内没有读取操作(断开连接) (继承IdleStateHandler)
							if(readTimeout>0) {
								ch.pipeline().addLast(new ReadTimeoutHandler(readTimeout*1000,TimeUnit.MILLISECONDS));
							}
							//读写超时机制
							//ch.pipeline().addLast(new IdleStateHandler(20,10,0));
							//写超时(心跳)
							if(heartTime>0) {
								ch.pipeline().addLast(new IdleStateHandler(0,heartTime,0));
							}
							//1)用于处理基于分隔符的协议和基于长度的协议的解码器
							//DelimiterBasedFrameDecoder 使用任何由用户提供的分隔符来提取帧的通用解码器。
							//LineBasedFrameDecoder 提取由行尾符（\n或者\r\n）分隔的帧的解码器。这个解码器比DelimiterBasedFrameDecoder更快。
							//2用于基于长度的协议的解码器
							//FixedLengthFrameDecoder 提取在调用构造函数时指定的定长帧
							//LengthFieldBasedFrameDecoder 根据编码进帧头部中的长度值提取帧；该字段的偏移量以及长度在构造函数中指定

							//字符串编码器，一定要加在SimpleClientHandler 的上面
							///r/n分隔
							//ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0])); 
							ch.pipeline().addLast(
									new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer(new byte[] { 0x7e }),
											Unpooled.copiedBuffer(new byte[] { 0x7e, 0x7e })));

							//找到他的管道 增加他的handler
							ch.pipeline().addLast(new SimpleClientHandler());
						}
					});
					ChannelFuture future = bootstrap.connect(host, port).sync();//同步返回结果，否则不会报错，下面的发送也会不成功
					channel=future.channel();
					isConnect=future.isSuccess() && channel.isOpen() && channel.isActive();
					log.info("----connetc-----isConnect:{}",isConnect);

					//当通道关闭了，就继续往下走(可以进行重连接)
					//channel.closeFuture().sync();
					//接收服务端返回的数据(用于同步返回数据处理)
					//AttributeKey<String> key = AttributeKey.valueOf("ServerData");
					//Object result = channel.attr(key).get();
				}
			} catch (Exception e) {
				log.error("connetc error:{}",e.getMessage());
				isConnect=false;
			}
		}
		return isConnect();
	}
	private static ByteBuf getMsg() throws Exception {
		String phone="013666665555";
		short msgid=(short)TPMSConsts.msg_id_terminal_location_info_upload;
		short flowid=(short)System.currentTimeMillis();
		ByteBuffer buff=ByteBuffer.allocate(40);
		buff.putShort(msgid);
		buff.putShort((short)28);
		buff.put(BCD8421Operater.string2Bcd(phone));
		buff.putShort(flowid);
		buff.putInt(new Random().nextInt());
		buff.putInt(new Random().nextInt());
		buff.putInt(new Random().nextInt());
		buff.putInt(new Random().nextInt());
		buff.putShort((short)new Random().nextInt());
		buff.putShort((short)new Random().nextInt());
		buff.putShort((short)new Random().nextInt());
		buff.put(BCD8421Operater.string2Bcd(DateUtil.getNowTime("yyMMddHHmmss")));
		byte[] data=buff.array();
		// 校验码
		int checkSum =  new BitOperator().getCheckSum4JT808(data, 0, data.length);
		data=new MsgEncoder().doEncode(data, checkSum);
		log.info("-send msgid:{},flowid:{},checkSum:{}",msgid,flowid & 0xffff,checkSum);
		return Unpooled.copiedBuffer(data);
	}
	public static void main(String[] args) {
		String host="127.0.0.1";
		int port=20048;
		int readTimeout=20;
		int connectTimes=1;
		int heartTime=10;
		NettyClient client=new NettyClient(host,port,readTimeout,connectTimes,heartTime);
		try {
			client.connect();
			for(int i=0;i<1;i++) {
				client.sendMessage(getMsg());
		     }
		} catch (Exception e) {
			log.error("main error",e);
		}
	}

}