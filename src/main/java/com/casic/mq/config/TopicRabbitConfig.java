package com.casic.mq.config;

import com.casic.mq.enums.QueueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TopicRabbitConfig {

   private final Logger logger = LoggerFactory.getLogger(TopicRabbitConfig.class);

   @Bean
   public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                              MessageConverter messageConverter) {
      SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
      factory.setConnectionFactory(connectionFactory);
      factory.setMessageConverter(messageConverter);
      factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
      return factory;
   }

   @Bean
   public MessageConverter messageConverter() {
      return new Jackson2JsonMessageConverter();
   }

   @Bean
   public Queue zbChangePlanQueue() {
      return new Queue(QueueEnum.ZB_CHANGE_PLAN_MESSAGE_QUEUE.getQueueName(), true, false, false);
   }

   @Bean
   TopicExchange zbChangeExchange() {
      return new TopicExchange(QueueEnum.ZB_CHANGE_PLAN_MESSAGE_QUEUE.getExchange(), true, false);
   }

      //将secondQueue和topicExchange绑定，绑定值ZB_CHANGE_PLAN_KEY
   @Bean
   Binding zbChangeBinding() {
      return BindingBuilder.bind(zbChangePlanQueue()).to(zbChangeExchange()).with(QueueEnum.ZB_CHANGE_PLAN_MESSAGE_QUEUE.getRouteKey());
   }

   @Bean
   public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
      RabbitTemplate rabbitTemplate = new RabbitTemplate();
      rabbitTemplate.setConnectionFactory(connectionFactory);

      //设置开启mandatory,才能触发回调函数，无论消息推送结果怎么样都强制调用回调函数
      rabbitTemplate.setMandatory(true);

      rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
         @Override
         public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            logger.info("ConfirmCallBack相关数据:{}",correlationData);
            logger.info("ConfirmCallBack确认情况:{}",ack);
            logger.info("ConfirmCallBack原因:{}",cause);


            //设置消费者消息消费成功，修改数据库消息状态


         }
      });

      rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
         @Override
         public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            logger.info("ReturnCallback消息:{}", message);
            logger.info("ReturnCallback回应码:{}", replyCode);
            logger.info("ReturnCallback回应信息:{}", replyText);
            logger.info("ReturnCallback交换机:{}", exchange);
            logger.info("ReturnCallback路由键:{}", routingKey);
         }
      });
      return rabbitTemplate;
   }


   /**
    * 暂未使用
    *
    * @return
    */
//   //TTL
//   @Bean
//   TopicExchange topicTTLExchange() {
//      return (TopicExchange) ExchangeBuilder
//         .topicExchange(QueueEnum.MESSAGE_TTL_QUEUE.getExchange()).durable(true).build();
//   }
//
//   //TTL
//   @Bean
//   public Binding messageTtlBinding(Queue messageTtlQueue, TopicExchange topicTTLExchange) {
//      return BindingBuilder
//         .bind(messageTtlQueue)
//         .to(topicTTLExchange)
//         .with(QueueEnum.MESSAGE_TTL_QUEUE.getRouteKey());
//   }
//
//   //TTL
//   @Bean
//   Queue messageTtlQueue() {
//      return QueueBuilder
//         .durable(QueueEnum.MESSAGE_TTL_QUEUE.getName())
//         // 配置到期后转发的交换
//         .withArgument("x-dead-letter-exchange", QueueEnum.MESSAGE_QUEUE.getExchange())
//         // 配置到期后转发的路由键
//         .withArgument("x-dead-letter-routing-key", QueueEnum.MESSAGE_QUEUE.getRouteKey())
//         .build();
//   }

//   @Bean
//   public Queue firstQueue() {
//      return new Queue(QueueContent.FIRST_TOPIC_KEY, true, false, false);
//   }
//
//   //绑定firstQueue和topicExchange,且绑定键值为topic.firstKey
//   //只有消息携带的路由键是topic.firstKey,才会分发到该队列
//   @Bean
//   Binding firstTopicBinding() {
//      return BindingBuilder.bind(firstQueue()).to(zbChangeExchange()).with(QueueContent.FIRST_TOPIC_KEY);
//   }
}

