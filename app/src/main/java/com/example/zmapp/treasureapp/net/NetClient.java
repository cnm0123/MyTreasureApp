package com.example.zmapp.treasureapp.net;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gqq on 2017/3/29.
 */

// 网络的客户端类
public class    NetClient {

    public static final String BASE_URL = "http://admin.syfeicuiedu.com";
    private static NetClient mNetClient;
    private final OkHttpClient mOkHttpClient;
    private final Gson mGson;
    private final Retrofit mRetrofit;
    private TreasureApi mTreasureApi;

    private NetClient() {

        mGson = new Gson();

        // 日志拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        // 需要设置打印级别
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttpClient的单例化
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        // Retrofit的创建
        // 必须要加的BASEURL
        // 添加OkHttpClient
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)// 必须要加的BASEURL
                .client(mOkHttpClient)// 添加OkHttpClient
                // 添加转换器
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized NetClient getInstance(){
        if (mNetClient==null){
            mNetClient = new NetClient();
        }
        return mNetClient;
    }

    // 将TreasureApi怎么对外提供处理：提供一个方法getTreasureApi()
    public TreasureApi getTreasureApi(){

        if (mTreasureApi==null){
            // 对请求接口的具体实现
            mTreasureApi = mRetrofit.create(TreasureApi.class);
        }
        return mTreasureApi;
    }
}
