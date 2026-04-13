package com.abswitch.weblog.admin.service;

import com.abswitch.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.abswitch.weblog.common.utils.Response;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-21 15:33
 * @Description：
 */
public interface AdminUserService {
    /**
     * 修改密码
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO);

    /**
     * 获取当前用户信息
     * @return
     */
    Response findUserInfo();
}
