package kr.puze.dodam.Server

import kr.puze.dodam.Data.*
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

    @GET("/auth/user")
    fun get_auth_user(): retrofit2.Call<UserData>
    //서버 독스 필요

    @PUT("/auth/users")
    fun put_auth_user(): retrofit2.Call<UserData>
    //서버 독스 필요

    @PUT("/auth/user/password")
    fun put_auth_user_password(
            @Field("current_password") current_password: String,
            @Field("next_password") next_password: String
    ): retrofit2.Call<String>
    //서버 독스 필요

    @GET("/auth/user/profile")
    fun get_auth_user_profile(): retrofit2.Call<String>
    //서버 독스 필요

    @FormUrlEncoded
    @POST("/user/facebook")
    fun post_user_facebook(
            @Field("access_token") access_token: String
    ): retrofit2.Call<UserData>
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
    fun get_users(): retrofit2.Call<WordListData>

    @GET("/user/profile")
    fun get_user_profile(): retrofit2.Call<WordListData>

    @GET("/reports")
    fun get_reports(): retrofit2.Call<WordListData>

    @GET("/reports/<id>")
    fun get_reports_id(): retrofit2.Call<WordListData>

    @GET("/missions")
    fun get_missions(): retrofit2.Call<WordListData>

    @GET("/test")
    fun get_test(): retrofit2.Call<WordListData>

    @GET("/word/quiz")
    fun get_word_quiz(): retrofit2.Call<QuizData>

    @GET("/char")
    fun get_char(): retrofit2.Call<CharData>

    @GET("/char")
    fun get_char_id(@Query("id") id: String): retrofit2.Call<CharData>

    @GET("/word")
    fun get_word(): retrofit2.Call<WordListData>

    @GET("/word")
    fun get_word_id(@Query("id") id: String): retrofit2.Call<WordData>

    @GET("/theme")
    fun get_debate_theme(): retrofit2.Call<DebateThemeListData>

    @FormUrlEncoded
    @POST("/theme")
    fun post_debate_theme(
            @Field("blue") blue: String,
            @Field("red") red: String,
            @Field("deadline") deadline: String
    ): retrofit2.Call<String>

    @FormUrlEncoded
    @POST("/theme/join")
    fun post_debate_theme_join(
            @Field("theme_id") theme_id: String,
            @Field("team") team: String
    ): retrofit2.Call<String>
    //team 에 red 혹은 blue 를 넣어야함

    @GET("/room")
    fun get_room(@Query("room_id") room_id: String): retrofit2.Call<RoomData>

    @GET("/room/<room_id>/quit")
    fun get_room_quit(@Query("room_id") room_id: String): retrofit2.Call<RoomData>
}