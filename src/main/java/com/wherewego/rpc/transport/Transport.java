package com.wherewego.rpc.transport;

import java.util.Arrays;

/**
 * 自定义的tcp通信协议（MyRPC）
 * @Author:lbl
 * @Date:Created in 22:40 2020/3/5
 * @Modified By:
 */
public class Transport {
    //请求头字节数
    public static final int HAND_LENGTH=5;
    //4个字节
    private int length=0;//消息体字节数
    //frameType和serializeType合占一个字节
    private byte frameType=0;//消息类型
    private byte serializeType=0;//消息体编码方式
    private byte[] bytes;//消息体

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getFrameType() {
        return frameType;
    }

    public void setFrameType(byte frameType) {
        this.frameType = frameType;
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

    @Override
    public String toString() {
        return "Transport{" +
                "length=" + length +
                ", frameType=" + frameType +
                ", serializeType=" + serializeType +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
