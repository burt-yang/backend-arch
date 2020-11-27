package com.practice.arch.commonarch.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.arch.commonarch.config.JacksonConfig;

/**
 * Created by byang059 on 1/9/18.
 */
public class JsonUtil {

    private JsonUtil() {
    }

    private static ObjectMapper mapper = JacksonConfig.getObjectMapper();

    public static String bean2Json(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T json2Bean(String json, Class<T> objClass) {
        try {
            return mapper.readValue(json, objClass);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
