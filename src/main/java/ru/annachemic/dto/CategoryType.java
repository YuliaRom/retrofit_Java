package ru.annachemic.dto;

import lombok.Getter;

public enum CategoryType {
    FOOD("Food", 1),
    DISH ("Dish", 2);

    @Getter
    public final String title;
    @Getter
    public final Integer id;

    CategoryType(String title, Integer id) {
        this.title = title;
        this.id = id;
    }
}
