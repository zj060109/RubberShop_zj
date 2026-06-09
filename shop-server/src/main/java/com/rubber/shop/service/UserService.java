package com.rubber.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rubber.shop.entity.User;

public interface UserService extends IService<User> {

    User getByPhone(String phone);
}
