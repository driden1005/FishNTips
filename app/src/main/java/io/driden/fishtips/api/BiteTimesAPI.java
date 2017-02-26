package io.driden.fishtips.api;

import io.driden.fishtips.model.MarkersTag;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BiteTimesAPI {
    @FormUrlEncoded
    @POST("/cgi-bin/worlddata.cgi")
    Call<ResponseBody> getData(@Field("latdeg") double latdeg,
                               @Field("lngdeg") double lngdeg,
                               @Field("numdays") int numdays,
                               @Field("tidestation") String tidestation,
                               @Field("tzone") String tzone,
                               @Field("date_time") String date_time
    );

    @GET("/phpsqlsearch_genxml.php")
    Call<MarkersTag> getTideStation(@Query("lat") double lat,
                                    @Query("lng") double lng,
                                    @Query("radius") long radius
    );
}
