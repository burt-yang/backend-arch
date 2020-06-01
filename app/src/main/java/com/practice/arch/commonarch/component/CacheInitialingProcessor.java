/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component;

import com.google.common.collect.Maps;
import com.practice.arch.commonarch.component.redis.lock.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by byang059 on 6/1/20
 */
@Slf4j
@Component
public class CacheInitialingProcessor implements BeanPostProcessor, Ordered, SmartInitializingSingleton {

    public static Map<Method, LockDTO> lockInfo = Maps.newHashMap();

    @Override
    public void afterSingletonsInstantiated() {
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AopInfrastructureBean) {
            // Ignore AOP infrastructure such as scoped proxies.
            return bean;
        }

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!AnnotationUtils.isCandidateClass(targetClass, DistributedLock.class)) {
            return bean;
        }

        Set<Method> methods = MethodIntrospector.selectMethods(targetClass, (ReflectionUtils.MethodFilter) method -> method.isAnnotationPresent(DistributedLock.class));
        // Non-empty set of methods
        methods.forEach(method -> processAnnotation(method, bean));
        if (log.isDebugEnabled()) {
            log.debug(methods.size() + " @DistributedLock methods processed on bean '" + beanName +
                    "': " + methods);
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    protected void processAnnotation(Method method, Object bean) {
        Method invocableMethod = AopUtils.selectInvocableMethod(method, bean.getClass());
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        String name = distributedLock.name();
        if (StringUtils.isEmpty(name)) {
            name = UUID.randomUUID().toString();
        }
        LockDTO dto = LockDTO.builder().leaseTime(distributedLock.leaseTime()).name(name).timeUnit(distributedLock.timeUnit()).waitTime(distributedLock.waitTime()).build();
        lockInfo.put(invocableMethod, dto);
    }
}