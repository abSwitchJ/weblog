package com.abswitch.weblog.admin.controller;

import com.abswitch.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.abswitch.weblog.admin.service.AdminUserService;
import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-21 15:46
 * @Description：
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 用户模块")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @PostMapping("/password/update")
    @ApiOperationLog(description = "修改用户密码")
    @Operation(summary = "修改用户密码")
    public Response updatePassword(@RequestBody @Validated UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO){

        return adminUserService.updatePassword(updateAdminUserPasswordReqVO);
    }

    @PostMapping("/user/info")
    @ApiOperationLog(description = "获取用户信息")
    @Operation(summary = "获取用户信息")
    public Response findUserInfo(){

        return adminUserService.findUserInfo();
    }
}
