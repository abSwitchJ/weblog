package com.abswitch.weblog.admin.service;

import com.abswitch.weblog.common.utils.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 18:40
 * @Description：
 */
public interface AdminFileService {

    Response uploadFile(MultipartFile file);
}
