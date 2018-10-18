package kr.puze.dodam.Server

import kr.puze.dodam.Data.CharData
import kr.puze.dodam.Data.QuizData
import kr.puze.dodam.Data.UserData
import kr.puze.dodam.Data.WordData
import retrofit2.http.*

interface RetrofitService {

    @FormUrlEncoded
    @POST("/user")
    fun  post_user(
            @Field("username") username: String,
            @Field("email") email: String,
            @Field("gender") gender: String,
            @Field("password") password: String,
            @Field("api_key") api_key: String,
            @Field("country") country: String,
            @Field("mother_lang") mother_lang: String,
            @Field("account_type") account_type: String
    ): retrofit2.Call<UserData>

    @FormUrlEncoded
    @POST("/user/facebook")
    fun post_user_facebook(
            @Field("access_token") access_token: String
    ): retrofit2.Call<String>
    //response 형식 바꾸기

    @FormUrlEncoded
    @POST("/user/login")
    fun post_user_login(
            @Field("email") username: String,
            @Field("password") password: String
    ): retrofit2.Call<UserData>
    //response 형식 바꾸기

    @FormUrlEncoded
    @POST("/user/refresh")
    fun post_user_refresh(
            @Field("refresh_token") refresh_token: String
    ): retrofit2.Call<String>
    //response 형식 바꾸기

    @FormUrlEncoded
    @POST("/test/:question_id")
    fun post_test_question_id(
            @Field("answer") answer: String
    ): retrofit2.Call<String>
    //response 형식 바꾸기

    @FormUrlEncoded
    @POST("/word/quiz")
    fun post_word_quiz(
            @Field("key") key: String,
            @Field("answers") answers: List<Int>
    ): retrofit2.Call<String>
    //response 형식 바꾸기

    @GET("/users")
    fun get_users(): retrofit2.Call<WordData>

    @GET("/user/profile")
    fun get_user_profile(): retrofit2.Call<WordData>

    @GET("/reports")
    fun get_reports(): retrofit2.Call<WordData>

    @GET("/reports/<id>")
    fun get_reports_id(): retrofit2.Call<WordData>

    @GET("/missions")
    fun get_missions(): retrofit2.Call<WordData>

    @GET("/test")
    fun get_test(): retrofit2.Call<WordData>

    @GET("/word/quiz")
    fun get_word_quiz(): retrofit2.Call<QuizData>

    @GET("/char")
    fun get_char(): retrofit2.Call<CharData>

    @GET("/char/<id>")
    fun get_char_id(@Query("id") id: String): retrofit2.Call<CharData>

    @GET("/word")
    fun get_word(): retrofit2.Call<WordData>

    @GET("/word/<id>")
    fun get_word_id(): retrofit2.Call<WordData>

}