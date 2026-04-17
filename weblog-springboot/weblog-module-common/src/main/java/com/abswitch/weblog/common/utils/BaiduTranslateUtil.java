package com.abswitch.weblog.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
public class BaiduTranslateUtil {

    private static final String API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将中文文本翻译为英文（向后兼容方法，内部转调 translate）
     */
    public static String translateToEnglish(String query, String appId, String secretKey) {
        try {
            return translate(query, "zh", "en", appId, secretKey);
        } catch (Exception e) {
            log.error("百度翻译（zh→en）调用失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 通用翻译接口，使用 POST 调用百度翻译 API。
     * <p>
     * 多行文本会按 trans_result 数组按行返回，本方法用 \n 拼接所有 dst。
     *
     * @param query     待翻译文本（支持多行 \n 分隔）
     * @param from      源语言（zh/en/auto 等）
     * @param to        目标语言
     * @param appId     百度翻译 appId
     * @param secretKey 百度翻译 secretKey
     * @return 翻译后的文本
     * @throws RuntimeException 当 API 返回 error_code 或网络/解析异常
     */
    public static String translate(String query, String from, String to, String appId, String secretKey) {
        if (query == null || query.isEmpty()) {
            return query;
        }
        try {
            String salt = String.valueOf(System.currentTimeMillis());
            String sign = md5(appId + query + salt + secretKey);

            String body = "q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                    + "&from=" + from
                    + "&to=" + to
                    + "&appid=" + appId
                    + "&salt=" + salt
                    + "&sign=" + sign;

            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonNode root = OBJECT_MAPPER.readTree(response.toString());

            JsonNode errorCode = root.get("error_code");
            if (errorCode != null) {
                String errorMsg = root.has("error_msg") ? root.get("error_msg").asText() : "unknown";
                String clientIp = root.has("client_ip") ? root.get("client_ip").asText() : "N/A";
                String appIdTail = appId == null ? "null"
                        : appId.substring(Math.max(0, appId.length() - 4));
                throw new RuntimeException("百度翻译 API 错误 " + errorCode.asText() + ": " + errorMsg
                        + "，client_ip=" + clientIp + "，appIdTail=" + appIdTail
                        + "，resp=" + response);
            }

            JsonNode transResult = root.get("trans_result");
            if (transResult != null && transResult.isArray() && !transResult.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < transResult.size(); i++) {
                    if (i > 0) sb.append("\n");
                    sb.append(transResult.get(i).get("dst").asText());
                }
                return sb.toString();
            }

            throw new RuntimeException("百度翻译返回异常: " + response);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("百度翻译调用失败: " + e.getMessage(), e);
        }
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 计算失败", e);
        }
    }
}
