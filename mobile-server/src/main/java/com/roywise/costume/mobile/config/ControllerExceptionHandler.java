package com.roywise.costume.mobile.config;

import com.roywise.common.exception.CommonException;
import com.roywise.mobile.module.comm.api.constants.ResultCodeConstants;
import com.roywise.mobile.module.comm.api.exception.MobileException;
import com.roywise.mobile.module.comm.api.vo.DefeatMsgVO;
import com.roywise.mobile.module.comm.api.vo.ErrorValidateMsgVO;
import com.roywise.mobile.module.comm.api.vo.ResultMsgVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * @description: 
 * @date: 2018/3/20 16:34
 */
@Log4j2
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResultMsgVO handleViolation(ConstraintViolationException exception) {
        log.error("",exception);
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        String msg = "input errror";
        for (ConstraintViolation<?> item : violations) {
            msg = item.getMessage();
            log.debug("params validate error!");
            break;
        }
        return new ErrorValidateMsgVO(msg);

    }

    @ExceptionHandler(CommonException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResultMsgVO handle(CommonException exception) {

        log.error("",exception);

        if (exception instanceof MobileException) {
            MobileException mobileException = (MobileException) exception;
            return new DefeatMsgVO(mobileException.getExtMsg());
        } else {
            return new ResultMsgVO(ResultCodeConstants.SERVER_BAD, exception.getErrorType().getText());
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultMsgVO handle(Exception exception) {

        log.error("", exception);
        return new ResultMsgVO(ResultCodeConstants.SERVER_BAD," Internal Server Error");

    }
}
