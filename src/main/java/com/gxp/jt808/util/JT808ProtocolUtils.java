package com.gxp.jt808.util;

import java.io.ByteArrayOutputStream;

//import org.apache.commons.fileupload.UploadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * JT808协议转义工具类
 * 
 * <pre>
 * 0x7d01 <====> 0x7d
 * 0x7d02 <====> 0x7e
 * </pre>
 * 
 * @author hylexus
 *
 */
public class JT808ProtocolUtils {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private BitOperator bitOperator;
	

	public JT808ProtocolUtils() {
		this.bitOperator = new BitOperator();
		
	}

	/**
	 * 接收消息时转义<br>
	 * 
	 * <pre>
	 * 0x7d01 <====> 0x7d
	 * 0x7d02 <====> 0x7e
	 * </pre>
	 * 
	 * @param bs
	 *            要转义的字节数组
	 * @param start
	 *            起始索引
	 * @param end
	 *            结束索引
	 * @return 转义后的字节数组
	 * @throws Exception
	 */
	public byte[] doEscape4Receive(byte[] bs, int start, int length) throws Exception {
		if (start < 0 || length > bs.length)
			throw new ArrayIndexOutOfBoundsException("doEscape4Receive error : index out of bounds(start=" + start
					+ ",end=" + length + ",bytes length=" + bs.length + ")");
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			for (int x = 0; x < start; x++) {
				baos.write(bs[x]);
			}
			int i= start;
			for (;i < length - 1; i++) {
				if (bs[i] == 0x7d && bs[i + 1] == 0x02) {
					baos.write(0x7e);
					i++;
				} else if (bs[i] == 0x7d && bs[i + 1] == 0x01) {
					baos.write(0x7d);
					i++;
				} else {
					baos.write(bs[i]);
				}
			}
			for (int y = i; y < bs.length; y++) {
				baos.write(bs[y]);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			if (baos != null) {
				baos.close();
				baos = null;
			}
		}
	}
	/**
	 *  接收消息时转义<br>
	 * <pre>
	 * 0x7d01 <====> 0x7d
	 * 0x7d02 <====> 0x7e
	 * </pre>
	 * @author: gxp
	 * @date: 2019年11月27日 下午2:17:24 
	 * @param bs
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public byte[] doEscape4Receive(byte[] bs) throws Exception {
		byte[] data=null;
		if(bs!=null && bs.length>0) {
			ByteBuf buf=Unpooled.buffer(bs.length);
			for (int i = 0; i < bs.length-1; i++) {
				if (bs[i] == 0x7d && bs[i + 1] == 0x02) {
					buf.writeByte(0x7e);
					i++;
				} else if (bs[i] == 0x7d && bs[i + 1] == 0x01) {
					buf.writeByte(0x7d);
					i++;
				} else {
					buf.writeByte(bs[i]);
				}
			}
			data=Unpooled.copiedBuffer(buf).array();
		}
		return data;
	}

	public int generateMsgBodyProps(int msgLen, int enctyptionType, boolean isSubPackage, int reversed_14_15) {
		// [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
		// [10-12] 0001,1100,0000,0000(1C00)(加密类型)
		// [ 13_ ] 0010,0000,0000,0000(2000)(是否有子包)
		// [14-15] 1100,0000,0000,0000(C000)(保留位)
		if (msgLen >= 1024)
			log.warn("The max value of msgLen is 1023, but {} .", msgLen);
		int subPkg = isSubPackage ? 1 : 0;
		int ret = (msgLen & 0x3FF) | ((enctyptionType << 10) & 0x1C00) | ((subPkg << 13) & 0x2000)
				| ((reversed_14_15 << 14) & 0xC000);
		return ret & 0xffff;
	}

	public byte[] generateMsgHeader(String phone, int msgType, byte[] body, int msgBodyProps, int flowId)
			throws Exception {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			// 1. 消息ID word(16)
			baos.write(bitOperator.integerTo2Bytes(msgType));
			// 2. 消息体属性 word(16)
			baos.write(bitOperator.integerTo2Bytes(msgBodyProps));
			// 3. 终端手机号 bcd[6]
			baos.write(BCD8421Operater.string2Bcd(phone));
			// 4. 消息流水号 word(16),按发送顺序从 0 开始循环累加
			baos.write(bitOperator.integerTo2Bytes(flowId));
			// 消息包封装项 此处不予考虑
			return baos.toByteArray();
		} finally {
			if (baos != null) {
				baos.close();
			}
		}
	}
}
