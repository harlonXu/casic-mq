package com.casic.mq.provider;

import com.alibaba.fastjson.JSON;
import com.casic.mq.constant.QueueConstant;
import com.casic.mq.domain.MessageTemp;
import com.casic.mq.enums.QueueEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageProvider {

   @Autowired
   RabbitTemplate rabbitTemplate;

   @Value("${server.ipAddTop}")
   private String ipAddTop;

   @Value("${server.ipAddSec1}")
   private String ipAddSec1;

   @Value("${server.ipAddSec2}")
   private String ipAddSec2;

   @Value("${server.ipAddSec3}")
   private String ipAddSec3;

   @Value("${server.ipAddSec4}")
   private String ipAddSec4;

   /**
    * @param list        对象数据(Json)
    * @param target      目标地址
    * @param domainName  实体类
    * @param serviceName service类
    * @param methodName  方法名
    */
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

   @Test
   public void sendSecondTopicMessage() {
      MessageProperties messageProperties = new MessageProperties();
      for (int i = 1; i <= 10; i++) {
         if (i % 2 == 0) {
            messageProperties.setAppId(ipAddSec1);
         } else {
            messageProperties.setAppId(ipAddSec2);
         }

         String messageData = "test second topic message," + i;

         //对象
         List<MessageTemp> list = new ArrayList<>();
         MessageTemp messageTemp = new MessageTemp();
         messageTemp.setMessage(messageData);
         list.add(messageTemp);
         String json = JSON.toJSONString(list);

         //字符串
         List<String> list2 = new ArrayList<>();
         String temp = "test second topic message";
         list2.add(temp);
         String json2 = JSON.toJSONString(list2);

         MessageConverter messageConverter = new SimpleMessageConverter();
         Message message = messageConverter.toMessage(json, messageProperties);
         //设置消息过期时间
         message.getMessageProperties().setExpiration("180000");

         String type = "com.casic.mq.service.HandsetServiceImpl"+";"
            +"updateData"+";"+messageTemp.getClass().getName();

         String type2 = "com.casic.mq.service.HandsetServiceImpl"+";"
            +"deleteData"+";"+temp.getClass().getName();

         message.getMessageProperties().setType(type);
         rabbitTemplate.send(QueueEnum.ZB_CHANGE_PLAN_MESSAGE_QUEUE.getExchange(),
            QueueEnum.ZB_CHANGE_PLAN_MESSAGE_QUEUE.getQueueName(), message);

         System.out.println("second ok:" + messageData);


         //保存消息到数据库中，作持久化处理

      }
   }
}
