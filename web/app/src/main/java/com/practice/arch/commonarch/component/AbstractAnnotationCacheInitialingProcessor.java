/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Created by byang059 on 6/1/20
 */
@Slf4j
public abstract class AbstractAnnotationCacheInitialingProcessor<T extends Annotation, DTO> implements BeanPostProcessor, Ordered, SmartInitializingSingleton {

    protected Map<Method, DTO> annotationPropertiesMap = Maps.newHashMap();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AopInfrastructureBean) {
            // Ignore AOP infrastructure such as scoped proxies.
            return bean;
        }

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        Class<T> annotationClass = getAnnotationClass();
        if (!AnnotationUtils.isCandidateClass(targetClass, annotationClass)) {
            return bean;
        }

        Set<Method> methods = MethodIntrospector.selectMethods(targetClass, (ReflectionUtils.MethodFilter) method -> method.isAnnotationPresent(annotationClass));
        // Non-empty set of methods
        methods.forEach(method -> {
            Method invocableMethod = AopUtils.selectInvocableMethod(method, bean.getClass());
            T distributedLock = method.getAnnotation(annotationClass);
            DTO dto = processAnnotation(distributedLock);
            annotationPropertiesMap.put(invocableMethod, dto);
        });
        if (log.isDebugEnabled()) {
            log.debug("{} @{} methods processed on bean {}: {}" + methods, methods.size(), annotationClass.getSimpleName(), beanName, methods);
        }
        return bean;
    }

    @Override
    public void afterSingletonsInstantiated() {
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    public abstract Class<T> getAnnotationClass();

    public abstract DTO processAnnotation(T annotation);
}