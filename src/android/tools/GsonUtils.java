package com.bjzjns.hxplugin.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Created by blade on 2/18/16.
 */
public class GsonUtils {
    private static Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    public  synchronized  static <T> T fromJson(String json,Class<T> clazz) {
        return gson.fromJson(json,clazz);
    }

    public  synchronized  static <T> T fromJson(String json,Type type) {
        return   gson.fromJson(json,type);
    }

    public synchronized static String toJson(Object object) {
        return gson.toJson(object);
    }

}
