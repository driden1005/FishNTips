package io.driden.fishtips.dependancy.module;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by driden on 3/02/2017.
 */

public interface TestAPI {
    @FormUrlEncoded
    @POST("/cgi-bin/worlddata.cgi")
    Call<ResponseBody> getData(@Field("latdeg") double latdeg,
                               @Field("lngdeg") double lngdeg,
                               @Field("numdays")int numdays,
                               @Field("tidestation")String tidestation,
                               @Field("tzone")String tzone,
                               @Field("date_time")String date_time
                                );

    @GET("/phpsqlsearch_genxml.php")
    Call<ResponseBody> getTideStation(@Query("lat") double lat,
                                @Query("lng") double lng,
                                @Query("radius")long radius
    );
}
