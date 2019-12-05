package com.gxp.jt808.service.codec;

import java.util.Arrays;

import com.gxp.jt808.common.TPMSConsts;
import com.gxp.jt808.util.BitOperator;
import com.gxp.jt808.util.JT808ProtocolUtils;
import com.gxp.jt808.vo.PackageData;
import com.gxp.jt808.vo.Session;
import com.gxp.jt808.vo.req.TerminalRegisterMsg;
import com.gxp.jt808.vo.resp.ServerCommonRespMsgBody;
import com.gxp.jt808.vo.resp.TerminalRegisterMsgRespBody;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MsgEncoder {
	private BitOperator bitOperator;
	private JT808ProtocolUtils jt808ProtocolUtils;

	public MsgEncoder() {
		this.bitOperator = new BitOperator();
		this.jt808ProtocolUtils = new JT808ProtocolUtils();
	}

	public byte[] encode4TerminalRegisterResp(TerminalRegisterMsg req, TerminalRegisterMsgRespBody respMsgBody,
			int flowId) throws Exception {
		// 消息体字节数组
		byte[] msgBody = null;
		// 鉴权码(STRING) 只有在成功后才有该字段
		if (respMsgBody.getReplyCode() == TerminalRegisterMsgRespBody.success) {
			msgBody = this.bitOperator.concatAll(Arrays.asList(//
					bitOperator.integerTo2Bytes(respMsgBody.getReplyFlowId()), // 流水号(2)
					new byte[] { respMsgBody.getReplyCode() }, // 结果
					respMsgBody.getReplyToken().getBytes(TPMSConsts.string_charset)// 鉴权码(STRING)
					));
		} else {
			msgBody = this.bitOperator.concatAll(Arrays.asList(//
					bitOperator.integerTo2Bytes(respMsgBody.getReplyFlowId()), // 流水号(2)
					new byte[] { respMsgBody.getReplyCode() }// 错误代码
					));
		}

		// 消息头
		int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(req.getMsgHeader().getTerminalPhone(),
				TPMSConsts.cmd_terminal_register_resp, msgBody, msgBodyProps, flowId);
		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);

		// 校验码
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}

	// public byte[] encode4ServerCommonRespMsg(TerminalAuthenticationMsg req,
	// ServerCommonRespMsgBody respMsgBody, int flowId) throws Exception {
	public byte[] encode4ServerCommonRespMsg(PackageData req, ServerCommonRespMsgBody respMsgBody, int flowId)
			throws Exception {
		byte[] msgBody = this.bitOperator.concatAll(Arrays.asList(//
				bitOperator.integerTo2Bytes(respMsgBody.getReplyFlowId()), // 应答流水号
				bitOperator.integerTo2Bytes(respMsgBody.getReplyId()), // 应答ID,对应的终端消息的ID
				new byte[] { respMsgBody.getReplyCode() }// 结果
				));

		// 消息头
		int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(req.getMsgHeader().getTerminalPhone(),
				TPMSConsts.cmd_common_resp, msgBody, msgBodyProps, flowId);
		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
		// 校验码
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}

	public byte[] encode4ParamSetting(byte[] msgBodyBytes, Session session) throws Exception {
		// 消息头
		int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBodyBytes.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(session.getTerminalPhone(),
				TPMSConsts.cmd_terminal_param_settings, msgBodyBytes, msgBodyProps, session.currentFlowId());
		// 连接消息头和消息体
		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBodyBytes);
		// 校验码
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}
	//转义 0x7d <====> 0x7d01  , 0x7e <====> 0x7d02
	public byte[] doEncode(byte[] bs, int checkSum) throws Exception {
		ByteBuf buf=Unpooled.buffer(bs.length+3);
		buf.writeByte(TPMSConsts.pkg_delimiter);
		for (int i = 0; i <  bs.length; i++) {
			if (bs[i] == 0x7e) {
				buf.writeByte(0x7d);
				buf.writeByte(0x02);
			}else if (bs[i] == 0x7d) {
				buf.writeByte(0x7d);
				buf.writeByte(0x01);
			}else {
				buf.writeByte(bs[i]);
			}
		}
		if (checkSum == 0x7e) {
			buf.writeByte(0x7d);
			buf.writeByte(0x02);
		}else if (checkSum == 0x7d) {
			buf.writeByte(0x7d);
			buf.writeByte(0x01);
		}else {
			buf.writeByte(checkSum);
		}

		buf.writeByte(TPMSConsts.pkg_delimiter);
		byte[] data=Unpooled.copiedBuffer(buf).array();
		buf.release();
		return data;
	}
}
