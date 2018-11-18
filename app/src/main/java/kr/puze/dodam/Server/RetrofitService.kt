package kr.puze.dodam.Server

import kr.puze.dodam.Data.*
import retrofit2.http.*

interface RetrofitService {

    @FormUrlEncoded
    @POST("/user")
    fun post_user(
            @Field("username") username: String,
            @Field("email") email: String,
            @Field("gender") gender: String,
            @Field("password") password: String,
            @Field("api_key") api_key: String,
            @Field("third_user_id") third_user_id: String,
            @Field("country") country: String,
            @Field("mother_lang") mother_lang: String,
            @Field("account_type") account_type: String
    ): retrofit2.Call<UserData>

    @GET("/user")
    fun get_user(
            @Header("Authorization") auth_token: String
    ): retrofit2.Call<UserData>
    //서버 독스 필요

    @PUT("/users")
    fun put_user(
            @Path("Authorization") auth_token: String
    ): retrofit2.Call<UserData>
    //서버 독스 필요

    @PUT("/user/password")
    fun put_user_password(
            @Field("current_password") current_password: String,
            @Field("next_password") next_password: String
    ): retrofit2.Call<String>
    //서버 독스 필요

    @GET("/user/profile")
    fun get_user_profile(
            @Path("Authorization") auth_token: String
    ): retrofit2.Call<String>
    //서버 독스 필요

    @FormUrlEncoded
    @POST("/user/facebook")
    fun post_user_facebook(
            @Field("access_token") access_token: String
    ): retrofit2.Call<FacebookUserData>
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
    fun get_char(
            @Header("Authorization") auth_token: String
    ): retrofit2.Call<CharData>

    @GET("/char/{id}")
    fun get_char_id(
            @Header("Authorization") auth_token: String,
            @Path("id") id: String
    ): retrofit2.Call<CharData>

    @GET("/phonetic")
    fun get_phonetic(
            @Header("Authorization") auth_token: String
    ): retrofit2.Call<PhoneticListData>

    @GET("/phonetic/{phonetic_id}")
    fun get_phonetic_id(
            @Header("Authorization") auth_token: String,
            @Path("phonetic_id") id: String
    ): retrofit2.Call<PhoneticData>

    @GET("/word")
    fun get_word(
            @Header("Authorization") auth_token: String
    ): retrofit2.Call<WordListData>

    @GET("/word/{id}")
    fun get_word_id(
            @Header("Authorization") auth_token: String,
            @Path("id") id: String
    ): retrofit2.Call<WordDataList>

    @GET("/theme")
    fun get_debate_theme(
            @Header("Authorization") auth_token: String
    ): retrofit2.Call<DebateThemeListData>

    @FormUrlEncoded
    @POST("/theme")
    fun post_debate_theme(
            @Header("Authorization") auth_token: String,
            @Field("blue") blue: String,
            @Field("red") red: String,
            @Field("deadline") deadline: String
    ): retrofit2.Call<String>

    @FormUrlEncoded
    @POST("/theme/join")
    fun post_debate_theme_join(
            @Header("Authorization") auth_token: String,
            @Field("theme_id") theme_id: String,
            @Field("team") team: String
    ): retrofit2.Call<RoomData>
    //team 에 red 혹은 blue 를 넣어야함

    @GET("/room/{room_id}")
    fun get_room(
            @Header("Authorization") auth_token: String,
            @Path("room_id") room_id: String
    ): retrofit2.Call<RoomData>

    @GET("/room/{room_id}/quit")
    fun get_room_quit(
            @Header("Authorization") auth_token: String,
            @Path("room_id") room_id: String
    ): retrofit2.Call<RoomData>
}