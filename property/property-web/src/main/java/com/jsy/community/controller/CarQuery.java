package com.jsy.community.controller;

import lombok.Data;

@Data
public class CarQuery {
   private Integer error_num;
   private String         error_str;
   private String passwd;
   private String         gpio_data;
   private String rs485_data;
   private String         triger_data;
   private String whitelist_data;
}
