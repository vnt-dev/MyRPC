package com.wherewego.rpc.transport;

/**
 * 自定义的tcp通信协议（MyRPC）
 *
 * @Author:lbl
 * @Date:Created in 22:40 2020/3/5
 * @Modified By:
 */
public class Transport {
    //请求头四个字节
    public static final int HAND_LENGTH = 5;
    private int length;//头部
    private byte serializeType;//消息体编码方式
    private byte[] bytes;//消息体

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(byte serializeType) {
        this.serializeType = serializeType;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
