package com.wherewego.rpc.protocol;

import io.netty.channel.ChannelPipeline;

/**
 * 传输协议
 *
 * @Author:lbl
 * @Date:Created in 0:14 2020/3/7
 * @Modified By:
 */
public interface Protocol {
    void initChannel(ChannelPipeline pipeline);
}
