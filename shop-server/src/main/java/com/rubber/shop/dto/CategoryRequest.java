package com.rubber.shop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    private String name_zj;

    private Long parent_id_zj;
    private Integer sort_zj;
    private String icon_zj;
}
