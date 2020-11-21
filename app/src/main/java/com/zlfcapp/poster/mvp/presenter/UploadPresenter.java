package com.zlfcapp.poster.mvp.presenter;


import com.zlfcapp.poster.App;
import com.zlfcapp.poster.http.listener.HttpClientListener;
import com.zlfcapp.poster.mvp.base.BasePresenter;
import com.zlfcapp.poster.mvp.view.IHomeView;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @Description: 文件上传演示
 * @Author: lixh
 * @CreateDate: 2020/5/19 20:01
 * @Version: 1.0
 */
public class UploadPresenter extends BasePresenter<IHomeView> {
    public UploadPresenter(App app) {
        super(app);
    }

    /**
     * @Author lixh
     * @Date 2020/5/19 20:03
     * @Description: 包含文件上传
     * List<String> 文件路径集合
     */
    public void SubAddData(List<String> imgPath, Map<String, String> map) {
        MultipartBody.Part[] filePart = prepareFilesPart("imgUrl[]", imgPath);
        if (filePart.length == 0) {
            filePart = new MultipartBody.Part[1];
            MultipartBody.Part part = MultipartBody.Part.createFormData("", "");
            filePart[0] = part;
        }
        httpClient(getService().addData(map, filePart), new HttpClientListener<String>() {
            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(int code, String message) {
                getView().onError(new Throwable());
            }
        });
    }

    /**
     * 一个key 对应多个文件
     *
     * @param partName
     * @param filePathList
     * @return
     */
    private MultipartBody.Part[] prepareFilesPart(String partName, List<String> filePathList) {
        MultipartBody.Part[] parts = new MultipartBody.Part[filePathList.size()];
        for (int i = 0; i < filePathList.size(); i++) {
            String filePath = filePathList.get(i);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
            parts[i] = part;
        }
        return parts;
    }
}
