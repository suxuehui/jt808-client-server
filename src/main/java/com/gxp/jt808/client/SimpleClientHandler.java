/**  
 * @Title:  SimpleClientHandler.java
 */
package com.gxp.jt808.client;



import java.nio.ByteBuffer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.util.Assert;

import com.gxp.jt808.common.TPMSConsts;
import com.gxp.jt808.service.codec.MsgDecoder;
import com.gxp.jt808.service.codec.MsgEncoder;
import com.gxp.jt808.util.BCD8421Operater;
import com.gxp.jt808.util.BitOperator;
import com.gxp.jt808.util.HexStringUtils;
import com.gxp.jt808.util.JT808ProtocolUtils;
import com.gxp.jt808.vo.PackageData;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;




/**
 * 处理服务端返回的数据
 * 
 * @author Administrator
 *
 */
public class SimpleClientHandler extends ChannelInboundHandlerAdapter {
	private static Logger log = LoggerFactory.getLogger(SimpleClientHandler.class);

	public SimpleClientHandler() {

	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		ByteBuf buf = (ByteBuf) msg;
		if (msg==null || buf.readableBytes() <1) {
			// ReferenceCountUtil.safeRelease(msg);
			return;
		}
		try {
			byte[] bs = new byte[buf.readableBytes()];
			buf.readBytes(bs);
			//反转义
			bs = new JT808ProtocolUtils().doEscape4Receive(bs, 0, bs.length);
			// 字节数据转换为针对于808消息结构的实体类
			PackageData pkg =  new MsgDecoder().bytes2PackageData(bs);
			log.info("{}",pkg);
		}finally {
			buf.release();
		}
	}
	public  void channelActive(ChannelHandlerContext ctx) throws Exception{
		//连接成功

	}

	/**
	 * The {@link Channel} of the {@link ChannelHandlerContext} was registered is now inactive and reached its
	 * end of lifetime.
	 */
	public void channelInactive(ChannelHandlerContext ctx) throws Exception{
		//断开连接

	}
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		log.error("exceptionCaught",cause);
	}	
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		//log.info("userEventTriggered: "+ctx.hashCode()+","+evt.getClass());
		if (evt instanceof IdleStateEvent){
			IdleStateEvent event = (IdleStateEvent)evt;
			//log.info("state:{}",event.state());
			if (event.state()== IdleState.WRITER_IDLE){//写超时
				//发送数据给服务器
				String phone="013666665555";
				short msgid=(short)TPMSConsts.msg_id_terminal_heart_beat;
				short flowid=(short)System.currentTimeMillis();
				ByteBuffer buf=ByteBuffer.allocate(12);
				buf.putShort(msgid);
				buf.putShort((short)0);
				buf.put(BCD8421Operater.string2Bcd(phone));
				buf.putShort(flowid);
				byte[] data0=buf.array();
				// 校验码
				int checkSum = new BitOperator().getCheckSum4JT808(data0, 0, data0.length);
				byte[] data=new MsgEncoder().doEncode(data0, checkSum);
				ctx.channel().writeAndFlush(Unpooled.copiedBuffer(data));
				log.info("heartbeat msgid:{},flowid:{},checkSum:{},hex:{}",msgid,flowid & 0xffff,checkSum,HexStringUtils.toHexString(data0));
			}else if(event.state()==IdleState.READER_IDLE) {//读超时
				// 可以用 ch.pipeline().addLast(new ReadTimeoutHandler(20000,TimeUnit.MILLISECONDS));来实现
				this.exceptionCaught(ctx,ReadTimeoutException.INSTANCE);
			}
		}
	}

}