package com.casic.mq.constant;

public class QueueConstant {

   //zb转隶队列、交换器
   public final static String ZB_CHANGE_PLAN_KEY = "zbChange.plan";
   public final static String ZB_CHANGE_EXCHANGE_KEY = "zbChangeExchange";

   /**
    * //ttl队列交换器(暂未用)
    */
   public final static String FIRST_TOPIC_KEY = "topic.firstKey";
   public final static String TTL_SECOND_TOPIC_KEY = "topic.secondKeyTTL";
   public final static String TTL_TOPIC_EXCHANGE_KEY = "topicExchangeTTL";

}
