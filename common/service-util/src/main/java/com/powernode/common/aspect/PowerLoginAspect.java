package com.powernode.common.aspect;

import com.powernode.common.constant.RedisConstant;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.common.util.AuthContextHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 登录切面
 */
@Aspect
@Component
public class PowerLoginAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Around("execution(* com.powernode.*.controller.*.*(..)) && @annotation(com.powernode.common.annotation.PowerLogin)")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {

        //获取请求属性
        ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

        //获取请求头中的token
        HttpServletRequest request = sra.getRequest();
        String token = request.getHeader("token");

        if (!StringUtils.hasText(token)) {
            throw new PowerException(ResultCodeEnum.LOGIN_AUTH);
        }

        //从redis中获取用户主键
        String userId = stringRedisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX+token);
        if (!StringUtils.hasText(userId)) {
            throw new PowerException(ResultCodeEnum.LOGIN_AUTH);
        }

        //将userId存储  将当前 线程对应的用户id放入threadLocal中
        AuthContextHolder.setUserId(Long.parseLong(userId));


        return joinPoint.proceed();

    }

}
