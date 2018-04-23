package com.taotao.rest.aop;

import com.taotao.common.utils.JsonUtils;
import com.taotao.common.utils.SpelParser;
import com.taotao.rest.annotation.MyCache;
import com.taotao.rest.dao.JedisClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
public class MyCacheAspect {
    private static Logger LOGGER = Logger.getLogger(MyCacheAspect.class);

    @Autowired
    private JedisClient jedisClient;

    @Around("@annotation(myCache)") //配置切入点，被MyCache注解的方法
    public Object doCache(ProceedingJoinPoint pjp, MyCache myCache) throws Throwable {
        //获取key
        String key = getKey(myCache.key(),pjp);
        String cacheName = myCache.cacheName();
        //从缓存中取内容
        String result = jedisClient.hget(cacheName, key);
        if (!StringUtils.isBlank(result)) {
            //把字符串转换成Object
            Object ret = JsonUtils.jsonToPojo(result, Object.class);
            return ret;
        }

        Object ret  = pjp.proceed(); //执行主业务代码

        //向缓存中添加内容
        //把业务执行返回转换成JSON字符串
        String cacheString = JsonUtils.objectToJson(ret);
        jedisClient.hset(cacheName, key , cacheString);
        return ret;
    }

    /**
     * 解析key
     * @param key 键值，支持EL表达式
     * @param pjp
     * @return
     */
    private String getKey(String key, ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Class cls = pjp.getTarget().getClass(); //获取目标类信息
        MethodSignature mts = (MethodSignature)pjp.getSignature();
        String mtdname = mts.getName(); //获取方法名
        Method mtd = cls.getMethod(mtdname,mts.getParameterTypes()); //获取目标方法信息
//        String [] paramNames = ((MethodSignature)pjp.getSignature()).getParameterNames();
//        Method mtd = ((MethodSignature) pjp.getSignature()).getMethod(); //获取方法(由于获取的是接口的方法信息，无法获取形参名数组)
        String [] paramNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(mtd); //获取方法的形参名数组
        Object[] args = pjp.getArgs(); //获取形参值数组
        return SpelParser.parseEl(key, paramNames, args); //使用EL解析工具类进行解析
    }
}
