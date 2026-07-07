package com.rubber.shop.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryTreeResponse {

    private Long id_zj;
    private String name_zj;
    private Long parent_id_zj;
    private Integer sort_zj;
    private String icon_zj;
    private List<CategoryTreeResponse> children = new ArrayList<>();
}
