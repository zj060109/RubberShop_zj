package com.rubber.shop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CustomizationCreateRequest {

    private String description;
    private List<String> referenceImages;
}
