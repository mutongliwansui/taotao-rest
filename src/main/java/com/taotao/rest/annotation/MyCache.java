package com.taotao.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE}) //作用于类和方法
@Retention(RetentionPolicy.RUNTIME) //运行时
public @interface MyCache {

    String cacheName(); //缓存名
    String key(); //键值
}
