package com.casic.mq.receive;

import com.alibaba.fastjson.JSONArray;
import com.casic.mq.domain.MessageTemp;
import com.casic.mq.utils.SpringBootBeanUtil;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageReceiver implements ChannelAwareMessageListener {


   @Value("${server.ipAdd}")
   private String ipAdd;

   @Autowired
   RabbitTemplate rabbitTemplate;

   private List<?> list;

   @RabbitHandler
//   @RabbitListener(queues = QueueContent.ZB_CHANGE_PLAN_KEY)
   @Override
   public void onMessage(Message message, Channel channel) throws Exception {
      String appId = message.getMessageProperties().getAppId();
      long tag = message.getMessageProperties().getDeliveryTag();
      String type = message.getMessageProperties().getType();
      String[] types = type.split(";");
      if(types.length == 3) {
         try {
            //核对标识，决定是否消费消息
            if (!ipAdd.equals(appId)) {
               System.out.println("这不是RE需要的消息。放回队列.");
               channel.basicReject(tag, true);
               return;
            }

            //获取消息
            MessageConverter messageConverter = new SimpleMessageConverter();
            classToList(Class.forName(types[2]));
            list = JSONArray.parseArray((String) messageConverter.fromMessage(message),
               Class.forName(types[2]));

            ApplicationContext applicationContext = SpringBootBeanUtil.getApplicationContext();
            Class<?> serviceImplType = Class.forName(types[0]);
            Class<?> entityType = Class.forName(types[2]);

            Method method = serviceImplType.getDeclaredMethod(types[1], entityType);
            method.invoke(applicationContext.getBean(serviceImplType), list.get(0));

            channel.basicAck(tag, false);

         } catch (Exception e) {
            e.printStackTrace();
            System.out.println("RE消费这监听异常，转入错误队列");
            channel.basicNack(tag, false, true);
         }
      }
   }

   public void classToList(Class<?> clazz){
      List<Class<?>> list = new ArrayList<Class<?>>();
      this.list = list;
   }
}
