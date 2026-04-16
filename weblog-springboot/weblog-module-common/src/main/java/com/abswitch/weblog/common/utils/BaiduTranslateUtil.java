package com.abswitch.weblog.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
     * 将中文文本翻译为英文
     *
     * @param query     待翻译文本
     * @param appId     百度翻译 appId
     * @param secretKey 百度翻译 secretKey
     * @return 翻译后的英文文本，翻译失败时返回 null
     */
    public static String translateToEnglish(String query, String appId, String secretKey) {
        try {
            String salt = String.valueOf(System.currentTimeMillis());
            String sign = md5(appId + query + salt + secretKey);

            String urlStr = API_URL
                    + "?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                    + "&from=zh"
                    + "&to=en"
                    + "&appid=" + appId
                    + "&salt=" + salt
                    + "&sign=" + sign;

            HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonNode root = OBJECT_MAPPER.readTree(response.toString());
            JsonNode transResult = root.get("trans_result");
            if (transResult != null && transResult.isArray() && !transResult.isEmpty()) {
                return transResult.get(0).get("dst").asText();
            }

            log.warn("百度翻译返回异常: {}", response);
            return null;
        } catch (Exception e) {
            log.error("百度翻译调用失败: {}", e.getMessage(), e);
            return null;
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
