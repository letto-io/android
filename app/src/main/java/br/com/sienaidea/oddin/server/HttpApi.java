package br.com.sienaidea.oddin.server;

import android.content.Context;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import br.com.sienaidea.oddin.model.Presentation;
import br.com.sienaidea.oddin.model.User;
import br.com.sienaidea.oddin.util.CookieUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class HttpApi {

    public static final String API_URL = "http://ws-edupanel.herokuapp.com/";

    /**
     * Generic HttpBin.org Response Container
     */
    static class HttpBinResponse {
        // the request url
        String url;

        // the requester ip
        String origin;

        // all headers that have been sent
        Map headers;

        // url arguments
        Map args;

        // post form parameters
        Map form;

        // post body json
        Map json;
    }

    /**
     * HttpBin.org service definition
     */
    public interface HttpBinService {
        @GET("/get")
        Call<HttpBinResponse> get();

        // request /get?testArg=...
        @GET("/get")
        Call<HttpBinResponse> getWithArg(
                @Query("testArg") String arg
        );

        // POST form encoded with form field params
        @FormUrlEncoded
        @POST("/post")
        Call<HttpBinResponse> postWithFormParams(
                @Field("field1") String field1
        );

        /* POST form encoded with form field params
        @POST("/post")
        Call<HttpBinResponse> postWithJson(
                @Body LoginData loginData
        );*/

        /* POST form encoded with form field params FUNCIONOU OK
        @POST("/controller/login")
        Call<Void> postWithJsonLogin(@Body User user);*/

         /*FUNCIONOU COM VOID
        @POST("controller/instruction/{instruction_id}/presentation")
        Call<Void> postWithJsonPresentation(@Header("Cookie") String cookie,
                                            @Path("instruction_id") String instruction_id,
                                            @Body PresentationRetrofit presentation);
        */


        //Login
        @POST("/controller/login")
        Call<Void> postLogin(@Body User user);

        //new Presentation OK
        @POST("controller/instruction/{instruction_id}/presentation")
        Call<Presentation> postPresentation(@Header("Cookie") String cookie,
                                            @Path("instruction_id") String instruction_id,
                                            @Body Presentation presentation);

        //change Status Doubt TESTAR
        @POST("controller/instruction/{instruction_id}/presentation/{presentation_id}/doubt/{doubt_id}/change-status")
        Call<Void> changeStatusDoubt(@Header("Cookie") String cookie,
                                     @Path("instruction_id") String instruction_id,
                                     @Path("presentation_id") String presentation_id,
                                     @Path("doubt_id") String doubt_id,
                                     @Body String json);

        /*new Doubt
        @POST("controller/instruction/{instruction_id}/presentation/{presentation_id}/doubt")
        Call<Doubt> postDoubt(@Header("Cookie") String cookie,
                              @Path("instruction_id") String instruction_id,
                              @Path("presentation_id") String presentation_id,
                              @Body Doubt doubt);
        */

        //new Material Discipline OK
        @Multipart
        @POST("controller/instruction/{instruction_id}/material")
        Call<Void> postMaterial(@Header("Cookie") String cookie,
                                @Path("instruction_id") String instruction_id,
                                @Part MultipartBody.Part file);

        //new Material Discipline TESTE
        @Multipart
        @POST("controller/instruction/{instruction_id}/material")
        Call<Void> postMaterialDisciplineTeste(@Header("Cookie") String cookie,
                                               @Path("instruction_id") String instruction_id,
                                               @Part MultipartBody.Part file);

        //getMaterial Presentation TESTAR
        @GET("controller/instruction/{instruction_id}/presentation/{presentation_id}/material")
        Call<JSONObject> getMaterialPresentation(@Header("Cookie") String cookie,
                                                 @Path("instruction_id") String instruction_id,
                                                 @Path("presentation_id") String presentation_id);

        //new Material Presentation OK
        @Multipart
        @POST("controller/instruction/{instruction_id}/presentation/{presentation_id}/material")
        Call<Void> postMaterialPresentation(@Header("Cookie") String cookie,
                                            @Path("instruction_id") String instruction_id,
                                            @Path("presentation_id") String presentation_id,
                                            @Part MultipartBody.Part file);

        //new Contribution text testar (aparentemente funcionou, necessário corrigir bug do retorno das contributions)
        @Multipart
        @POST("controller/instruction/{instruction_id}/presentation/{presentation_id}/doubt/{doubt_id}/contribution")
        Call<Void> postTextContributionMultiPart(@Header("Cookie") String cookie,
                                                 @Path("instruction_id") String instruction_id,
                                                 @Path("presentation_id") String presentation_id,
                                                 @Path("doubt_id") String doubt_id,
                                                 @Part("text") RequestBody text);

        //não funcionou no teste
        @FormUrlEncoded
        @POST("controller/instruction/{instruction_id}/presentation/{presentation_id}/doubt/{doubt_id}/contribution")
        Call<Void> postTextContributionFormUrlEncoded(@Header("Cookie") String cookie,
                                                      @Path("instruction_id") String instruction_id,
                                                      @Path("presentation_id") String presentation_id,
                                                      @Path("doubt_id") String doubt_id,
                                                      @Field("text") String text);

        //new Contribution File OK
        @Multipart
        @POST("controller/instruction/{instruction_id}/presentation/{presentation_id}/doubt/{doubt_id}/contribution")
        Call<Void> postFileContribution(@Header("Cookie") String cookie,
                                        @Path("instruction_id") String instruction_id,
                                        @Path("presentation_id") String presentation_id,
                                        @Path("doubt_id") String doubt_id,
                                        @Part MultipartBody.Part file);


        //get Contribution File testar
        //GET /instruction/$instruction_id/presentation/$presentation_id/doubt/$doubt_id/contribution/$contribution_id/materials/$material_id
        @GET("controller/instruction/{instruction_id}/presentation/{presentation_id}/doubt/{doubt_id}/contribution/{contribution_id}/materials/{material_id}")
        Call<byte[]> getFileContribution(@Header("Cookie") String cookie,
                                         @Path("instruction_id") String instruction_id,
                                         @Path("presentation_id") String presentation_id,
                                         @Path("doubt_id") String doubt_id,
                                         @Path("contribution_id") String contribution_id,
                                         @Path("material_id") String material_id);

    }

    public static void testApiRequest(String instruction_id, File file, Context context) {
        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Service setup
        HttpBinService service = retrofit.create(HttpBinService.class);

        // Prepare the HTTP request
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<Void> call = service.postMaterial(CookieUtil.getCookie(context), instruction_id, body);

        // Asynchronously execute HTTP request
        call.enqueue(new Callback<Void>() {
            /**
             * onResponse is called when any kind of response has been received.
             */
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // http response status code + headers
                System.out.println("Response status code: " + response.code());

                // isSuccess is true if response code => 200 and <= 300
                if (!response.isSuccessful()) {
                    // print response body if unsuccessful
                    try {
                        System.out.println(response.errorBody().string());
                    } catch (IOException e) {
                        // do nothing
                    }
                    return;
                }

            }

            /**
             * onFailure gets called when the HTTP request didn't get through.
             * For instance if the URL is invalid / host not reachable
             */
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("onFailure");
                System.out.println(t.getMessage());
            }
        });
    }
}
