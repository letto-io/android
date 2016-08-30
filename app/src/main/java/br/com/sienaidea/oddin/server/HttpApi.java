package br.com.sienaidea.oddin.server;

import android.content.Context;
import android.service.voice.VoiceInteractionService;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.sienaidea.oddin.retrofitModel.Answer;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Material;
import br.com.sienaidea.oddin.retrofitModel.Person;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.retrofitModel.Profile;
import br.com.sienaidea.oddin.retrofitModel.Question;
import br.com.sienaidea.oddin.retrofitModel.ResponseConfirmMaterial;
import br.com.sienaidea.oddin.retrofitModel.ResponseCredentialsMaterial;
import br.com.sienaidea.oddin.retrofitModel.ResponseUpVoteAnswer;
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
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class HttpApi {
    //public static final String API_URL = "http://ws-oddin.herokuapp.com/"; //produção
    public static final String API_URL = "http://ws-edupanel.herokuapp.com/"; //testes

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

        //Create Session OK
        @POST("session")
        Call<Session> Login(@Body User user);

        //Delete Session OK
        @DELETE("session")
        Call<Void> Logoff(@Header("x-session-token") String token);

        //Recover Password OK
        @POST("recover-password")
        Call<Void> recoverPassword(@Body User user);

        //Get Profile OK
        @GET("instructions/{instruction_id}/profile")
        Call<Profile> Profile(@Header("x-session-token") String token,
                              @Path("instruction_id") int instruction_id);

        //Get Instructions OK
        @GET("instructions")
        Call<List<Instruction>> Instructions(@Header("x-session-token") String token);

        //Get Instruction Materials OK
        @GET("instructions/{instruction_id}/materials")
        Call<List<Material>> InstructionMaterials(@Header("x-session-token") String token,
                                                  @Path("instruction_id") int instruction_id);

        //Delete Material
        @DELETE("materials/{material_id}")
        Call<Void> deleteInstructionMaterial(@Header("x-session-token") String token,
                                             @Path("material_id") int material_id);

        //Get Presentation Materials
        @GET("presentations/{presentation_id}/materials")
        Call<List<Material>> PresentationMaterials(@Header("x-session-token") String token,
                                                   @Path("presentation_id") int presentation_id);

        //Get Credentials Instruction Material OK
        @GET("instructions/{instruction_id}/materials/new")
        Call<ResponseCredentialsMaterial> createInstructionMaterial(@Header("x-session-token") String token,
                                                                    @Path("instruction_id") int instruction_id);

        //Send File to Amazon OK
        @Multipart
        @POST("./")
        Call<Void> sendMaterial(@Part("key") RequestBody key,
                                @Part("policy") RequestBody policy,
                                @Part("x-amz-credential") RequestBody x_amz_credential,
                                @Part("x-amz-algorithm") RequestBody x_amz_algorithm,
                                @Part("x-amz-date") RequestBody x_amz_date,
                                @Part("x-amz-signature") RequestBody x_amz_signature,
                                @Part MultipartBody.Part file);

        //Cofirm Material OK
        @PUT("materials/{material_id}")
        Call<ResponseConfirmMaterial> confirmMaterial(@Header("x-session-token") String token,
                                                      @Path("material_id") int material_id,
                                                      @Body Material material);

        //Get Material OK
        @GET("materials/{material_id}")
        Call<ResponseConfirmMaterial> getMaterial(@Header("x-session-token") String token,
                                                  @Path("material_id") int material_id);

        //Get Presentations OK
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

        //Get Participants OK
        @GET("instructions/{instruction_id}/participants")
        Call<List<Person>> Participants(@Header("x-session-token") String token,
                                        @Path("instruction_id") int instruction_id);

        //Get Questions OK
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

        //Get Question Answers OK
        @GET("questions/{question_id}/answers")
        Call<List<Answer>> getAnswers(@Header("x-session-token") String token,
                                      @Path("question_id") int question_id);

        //UpVote Answer OK
        @POST("answers/{answer_id}/upvote")
        Call<ResponseUpVoteAnswer> upVoteAnswer(@Header("x-session-token") String token,
                                                @Path("answer_id") int answer_id);

        //DownVote Answer OK
        @POST("answers/{answer_id}/downvote")
        Call<ResponseUpVoteAnswer> downVoteAnswer(@Header("x-session-token") String token,
                                                  @Path("answer_id") int answer_id);

        //Accept Answer
        @POST("answers/{answer_id}/accept")
        Call<Void> acceptAnswer(@Header("x-session-token") String token,
                                @Path("answer_id") int answer_id);

        //Delete Accept Answer
        @POST("answers/{answer_id}/accept")
        Call<Void> deleteAcceptAnswer(@Header("x-session-token") String token,
                                      @Path("answer_id") int answer_id);

        //Create Answer
        @POST("questions/{question_id}/answers")
        Call<Answer> createAnswer(@Header("x-session-token") String token,
                                  @Path("question_id") int question_id,
                                  @Body Answer answer);


    }
}
