package io.driden.fishtips.provider;


import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.driden.fishtips.exception.NoMethodFoundException;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpProvider {

    /**
     * Client builder
     */
    public static class ClientBuilder {

        private OkHttpClient.Builder builder;

        public ClientBuilder() {
            builder = new OkHttpClient().newBuilder();
        }

        public ClientBuilder setDefaultTimeOuts() {

            builder
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS);

            return this;
        }

        public ClientBuilder setCache(Cache cache){
            builder.cache(cache);
            return this;
        }

        public ClientBuilder addInterceptor(Interceptor interceptor) {
            builder.addInterceptor(interceptor);
            return this;
        }

        public OkHttpClient build() {
            return builder.build();
        }
    }

    /**
     * Request builder
     */
    public static class RequestBuilder {

        final int GET = 1000;
        final int POST = 2000;

        String url;

        Request.Builder builder;

        public RequestBuilder(String url) {
            builder = new Request.Builder();
            this.url = url;
            builder.url(url);
        }

        public void addHeaders(List<Map<String, String>> headers) {
            for (Map<String, String> map : headers) {
                builder.header(map.get("KEY"), map.get("VALUE"));
            }
        }

        public void setMethod(int method, List<Map<String, String>> params) {
            switch (method) {
                case GET:
                    if (params != null) {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < params.size(); i++) {
                            Map<String, String> param = params.get(i);
                            if (i == 0) {
                                sb.append("?");
                            }
                            sb.append(param.get("KEY")).append("=").append(param.get("VALUE"));
                            if (i > 0 && i < params.size() - 1) {
                                sb.append("&");
                            }
                        }

                        this.url = this.url + sb.toString();
                    }
                    builder.url(url);
                    builder.get();

                    break;

                case POST:

                    MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();

                    if (params != null) {

                        bodyBuilder.setType(MultipartBody.FORM);

                        for (Map<String, String> param : params) {
                            bodyBuilder.addFormDataPart(param.get("KEY"), param.get("VALUE"));
                        }

                    }

                    builder.post(bodyBuilder.build());

                    break;

                default:
                    throw new NoMethodFoundException(String.format("The Method type must be GET(%1$d) or POST(%2$d)", GET, POST));
            }
        }

        public Request build() {
            return builder.build();
        }
    }
}
