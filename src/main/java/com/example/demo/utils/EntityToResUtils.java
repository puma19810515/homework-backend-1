package com.example.demo.utils;

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
        if (entity == null) {
            return null;
        }
        try {
            R res = resClass.getDeclaredConstructor().newInstance();
            // Simple property copying logic (you can use libraries like BeanUtils or ModelMapper for complex cases)
            for (var field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                var resField = resClass.getDeclaredField(field.getName());
                resField.setAccessible(true);
                resField.set(res, field.get(entity));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException("Error converting entity to response", e);
        }
    }
}
