package com.casic.mq.service;

public interface ISendMessageService {
   void sendMessage(String list, String target,
                           String domainName, String serviceName, String methodName);
}
