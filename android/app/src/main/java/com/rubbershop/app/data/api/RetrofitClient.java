package com.rubbershop.app.data.api;

import com.rubbershop.app.data.local.TokenManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static final String BASE_URL = "http://172.20.10.2:8080/api/";
    private static ApiService apiService;

    public static ApiService getApi() {
        if (apiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

            Interceptor authInterceptor = chain -> {
                okhttp3.Request original = chain.request();
                String token = TokenManager.getToken();
                okhttp3.Request request = original;
                if (token != null && !token.isEmpty()) {
                    request = original.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();
                }
                return chain.proceed(request);
            };

            Interceptor errorInterceptor = chain -> {
                okhttp3.Response response = chain.proceed(chain.request());
                if (response.code() == 401 || response.code() == 403) {
                    TokenManager.clear();
                }
                return response;
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging)
                    .addInterceptor(errorInterceptor)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
