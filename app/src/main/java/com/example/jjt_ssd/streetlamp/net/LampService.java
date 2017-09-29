package com.example.jjt_ssd.streetlamp.net;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.POST;

/**
 * Created by LJN on 2017/9/29.
 */

public interface LampService {
    @POST("")
    Call<String> getStatus(@Field("page") String page);
}
