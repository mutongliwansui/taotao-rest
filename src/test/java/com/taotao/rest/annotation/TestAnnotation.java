package com.taotao.rest.annotation;

import org.junit.Test;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.reflect.Method;

public class TestAnnotation {

    @MyCache(cacheName = "TestCache",key = "#param")
    public Long Hello(Long param){
        return param;
    }

    @Test
    public void testAnnoMycache() throws NoSuchMethodException {
        Class cls = TestAnnotation.class;
        Method mtd = cls.getMethod("Hello",Long.class);
        MyCache anno = mtd.getAnnotation(MyCache.class);
        String []paramNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(mtd);
        System.out.println("paramNames:"+paramNames[0]);
        String cacheName = anno.cacheName();
        String key = anno.key();
        System.out.println("cacheName:"+cacheName+"||key:"+key);
    }
}
interface TestInterfaceAnnotation{
    public Long Hello(Long param);
}
