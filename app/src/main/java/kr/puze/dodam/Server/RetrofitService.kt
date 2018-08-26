package kr.puze.dodam.Server

import kr.puze.dodam.Data.CharData
import kr.puze.dodam.Data.QuizData
import kr.puze.dodam.Data.UserData
import kr.puze.dodam.Data.WordData
import retrofit2.http.*

interface RetrofitService {

    @FormUrlEncoded
    @POST("/word/quiz")
    fun word_quiz(@Field("key") key: String, @Field("answers") answers: List<Int>): retrofit2.Call<String>

    @GET("/word/quiz")
    fun word_quiz(): retrofit2.Call<QuizData>

    @GET("/char")
    fun char(): retrofit2.Call<CharData>

    @GET("/char/<id>")
    fun char_id(): retrofit2.Call<CharData>

    @GET("/word")
    fun word(): retrofit2.Call<WordData>

    @GET("/word/<id>")
    fun word_id(): retrofit2.Call<WordData>

}