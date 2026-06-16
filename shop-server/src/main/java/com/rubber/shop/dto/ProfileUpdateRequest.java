package com.rubber.shop.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {

    private String realName;
    private String avatar;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
}
