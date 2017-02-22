package com.pc.magso;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by admin_2 on 2017/2/9.
 */

public interface NetClient {

    @Headers({"User-Agent:Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36",
            "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"})
    @GET("{name}/{order}")
    Call<String> getRespos(@Path("name") String name,@Path("order") String order);
}
