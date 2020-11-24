package com.zlfcapp.poster.http;


import com.zlfcapp.poster.bean.Result;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

/**
 * @Description:接口
 * @Author: lixh
 * @CreateDate: 2020/5/19 19:52
 * @Version: 1.0
 */
public interface APIService {

    /**
     * 提交用户反馈
     *
     * @return
     */
    @FormUrlEncoded
    @POST("http://139.9.159.20/api/home/appapi/feedback")
    Observable<Result<String>> subFaq(@Field("content") String content, @QueryMap Map<String, String> dynamic);

    /**
     * 获取回复和反馈
     *
     * @return
     */
    @GET("http://139.9.159.20/api/home/appapi/feedbackList")
    Observable<Result<String>> subGetFeedBack(@QueryMap Map<String, String> dynamic);

    /**
     * 上传一条笔记
     *
     * @return
     */
    @Multipart
    @POST("addDatas")
    Observable<Result<String>> addData(@QueryMap Map<String, String> dynamic, @Part() MultipartBody.Part... parts);

    /**
     * @Author lixh
     * @Date 2020/10/15 19:32
     * @Description: 查询字体列表
     */
    @POST("api/admin/font/fontList")
    Observable<Result<String>> queryFontList(@QueryMap Map<String, Object> dynamic);

    /**
     * @Author lixh
     * @Date 2020/10/15 19:32
     * @Description: 查询字体下载地址
     */
    @POST("api/admin/font/fontDown")
    Observable<Result<String>> queryFontId(@QueryMap Map<String, Object> dynamic);

    /**
     * @Author lixh
     * @Date 2020/10/17 15:01
     * @Description: 查询图片素材
     */
    @POST("api/admin/font/imgList")
    Observable<Result<String>> queryImgList(@QueryMap Map<String, Object> dynamic);

    /**
     * @Author lixh
     * @Date 2020/10/26 11:51
     * @Description: 添加模板数据
     */
    @POST("api/admin/font/insertLogoTemp")
    Observable<Result<String>> AddTempData(@QueryMap Map<String, Object> dynamic);

    /**
     * @Author lixh
     * @Date 2020/11/4 15:01
     * @Description: 查询所有模板数据
     */
    @POST("api/admin/font/logoTemplateList")
    Observable<Result<String>> queryAllTempData(@QueryMap Map<String, Object> dynamic);

    //查询缩略图
    @POST("api/admin/posters/imgList")
    Observable<Result<String>> queryImageList(@QueryMap Map<String, Object> dynamic);
    //上传编辑之后的图
    @Multipart
    @POST("api/admin/posters/uploadLower")
    Observable<Result<String>> uploadLower(@QueryMap Map<String, Object> dynamic,@Part() MultipartBody.Part... parts);

    /**
     * 上传文字等信息
     *
     * @return
     */
    @POST("api/admin/posters/uploadLowerInfo")
    Observable<Result<String>> uploadLowerInfo(@Body RequestBody info);

    //缩略图列表
    @POST("api/admin/posters/lowerList")
    Observable<Result<String>> lowerList(@QueryMap Map<String, Object> dynamic);
}
