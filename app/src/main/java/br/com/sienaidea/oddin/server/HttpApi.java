package br.com.sienaidea.oddin.server;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Material;
import br.com.sienaidea.oddin.retrofitModel.Person;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.retrofitModel.Profile;
import br.com.sienaidea.oddin.retrofitModel.Question;
import br.com.sienaidea.oddin.retrofitModel.ResponseVote;
import br.com.sienaidea.oddin.retrofitModel.Session;
import br.com.sienaidea.oddin.retrofitModel.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class HttpApi {

    Context mContext;
    private List<Person> persons = new ArrayList<>();

    public static final String API_URL = "http://rws-edupanel.herokuapp.com/";

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
        //NEW BACK
        //Session OK
        @POST("session")
        Call<Session> Login(@Body User user);

        //Profile OK
        @GET("instructions/{instruction_id}/profile")
        Call<Profile> Profile(@Header("x-session-token") String token,
                              @Path("instruction_id") int instruction_id);

        //Instructions OK
        @GET("instructions")
        Call<List<Instruction>> Instructions(@Header("x-session-token") String token);

        //Instruction Materials OK
        @GET("instructions/{instruction_id}/materials")
        Call<List<Material>> InstructionMaterials(@Header("x-session-token") String token,
                                                  @Path("instruction_id") int instruction_id);

        //new Instruction Material (Bruno disse que ainda não esta funcionando)
        @Multipart
        @POST("instructions/{instruction_id}/materials/new")
        Call<Void> createInstructionMaterial(@Header("x-session-token") String token,
                                             @Path("instruction_id") int instruction_id,
                                             @Part MultipartBody.Part file);

        //Presentations OK
        @GET("instructions/{instruction_id}/presentations")
        Call<List<Presentation>> Presentations(@Header("x-session-token") String token,
                                               @Path("instruction_id") int instruction_id);

        //New Presentation OK
        @POST("instructions/{instruction_id}/presentations")
        Call<Presentation> NewPresentation(@Header("x-session-token") String token,
                                           @Path("instruction_id") String instruction_id,
                                           @Body Presentation presentation);

        //Close Presentation OK
        @POST("presentations/{presentation_id}/close")
        Call<Presentation> ClosePresentation(@Header("x-session-token") String token,
                                             @Path("presentation_id") int presentation_id);

        //Participants OK
        @GET("instructions/{instruction_id}/participants")
        Call<List<Person>> Participants(@Header("x-session-token") String token,
                                        @Path("instruction_id") int instruction_id);

        //Questions OK
        @GET("presentations/{presentation_id}/questions")
        Call<List<Question>> Questions(@Header("x-session-token") String token,
                                       @Path("presentation_id") int presentation_id);

        //New Question OK
        @POST("presentations/{presentation_id}/questions")
        Call<Question> NewQuestion(@Header("x-session-token") String token,
                                   @Path("presentation_id") int presentation_id,
                                   @Body Question question);

        //UpVote Question OK
        @POST("questions/{question_id}/upvote")
        Call<ResponseVote> UpVoteQuestion(@Header("x-session-token") String token,
                                          @Path("question_id") int question_id);

        //DownVote Question
        @POST("questions/{question_id}/downvote")
        Call<ResponseVote> DownVoteQuestion(@Header("x-session-token") String token,
                                            @Path("question_id") int question_id);

        //FIM NEW BACK


        //change Status Question TESTAR
        @POST("controller/instruction/{instruction_id}/presentation/{presentation_id}/doubt/{doubt_id}/change-status")
        Call<Void> changeStatusDoubt(@Header("Cookie") String cookie,
                                     @Path("instruction_id") String instruction_id,
                                     @Path("presentation_id") String presentation_id,
                                     @Path("doubt_id") String doubt_id,
                                     @Body String json);

        /*new Question
        @POST("controller/instruction/{instruction_id}/presentation/{presentation_id}/doubt")
        Call<Question> postDoubt(@Header("Cookie") String cookie,
                              @Path("instruction_id") String instruction_id,
                              @Path("presentation_id") String presentation_id,
                              @Body Question doubt);
        */

        //new Material Discipline OK
        @Multipart
        @POST("controller/instruction/{instruction_id}/material")
        Call<Void> postMaterial(@Header("Cookie") String cookie,
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

    private static String getToken(Context context) {
        Preference preference = new Preference();
        return preference.getToken(context);
    }

    private static void onRequestSuccess() {
        // TODO: 03/08/2016
        Log.d("API >>", "onRequestSuccess");
    }

    private static void onRequestFailure() {
        // TODO: 03/08/2016
        Log.d("API >>", "onRequestFailure");
    }

    public static void newMaterial(Context context, Instruction instruction, File file, String mimeType) {
        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .build();

        // Service setup
        HttpBinService service = retrofit.create(HttpBinService.class);

        // Prepare the HTTP request
        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<Void> call = service.createInstructionMaterial(getToken(context), instruction.getId(), body);

        // Asynchronously execute HTTP request
        call.enqueue(new Callback<Void>() {
            /**
             * onResponse is called when any kind of response has been received.
             */
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // isSuccess is true if response code => 200 and <= 300
                if (response.isSuccessful()) {
                    onRequestSuccess();
                }
            }

            /**
             * onFailure gets called when the HTTP request didn't get through.
             * For instance if the URL is invalid / host not reachable
             */
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onRequestFailure();
            }
        });
    }

}
