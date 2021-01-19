package com.jsy.community.util;

import com.jsy.community.constant.BusinessConst;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 租赁配置类
 * @author YuLF
 * @since 2021-01-19 11:36
 */
@Configuration
public class LeaseConfig {


    /**
     * 自定义线程池配置
     * [拒绝策略]
     * rejectedExecutionHandler参数字段用于配置绝策略，常用拒绝策略如下
     * AbortPolicy：它将抛出RejectedExecutionException
     * CallerRunsPolicy：，它直接在execute方法的调用线程中运行被拒绝的任务。
     * DiscardOldestPolicy：它放弃最旧的未处理请求，然后重试execute。
     * DiscardPolicy：默认情况下它将丢弃被拒绝的任务
     *
     * [使用方法]
     * 需要异步的方法上面增加：@Async(BusinessConst.LEASE_ASYNC_POOL) 注解
     * 建议异步使用线程执行的方法 放在Service 接口里面，像其他业务方法一样 实现 其实现方法
     * 而只在接口上面增加@Async(BusinessConst.LEASE_ASYNC_POOL) 注解
     * 或者单独使用一个类来执行异步任务，而这个类使用@Component注解修饰
     *
     * [踩坑记录]
     * 1.异步注解的方法如果返回类型是void,那么该方法不能将异步方法执行产生的异常信息传递到异步方法的调用方默认情况下，这些未捕获的异常只能在日志中记录。
     * 2.如果异步方法需要有返回值，则只接受 Future<?> 类型
     * 3.@Async所修饰的函数不要定义为static类型，这样异步调用不会生效
     * 4.@Async修饰的方法 不能放在和调用方同类里面，比如在Controller，需要放在其他受Spring管理的类里面
     */
    @Bean(BusinessConst.LEASE_ASYNC_POOL)
    public ThreadPoolTaskExecutor leaseTaskActuator() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //获取到服务器的cpu内核 用于计算线程池设定线程最优方案
        int cpuCore = Runtime.getRuntime().availableProcessors();

        //核心池大小   表示可异步执行线程的数量，如果请求数量超过 则该任务会进入到队列等待
        //按执行一个任务0.2s,按系统80%的时间每秒都会产生100个任务
        executor.setCorePoolSize(20);

        //最大线程数   当线程数>=corePoolSize，且任务队列已满时。线程池会创建新线程来处理任务。当线程数=maxPoolSize，且任务队列已满时，线程池会使用拒绝策略。
        executor.setMaxPoolSize(80);

        //按每个任务执行时间为0.2s  核心线程/单个任务执行时间*2  队列200
        executor.setQueueCapacity(200);

        //线程空闲时间  ：当超过了核心线程数之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(30);
        //关机时等待任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程前缀名称
        executor.setThreadNamePrefix("Lease-Async-Thread");
        //配置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //设置等待终止秒数
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

}
