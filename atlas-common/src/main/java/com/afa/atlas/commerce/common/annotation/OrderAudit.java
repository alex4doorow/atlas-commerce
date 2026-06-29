package com.afa.atlas.commerce.common.annotation;

import com.afa.atlas.commerce.common.enums.OrderOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderAudit {

    OrderOperation operation();
}