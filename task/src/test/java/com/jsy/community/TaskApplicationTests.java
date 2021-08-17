package com.jsy.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@SpringBootTest
class TaskApplicationTests {

	@Test
	void contextLoads() {
		int i= 10;
//		for(i=10;i<20;i++){
//			System.out.println("锁执行");
//			System.out.println("");
//			
//			
//			}
	}
	@Test
	void g(){
		long l = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
		System.out.println(l+"ggg");

	}

}
