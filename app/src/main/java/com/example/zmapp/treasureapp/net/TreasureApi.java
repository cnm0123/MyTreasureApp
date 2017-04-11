package com.example.zmapp.treasureapp.net;

import com.example.zmapp.treasureapp.treasure.Area;
import com.example.zmapp.treasureapp.treasure.Treasure;
import com.example.zmapp.treasureapp.treasure.detail.TreasureDetail;
import com.example.zmapp.treasureapp.treasure.detail.TreasureDetailResult;
import com.example.zmapp.treasureapp.treasure.hide.HideTreasure;
import com.example.zmapp.treasureapp.treasure.hide.HideTreasureResult;
import com.example.zmapp.treasureapp.user.User;
import com.example.zmapp.treasureapp.user.account.Update;
import com.example.zmapp.treasureapp.user.account.UpdateResult;
import com.example.zmapp.treasureapp.user.account.UploadResult;
import com.example.zmapp.treasureapp.user.login.LoginResult;
import com.example.zmapp.treasureapp.user.register.RegisterResult;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by gqq on 2017/3/30.
 */
// 对应服务器的接口
public interface TreasureApi {

    // 登录的请求
    @POST("/Handler/UserHandler.ashx?action=login")
    Call<LoginResult> login(@Body User user);

    // 注册的请求
    @POST("/Handler/UserHandler.ashx?action=register")
    Call<RegisterResult> register(@Body User user);

    // 获取区域内的宝藏数据
    @POST("/Handler/TreasureHandler.ashx?action=show")
    Call<List<Treasure>> getTreasureInArea(@Body Area area);

    // 宝藏详情的数据获取
    @POST("/Handler/TreasureHandler.ashx?action=tdetails")
    Call<List<TreasureDetailResult>> getTreasureDetail(@Body TreasureDetail treasureDetail);

    // 埋藏宝藏的请求
    @POST("/Handler/TreasureHandler.ashx?action=hide")
    Call<HideTreasureResult> hideTreasure(@Body HideTreasure hideTreasure);

    // 头像的上传
    @Multipart
    @POST("/Handler/UserLoadPicHandler1.ashx")
    Call<UploadResult> upload(@Part MultipartBody.Part part);

    // 用户头像的更新
    @POST("/Handler/UserHandler.ashx?action=update")
    Call<UpdateResult> update(@Body Update update);
}
