package com.comma.learnabout;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fanqi on 2018/6/26.
 * Description:回顾总结：常用的4种线程池（
 * 定长线程池（FixedThreadPool）
 定时线程池（ScheduledThreadPool ）
 可缓存线程池（CachedThreadPool）
 单线程化线程池（SingleThreadExecutor） ）。

 * 以及目前最为常用的可以自定义核心数的线程池(手动创建线程池)，这也是推荐的线程池使用方式。
 *
 * ExecutorService继承自Executor； public interface ExecutorService extends Executor；
 *
 *
 */

public class ThreadPoolLearn {

    //
    private Executor mExecutor;
    private Executors executors;
    private ExecutorService mExecutorService;



}
