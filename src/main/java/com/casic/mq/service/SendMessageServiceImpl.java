package com.casic.mq.service;

import com.casic.mq.enums.QueueEnum;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendMessageServiceImpl implements ISendMessageService {

   @Autowired
   RabbitTemplate rabbitTemplate;

   /**
    * @param list        对象数据(Json)
    * @param target      目标地址
    * @param domainName  实体类
    * @param serviceName service类
    * @param methodName  方法名
    */
   @Override
   public void sendMessage(String list, String target,
                           String domainName, String serviceName, String methodName) {
      String type = serviceName+";"+methodName+";"+domainName;
      MessageProperties messageProperties = new MessageProperties();
      messageProperties.setAppId(target);
      MessageConverter messageConverter = new SimpleMessageConverter();
      Message message = messageConverter.toMessage(list, messageProperties);
      //设置消息过期时间
      message.getMessageProperties().setExpiration("180000");
      message.getMessageProperties().setType(type);
      rabbitTemplate.send(QueueEnum.ZB_CHANGE_PLAN_MESSAGE_QUEUE.getExchange(),
         QueueEnum.ZB_CHANGE_PLAN_MESSAGE_QUEUE.getQueueName(), message);
   }
}
