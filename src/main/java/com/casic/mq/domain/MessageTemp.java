package com.casic.mq.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageTemp implements Serializable {
   private String message;



   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }


}
