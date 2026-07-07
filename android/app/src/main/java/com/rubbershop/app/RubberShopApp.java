package com.rubbershop.app;

import android.app.Application;
import com.rubbershop.app.data.local.TokenManager;

public class RubberShopApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TokenManager.init(this);
    }
}
