package com.makersy.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

//自定义手机格式检验

/**
 * @author yhl
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IsMobileValidator.class })  //绑定校验器
public @interface IsMobile {

	//允许不传参数。默认为必须有
	boolean required() default true;
	
	String message() default "手机号码格式错误";  //默认校验不通过时的值

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
