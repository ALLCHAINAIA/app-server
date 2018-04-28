package com.roywise.costume.mobile.user.gateway.web;


import com.roywise.common.exception.CommonException;
import com.roywise.costume.business.user.api.model.MUserInfo;
import com.roywise.costume.business.user.api.service.MUserInfoService;
import com.roywise.costume.business.user.api.service.MUserLoginService;
import com.roywise.mobile.module.comm.api.service.AuthTokenService;
import com.roywise.mobile.module.comm.api.vo.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Log4j2
@RestController
@RequestMapping(value = "/v1/user/login")
@Validated
public class UserLoginController {

    @Resource
    private MUserLoginService mUserLoginService;

    @Resource
    private MUserInfoService mUserInfoService;

    @Resource
    private AuthTokenService authTokenService;

    private final static Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();


    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public ResultMsgVO register(@Size(max = 30, min = 3, message = "Please enter the correct email number") String userName,
                                @Size(max = 60, min = 6, message = "The user name is 6-60 characters in length") String password,
                                @Size(max = 30, min = 2, message = "The user name is 2-30 characters in length") String nickName)
            throws CommonException {

        if (StringUtils.isAnyBlank(userName, password, nickName)) {
            return new ErrorMissingParamsMsgVO("error! miss required parameter");
        }


        if (!this.isEmail(userName)) {
            return new ErrorValidateMsgVO("Incorrect email format.");
        }
        MUserInfo userInfo = mUserLoginService.register(userName, password);

        userInfo.setNickName(nickName);
        mUserInfoService.updateByPrimaryKeySelective(userInfo);

        return new SuccessMsgVO("Registration Successful.");
    }


    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResultMsgVO login(String userName, String password, String dev,String appType,String appVersion) {
        if( StringUtils.isAnyBlank(userName, password, dev, appType, appVersion)) {
            return new ErrorMissingParamsMsgVO();
        }

        String token ;
        try {
            MobileUserAuthVO authVO = mUserLoginService.login(userName, password, dev, appType, appVersion);
            token = authTokenService.generateToken(authVO);
        } catch (CommonException e) {
            log.error("", e);
            return new DefeatMsgVO("Incorrect username or password.");
        }

        Map map = new HashMap<String, Object>();
        map.put("token", token);
        return new SuccessMsgWithDataVO(map);
    }


    @RequestMapping(value = "/forget_pwd", method = RequestMethod.POST)
    public ResultMsgVO forgetPassword(String userName,String newPassword,String verifyCode) {
        if (StringUtils.isAnyBlank(userName, newPassword, verifyCode)) {
            return new ErrorMissingParamsMsgVO();
        }

        return new SuccessMsgVO("Update Manufacturer OK");
    }



    public static boolean isEmail(String email) {
        String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return Pattern.matches(REGEX_EMAIL, email);
    }

}
