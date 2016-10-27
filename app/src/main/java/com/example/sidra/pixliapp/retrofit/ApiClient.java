package com.example.sidra.pixliapp.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sidra on 21-10-2016.
 */

public class ApiClient {

    public static final String BASR_URL = "http://192.168.7.50:5000/";
    //http://10.0.2.2:8000/
    //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    public static Retrofit retrofit = null;
    //retrofit.addInterceptor(logging);
    public static Retrofit getClient(){
        if (retrofit== null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASR_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }


        return retrofit;
    }
}
