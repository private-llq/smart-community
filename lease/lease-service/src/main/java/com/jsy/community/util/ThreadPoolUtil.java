package com.jsy.community.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author chq459799974
 * @description 线程池
 * @since 2021-01-15 10:48
 **/
@Slf4j
public class ThreadPoolUtil {
	public static ThreadPoolExecutor threadpool = new ThreadPoolExecutor(50, 500, 5, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(100000));
	
	public static void main(String[] args) {
		Future<?> submit = ThreadPoolUtil.threadpool.submit(new Runnable() {
			@Override
			public void run() {
				System.out.println(1);
			}
		});
		try {
			submit.get(1 * 500, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error("线程中断：id - " + Thread.currentThread().getId() + Thread.currentThread().getName());
			e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("线程执行异常：" + Thread.currentThread().getId() + Thread.currentThread().getName());
			e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("线程超时：id - " + Thread.currentThread().getId() + Thread.currentThread().getName());
			e.printStackTrace();
		}
		
	}
	
//	public static Thread findThread(long threadId) {
//		ThreadGroup group = Thread.currentThread().getThreadGroup();
//		while(group != null) {
//			Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
//			int count = group.enumerate(threads, true);
//			for(int i = 0; i < count; i++) {
//				if(threadId == threads[i].getId()) {
//					System.out.println(threads[i].getId() + threads[i].getName());
//					System.out.println(threads[i].isAlive());
//					return threads[i];
//				}
//			}
//			group = group.getParent();
//		}
//		return null;
//	}
}
