package com.abswitch.weblog.admin.service.impl;

import com.abswitch.weblog.admin.model.vo.user.FindUserInfoRspVO;
import com.abswitch.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.abswitch.weblog.admin.service.AdminUserService;
import com.abswitch.weblog.common.domain.mapper.UserMapper;
import com.abswitch.weblog.common.emuns.ResponseCodeEnum;
import com.abswitch.weblog.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-21 15:32
 * @Description：
 */
@Service
public class AdminUserImpl implements AdminUserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {

        String username = updateAdminUserPasswordReqVO.getUsername();
        String password = updateAdminUserPasswordReqVO.getPassword();

        String encodePassword = passwordEncoder.encode(password);

        int count = userMapper.updatePasswordByUsername(username, encodePassword);

        return count == 1 ? Response.ok() : Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
    }

    @Override
    public Response findUserInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return Response.ok(FindUserInfoRspVO.builder().username(username).build());
    }
}
