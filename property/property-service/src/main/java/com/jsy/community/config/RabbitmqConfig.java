package com.jsy.community.config;

import com.jsy.community.listener.handler.MessageConsumerHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.IOException;

/**
* @Description: Rabbitmq配置类
 * @Author: chq459799974
 * @Date: 2021/6/26
**/
@Configuration
@Slf4j
public class RabbitmqConfig {

	@Autowired
	private CachingConnectionFactory connectionFactory;

	@Autowired
	private MessageConsumerHandler handler;

//	@Autowired
//	private QueueService queueService;

//	@Bean
//	@Scope("prototype")
//	public RabbitTemplate rabbitTemplate(){
//		//若使用confirm-callback或return-callback，必须要配置publisherConfirms或publisherReturns为true
//		//每个rabbitTemplate只能有一个confirm-callback和return-callback，如果这里配置了，那么写生产者的时候不能再写confirm-callback和return-callback
//		//使用return-callback时必须设置mandatory为true，或者在配置中设置mandatory-expression的值为true
//		/**
//		 * 考虑到并发性，与 validErr 消息的 次要性，这里不使用 confirm 模式 和 return 模式
//		 * 如果使用这两个模式的话，会报异常 channelMax reached。
//		 * 如果后边需要这两个模式的话，再做解决，可以考虑通过Thread.sleep() 的方式，减少 channel 的积压
//		 */
//		connectionFactory.setPublisherConfirms(false);
//		connectionFactory.setPublisherReturns(false);
//
//		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//		rabbitTemplate.setMandatory(true);
////        /**
////         * 如果消息没有到exchange,则confirm回调,ack=false
////         * 如果消息到达exchange,则confirm回调,ack=true
////         * exchange到queue成功,则不回调return
////         * exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
////         */
//		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//			@Override
//			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//				if(ack){
//					log.debug("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
//				}else{
//					log.debug("消息发送失败:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
//				}
//			}
//		});
//		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
//			@Override
//			public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
//				log.debug("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",exchange,routingKey,replyCode,replyText,message);
//			}
//		});
//		return rabbitTemplate;
//	}

	@Bean
	public RabbitAdmin rabbitAdmin() {
		return new RabbitAdmin(connectionFactory);
	}

	@Bean
	@Order(value = 2)
	public SimpleMessageListenerContainer mqMessageContainer() throws AmqpException, IOException {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
//		List<String> list = queueService.getMQJSONArray();
//		container.setQueueNames(list.toArray(new String[list.size()]));
//		container.setExposeListenerChannel(true);
//		container.setPrefetchCount(1);//设置每个消费者获取的最大的消息数量
//		container.setConcurrentConsumers(100);//消费者个数
//		container.setAcknowledgeMode(AcknowledgeMode.AUTO);//设置确认模式为手工确认
		container.setMessageListener(handler);//监听处理类
		return container;
	}

//	@Bean
//	public void start() {
//		try {
//			mqMessageContainer().start();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
