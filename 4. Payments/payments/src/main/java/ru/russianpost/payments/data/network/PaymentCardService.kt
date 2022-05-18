package ru.russianpost.payments.data.network

import okhttp3.ResponseBody
import retrofit2.http.GET

/** Сервис оплаты картой */
internal interface PaymentCardService {
//    "http://www.africau.edu/images/default/sample.pdf"
    @GET("https://mindorks.s3.ap-south-1.amazonaws.com/courses/MindOrks_Android_Online_Professional_Course-Syllabus.pdf")
    suspend fun getCheck(): ResponseBody
}
