package com.casic.mq.enums;

import com.casic.mq.constant.QueueConstant;

public enum QueueEnum {

      /**
       * zb转隶计划消息通知队列
       */
      ZB_CHANGE_PLAN_MESSAGE_QUEUE(QueueConstant.ZB_CHANGE_EXCHANGE_KEY, QueueConstant.ZB_CHANGE_PLAN_KEY, QueueConstant.ZB_CHANGE_PLAN_KEY),
      /**
       * 消息通知ttl队列
       */
      MESSAGE_TTL_QUEUE(QueueConstant.TTL_TOPIC_EXCHANGE_KEY, QueueConstant.TTL_SECOND_TOPIC_KEY, QueueConstant.TTL_SECOND_TOPIC_KEY);

      /**
       * 交换名称
       */
      private String exchange;
      /**
       * 队列名称
       */
      private String queueName;
      /**
       * 路由键
       */
      private String routeKey;

   QueueEnum(String exchange, String queueName, String routeKey) {
      this.exchange = exchange;
      this.queueName = queueName;
      this.routeKey = routeKey;
   }

   public String getExchange() {
      return exchange;
   }

   public void setExchange(String exchange) {
      this.exchange = exchange;
   }

   public String getQueueName() {
      return queueName;
   }

   public void setQueueName(String queueName) {
      this.queueName = queueName;
   }

   public String getRouteKey() {
      return routeKey;
   }

   public void setRouteKey(String routeKey) {
      this.routeKey = routeKey;
   }
}
