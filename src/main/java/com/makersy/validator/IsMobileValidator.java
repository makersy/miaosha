package com.makersy.validator;
import  javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.makersy.utils.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 检验器。用来和注解绑定。参数1：注解 参数2：写入参数类型
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

	private boolean required = false;

	/**
	 * 取到注解的值。required：是否要求传入参数
	 */
	@Override
	public void initialize(IsMobile constraintAnnotation) {
		required = constraintAnnotation.required();
	}

	/**
	 * 判断是否合法
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(required) {
			//是必须，验证是否为有效手机号
			return ValidatorUtil.isMobile(value);
		} else {
			//如果不是必须，判断是否为空，不为空就进行验证
			if(StringUtils.isEmpty(value)) {
				return true;
			}else {
				return ValidatorUtil.isMobile(value);
			}
		}
	}

}
