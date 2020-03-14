package com.wherewego.rpc.transport;

/**
 * 帧类型，0x00~0x70 共可用8个类型
 * @Author:lbl
 * @Date:Created in 19:04 2020/3/13
 * @Modified By:
 */
public interface FrameType {
    byte TEXT=0x10;//普通消息
    byte IDLE_NOT_ANSWER=0x20;//心跳检测,不回应
    byte IDLE_ANSWER=0x30;//心跳检测,需要回应
}
