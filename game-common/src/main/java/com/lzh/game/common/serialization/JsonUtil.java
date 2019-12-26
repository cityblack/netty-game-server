package com.lzh.game.common.serialization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static String toJSON(Object o) {
        return JSON.toJSONString(o);
    }

    public static <T>List<T> jsonToList(String json, Class<T> clazz) {

        return JSONArray.parseArray(json,clazz);
    }

    public static Map<String,Object> jsonToMap(String json) {
        return JSON.parseObject(json);
    }

    public static <T>T jsonParse(String json, Class<T> clazz) {
        return JSON.parseObject(json,clazz);
    }
}
