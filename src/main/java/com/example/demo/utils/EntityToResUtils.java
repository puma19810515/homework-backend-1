package com.example.demo.utils;

import org.springframework.beans.BeanUtils;

public class EntityToResUtils {

    /**
     * Convert entity to response object
     * @param entity
     * @param resClass
     * @return
     * @param <T>
     * @param <R>
     */
    public static <T, R> R entityToRes(T entity, Class<R> resClass) {
        if (entity == null) return null;
        try {
            R res = resClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(entity, res); // 自動匹配相同欄位
            return res;
        } catch (Exception e) {
            throw new RuntimeException("Error converting entity to response", e);
        }
    }
}
