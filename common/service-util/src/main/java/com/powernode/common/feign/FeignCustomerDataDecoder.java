package com.powernode.common.feign;

import com.powernode.common.result.Result;
import com.powernode.common.result.ResultCodeEnum;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * feign数据解码器
 */
public class FeignCustomerDataDecoder implements Decoder {

    private SpringDecoder decoder;

    public FeignCustomerDataDecoder(SpringDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        //fegin发起远程调用之后的结果赋值给object
        Object object = this.decoder.decode(response, type);

        if (null == object) {
            throw new DecodeException(response.status(), "decode error", response.request());
        }

        if (object instanceof Result<?>) {
            Result<?> result = (Result<?>)object;

            //判断结果的状态码是否为200
            if (result.getCode() != ResultCodeEnum.SUCCESS.getCode().intValue()) {
                throw new DecodeException(result.getCode(), result.getMessage(), response.request());
            }

            return result;
        }



        return object;
    }
}
