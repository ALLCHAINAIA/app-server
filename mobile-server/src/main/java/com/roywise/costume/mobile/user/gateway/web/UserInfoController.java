package com.roywise.costume.mobile.user.gateway.web;

import com.roywise.costume.business.user.api.constants.FileSubDirContants;
import com.roywise.costume.business.user.api.model.MUserInfo;
import com.roywise.costume.business.user.api.service.MUserInfoService;
import com.roywise.costume.business.util.CostumeUtils;
import com.roywise.costume.mobile.user.api.vo.UserInfoVO;
import com.roywise.mobile.module.comm.api.vo.*;
import com.roywise.mobile.module.comm.provider.service.LocalUploadFileService;
import com.roywise.mobile.module.comm.util.RequestTokenUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;
import java.util.Date;

@Log4j2
@RestController
@RequestMapping(value = "/v1/user/info")
@Validated
public class UserInfoController {

    @Resource
    private MUserInfoService mUserInfoService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private LocalUploadFileService localUploadFileService;



    @RequestMapping(value = "/mod_info", method = RequestMethod.POST)
    public ResultMsgVO modInfo(String avatarStr, @Size(max = 30, min = 2, message = "The user name is 2-30 characters in length") String nickName,String fileName) {
        if (StringUtils.isBlank(avatarStr) && StringUtils.isBlank(nickName)) {
            return new SuccessMsgVO();
        }
        Long userId = RequestTokenUtils.getCurrUserId(request);
        MUserInfo tmp = mUserInfoService.selectByPrimaryKey(userId);
        if (tmp == null) {
            return new DefeatMsgVO("User not exist!");
        }
        if (StringUtils.isNotBlank(nickName)) {
            tmp.setNickName(nickName);
        }

        String avatarSrc = null;

        if (!StringUtils.isBlank(avatarStr) && !StringUtils.isBlank(fileName)) {
            try {
                byte[] bytes = localUploadFileService.decodeBase64File(avatarStr);
                if (bytes.length > 1024 * 1024 * 1) { //
                    return new DefeatMsgVO("The head is too big!");
                }
                avatarSrc = localUploadFileService.uploadFile(bytes, FileSubDirContants.SUB_AVATAR_DIR,
                        fileName, true);
                localUploadFileService.remove(tmp.getAvatar());
                tmp.setAvatar(avatarSrc);
            } catch (Exception e) {
                log.error("",e);
                return new DefeatMsgVO("Failure to modify avatar!");
            }
        }

        MUserInfo userInfo = MUserInfo.builder().nickName(nickName).avatar(avatarSrc).updateTime(new Date()).build();
        userInfo.setId(userId);
        mUserInfoService.updateByPrimaryKeySelective(userInfo);

        UserInfoVO vo = UserInfoVO.builder().userId(userId).nickName(tmp.getNickName())
                .avatarUrl(CostumeUtils.getUrl(tmp.getAvatar()))
                .uniqueId(tmp.getUniqueId())
                .userName(tmp.getUserName())
                .build();

        return new SuccessMsgWithDataVO(vo);
    }


    @RequestMapping(value = "/get_userinfo", method = RequestMethod.GET)
    public ResultMsgVO getUserinfo() {
        Long  userId = RequestTokenUtils.getCurrUserId(request);
        MUserInfo dbUser = mUserInfoService.selectByPrimaryKey(userId);

        if (dbUser == null) {
            return new DefeatMsgVO("user does not exist");
        }

        UserInfoVO vo = UserInfoVO.builder().userId(userId).nickName(dbUser.getNickName())
                .avatarUrl(CostumeUtils.getUrl(dbUser.getAvatar()))
                .uniqueId(dbUser.getUniqueId())
                .userName(dbUser.getUserName())
                .build();

        return new SuccessMsgWithDataVO(vo);

    }

}
