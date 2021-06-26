package com.jsy.community.listener.handler;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
* @Description: 消息监听处理类
 * @Author: chq459799974
 * @Date: 2021/6/26
**/
@Component
public class MessageConsumerHandler implements ChannelAwareMessageListener {

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		System.out.println("统一消息处理类收到消息：" + new String(message.getBody()));
	}

}
