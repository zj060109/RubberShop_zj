package com.rubber.shop.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryTreeResponse {

    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private String icon;
    private List<CategoryTreeResponse> children = new ArrayList<>();
}
