package org.example.client;

import okhttp3.OkHttpClient;
import org.example.client.Interceptor.BaseIn;
import org.example.config.ConfigManager;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ClientManager {

    public static  <T> T createClient(Class<T> service, String apiKey) {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .callTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new BaseIn(apiKey))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConfigManager.getBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(service);
    }
}
