package com.roywise.costume.mobile.user.api.vo;

import lombok.*;

/**
 * @description:

 * @date: 2018/3/23 11:26
 */
@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoVO {
    private Long userId;
    private String userName;
    private String nickName;
    private String avatarUrl;
    private String uniqueId;
}
