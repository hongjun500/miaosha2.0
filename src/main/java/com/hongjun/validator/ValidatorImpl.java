package com.hongjun.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author hongjun500
 * @date 2020/6/21 20:45
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
@Component
public class ValidatorImpl implements InitializingBean {

    private Validator validator;

    // 实现校验方法并返回校验结果
    public ValidationResult validate(Object obj){
        ValidationResult validationResult = new ValidationResult();
        Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(obj);
        if (constraintViolationSet.size() > 0){
            // 有错误
            validationResult.setHasErrors(true);
            constraintViolationSet.forEach(constraintViolation->{
                // 错误信息
                String errMsg = constraintViolation.getMessage();

                // 错误字段
                String propertyName = constraintViolation.getPropertyPath().toString();

                validationResult.getErrorMsgMap().put(propertyName, errMsg);
            });
        }
        return validationResult;
    }


    /**
     * 当spring Bean初始化完成之后会回调这个ValidatorImpl
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 将hibernate的validator通过工厂的初始化方式使其实例化
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
