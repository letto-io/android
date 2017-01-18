package br.com.sienaidea.oddin.server;

import java.util.List;

import br.com.sienaidea.oddin.retrofitModel.Answer;
import br.com.sienaidea.oddin.retrofitModel.Date;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Material;
import br.com.sienaidea.oddin.retrofitModel.Notice;
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
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class HttpApi {
    public static final String API_URL = "http://ws-oddin.herokuapp.com/"; //produção
    //public static final String API_URL = "http://ws-edupanel.herokuapp.com/"; //testes

    private static final String KEY_TOKEN = "x-session-token";

    private static final String INSTRUCTION_ID = "instruction_id";
    private static final String MATERIAL_ID = "material_id";
    private static final String PRESENTATION_ID = "presentation_id";
    private static final String QUESTION_ID = "question_id";
    private static final String ANSWER_ID = "answer_id";

    private static final String SESSION_PATH = "session";
    private static final String RECOVER_PASSWORD_PATH = "recover-password";
    private static final String PROFILE_PATH = "instructions/{instruction_id}/profile";
    private static final String INSTRUCTION_PATH = "instructions";
    private static final String PRESENTATION_PATH = "instructions/{instruction_id}/presentations";
    private static final String CLOSE_PRESENTATION_PATH = "presentations/{presentation_id}/close";
    private static final String INSTRUCTION_MATERIAL_PATH = "instructions/{instruction_id}/materials";
    private static final String MATERIAL_PATH = "materials/{material_id}";
    private static final String PRESENTATION_MATERIAL_PATH = "presentations/{presentation_id}/materials";
    private static final String GET_CREDENTIALS_INSTRUCTION_PATH = "instructions/{instruction_id}/materials";
    private static final String GET_CREDENTIALS_PRESENTATION_PATH = "presentations/{presentation_id}/materials";
    private static final String DELETE_PRESENTATION_PATH = "presentations/{presentation_id}";
    private static final String PARTICIPANT_PATH = "instructions/{instruction_id}/participants";
    private static final String QUESTIONS_PATH = "presentations/{presentation_id}/questions";
    private static final String NEW_QUESTION_PATH = "presentations/{presentation_id}/questions";
    private static final String UPVOTE_QUESTION_PATH = "questions/{question_id}/upvote";
    private static final String UPVOTE_ANSWER_PATH = "answers/{answer_id}/upvote";
    private static final String DOWN_ANSWER_PATH = "answers/{answer_id}/downvote";
    private static final String ANSWERS_PATH = "questions/{question_id}/answers";
    private static final String ACCEPT_ANSWER_PATH = "answers/{answer_id}/accept";
    private static final String ANSWER_MATERIALS_PATH = "questions/{question_id}/answers/materials";
    private static final String NOTICES_PATH = "instructions/{instruction_id}/notices";
    private static final String DATES_PATH = "instructions/{instruction_id}/dates";

    public interface HttpBinService {

        //Create Session OK
        @POST(SESSION_PATH)
        Call<Session> Login(@Body User user);

        //Delete Session OK
        @DELETE(SESSION_PATH)
        Call<Void> Logoff(@Header(KEY_TOKEN) String token);

        //Recover Password OK
        @POST(RECOVER_PASSWORD_PATH)
        Call<Void> recoverPassword(@Body User user);

        //Get Profile OK
        @GET(PROFILE_PATH)
        Call<Profile> Profile(@Header(KEY_TOKEN) String token,
                              @Path(INSTRUCTION_ID) int instruction_id);

        //Get Instructions OK
        @GET(INSTRUCTION_PATH)
        Call<List<Instruction>> Instructions(@Header(KEY_TOKEN) String token);

        //Get Instruction Materials OK
        @GET(INSTRUCTION_MATERIAL_PATH)
        Call<List<Material>> InstructionMaterials(@Header(KEY_TOKEN) String token,
                                                  @Path(INSTRUCTION_ID) int instruction_id);

        //Delete Material OK
        @DELETE(MATERIAL_PATH)
        Call<Void> deleteMaterial(@Header(KEY_TOKEN) String token,
                                  @Path(MATERIAL_ID) int material_id);

        //Get Presentation Materials OK
        @GET(PRESENTATION_MATERIAL_PATH)
        Call<List<Material>> PresentationMaterials(@Header(KEY_TOKEN) String token,
                                                   @Path(PRESENTATION_ID) int presentation_id);

        //Get Credentials Instruction Material OK
        @POST(GET_CREDENTIALS_INSTRUCTION_PATH)
        Call<ResponseCredentialsMaterial> createInstructionMaterial(@Header(KEY_TOKEN) String token,
                                                                    @Path(INSTRUCTION_ID) int instruction_id);

        //Get Credentials Presentation Material
        @POST(GET_CREDENTIALS_PRESENTATION_PATH)
        Call<ResponseCredentialsMaterial> createPresentationMaterial(@Header(KEY_TOKEN) String token,
                                                                     @Path(PRESENTATION_ID) int presentation_id);

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
        @PUT()
        Call<ResponseConfirmMaterial> confirmMaterial(@Header(KEY_TOKEN) String token,
                                                      @Path(MATERIAL_ID) int material_id,
                                                      @Body Material material);

        //Get Material OK
        @GET(MATERIAL_PATH)
        Call<ResponseConfirmMaterial> getMaterial(@Header(KEY_TOKEN) String token,
                                                  @Path(MATERIAL_ID) int material_id);

        //Get Presentations OK
        @GET(PRESENTATION_PATH)
        Call<List<Presentation>> Presentations(@Header(KEY_TOKEN) String token,
                                               @Path(INSTRUCTION_ID) int instruction_id);

        //New Presentation OK
        @POST(PRESENTATION_PATH)
        Call<Presentation> NewPresentation(@Header(KEY_TOKEN) String token,
                                           @Path(INSTRUCTION_ID) String instruction_id,
                                           @Body Presentation presentation);

        //Close Presentation OK
        @POST(CLOSE_PRESENTATION_PATH)
        Call<Presentation> ClosePresentation(@Header(KEY_TOKEN) String token,
                                             @Path(PRESENTATION_ID) int presentation_id);

        //Delete Presentation
        @DELETE(DELETE_PRESENTATION_PATH)
        Call<Void> deletePresentation(@Header(KEY_TOKEN) String token,
                                      @Path(PRESENTATION_ID) int presentation_id);

        //Get Participants OK
        @GET(PARTICIPANT_PATH)
        Call<List<Person>> Participants(@Header(KEY_TOKEN) String token,
                                        @Path(INSTRUCTION_ID) int instruction_id);

        //Get Questions OK
        @GET(QUESTIONS_PATH)
        Call<List<Question>> Questions(@Header(KEY_TOKEN) String token,
                                       @Path(PRESENTATION_ID) int presentation_id);

        //New Question OK
        @POST(NEW_QUESTION_PATH)
        Call<Question> NewQuestion(@Header(KEY_TOKEN) String token,
                                   @Path(PRESENTATION_ID) int presentation_id,
                                   @Body Question question);

        //UpVote Question OK
        @POST(UPVOTE_QUESTION_PATH)
        Call<ResponseVote> UpVoteQuestion(@Header(KEY_TOKEN) String token,
                                          @Path(QUESTION_ID) int question_id);

        //Get Question Answers OK
        @GET(ANSWERS_PATH)
        Call<List<Answer>> getAnswers(@Header(KEY_TOKEN) String token,
                                      @Path(QUESTION_ID) int question_id);

        //UpVote Answer OK
        @POST(UPVOTE_ANSWER_PATH)
        Call<ResponseUpVoteAnswer> upVoteAnswer(@Header(KEY_TOKEN) String token,
                                                @Path(ANSWER_ID) int answer_id);

        //DownVote Answer OK
        @POST(DOWN_ANSWER_PATH)
        Call<ResponseUpVoteAnswer> downVoteAnswer(@Header(KEY_TOKEN) String token,
                                                  @Path(ANSWER_ID) int answer_id);

        //Accept Answer OK
        @POST(ACCEPT_ANSWER_PATH)
        Call<Void> acceptAnswer(@Header(KEY_TOKEN) String token,
                                @Path(ANSWER_ID) int answer_id);

        //Create Answer OK
        @POST(ANSWERS_PATH)
        Call<Answer> createAnswer(@Header(KEY_TOKEN) String token,
                                  @Path(QUESTION_ID) int question_id,
                                  @Body Answer answer);

        //Create Answer Material OK
        @POST(ANSWER_MATERIALS_PATH)
        Call<ResponseCredentialsMaterial> createAnswerMaterial(@Header(KEY_TOKEN) String token,
                                                               @Path(QUESTION_ID) int question_id);

        //Get Notices OK
        @GET(NOTICES_PATH)
        Call<List<Notice>> getInstructionNotices(@Header(KEY_TOKEN) String token,
                                                 @Path(INSTRUCTION_ID) int question_id);

        //Post Notices OK
        @POST(NOTICES_PATH)
        Call<Notice> createInstructionNotices(@Header(KEY_TOKEN) String token,
                                              @Path(INSTRUCTION_ID) int instruction_id,
                                              @Body Notice notice);

        //Get Dates OK
        @GET(DATES_PATH)
        Call<List<Date>> getInstructionDates(@Header(KEY_TOKEN) String token,
                                             @Path(INSTRUCTION_ID) int instruction_id);

        //Create Dates OK
        @POST(DATES_PATH)
        Call<Date> createInstructionDate(@Header(KEY_TOKEN) String token,
                                         @Path(INSTRUCTION_ID) int instruction_id,
                                         @Body Date date);
    }
}
