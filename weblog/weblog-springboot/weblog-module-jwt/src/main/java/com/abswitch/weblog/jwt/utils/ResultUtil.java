package com.abswitch.weblog.jwt.utils;

import com.abswitch.weblog.common.utils.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-18 15:27
 * @Description：
 */
public class ResultUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 成功响参
     * @param response
     * @param result
     * @throws IOException
     */
    public static void ok(HttpServletResponse response, Response<?> result)throws IOException{

        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");

        PrintWriter writer = response.getWriter();
        writer.write(OBJECT_MAPPER.writeValueAsString(result));
        writer.flush();
    }

    /**
     * 失败响参
     * @param response
     * @param result
     * @throws IOException
     */
    public static void fail(HttpServletResponse response, Response<?> result) throws IOException{

        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");

        PrintWriter writer = response.getWriter();

        writer.write(OBJECT_MAPPER.writeValueAsString(result));

        writer.flush();
    }

    /**
     * 失败响参
     * @param response
     * @param status 可指定响应码，如401等
     * @param result
     * @throws IOException
     */
    public static void fail(HttpServletResponse response, int status, Response<?> result) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.setContentType("application/json");

        PrintWriter writer = response.getWriter();

        writer.write(OBJECT_MAPPER.writeValueAsString(result));

        writer.flush();
    }




}
