package ru.russianpost.payments.demo

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal class TestInterceptor : Interceptor {
    companion object {
        private const val ESIA_DATA_DEMO_ASSET = "demo/esia_data.json"
        private const val ESIA_REQUEST = "transfer.create"
        private val NEED_HEAD_HEADER_REQUESTS  = listOf<String>("transfers/", "cps/")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        val url = request.url.toString()

        request = when {
            NEED_HEAD_HEADER_REQUESTS.find { url.contains(it) } != null -> addHidHeader(request)
            else -> request
        }

        val response = chain.proceed(request)
        val isS = response.isSuccessful
//  check: response.body?.string()
//        return response

        return when {
            url.contains(ESIA_REQUEST) -> createEsiaResponse(response)
            else -> response
        }

//        throw IOException() Exception()
// for testing server error
/*
        val jsonObject = JSONObject()
        jsonObject.put("errorCode", 2000)
        jsonObject.put("errorMsg","страшная ошибка")
        val contentType = response.body?.contentType()
        // for object
//        val body = ResponseBody.create(contentType, jsonObject.toString())
        // for list
        val jsonArray = JSONArray().put(jsonObject)
        val body = ResponseBody.create(contentType, jsonArray.toString())
        //
        return response.newBuilder().body(body).build()
*/
    }

    // 69a64735-eab3-411b-a194-2bfa09822615 - 1 card
    // 181af4c9-564c-4dee-82da-cda39939b44e - 2 cards
    private fun addHidHeader(request: Request) : Request =
        request.newBuilder()
            .addHeader("RU-POST-MA-HID", "181af4c9-564c-4dee-82da-cda39939b44e")
            .build()

    private fun createEsiaResponse(response: Response) : Response {
        val jsonObject = JSONObject(readAssetsFile(ESIA_DATA_DEMO_ASSET))
        val contentType = response.body?.contentType()
        val body = jsonObject.toString().toResponseBody(contentType)
        return response.newBuilder().code(200).body(body).build()
    }

    private fun readAssetsFile(fileName : String): String =
        PaymentDemoApplication.instance.assets.open(fileName).bufferedReader().use { it.readText() }
}

internal class AuthorizationInterceptor : Interceptor {
    // TODO: временно: не проходит запрос наружу (для чека) с Authorization header-ом :) Решится когда у нас будет свой чек
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        if (!request.url.toString().contains(".pdf")) {
            request = request.newBuilder()
                .addHeader("Authorization", "Basic b3BheXRlc3Q6RHJ0NHdmMnMx")
                .build()
        }
        return chain.proceed(request)
    }
/*
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader("Authorization", "Basic b3BheXRlc3Q6RHJ0NHdmMnMx")
                .build()
        )
    }
*/
}

fun provideOkHttpClient(): OkHttpClient {
    // Create a trust manager that does not validate certificate chains
    val trustAllCerts: Array<TrustManager> = arrayOf(
        object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOf()
            }
        }
    )

    // Install the all-trusting trust manager
    val sslContext: SSLContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())

    // Create an ssl socket factory with our all-trusting manager
    val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

    val clientBuilder = OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
        .addInterceptor(AuthorizationInterceptor())
        .addInterceptor(TestInterceptor())

    addLogInterceptor(clientBuilder)

    return clientBuilder.build()
}

private fun addLogInterceptor(clientBuilder: OkHttpClient.Builder) {
    if (BuildConfig.DEBUG) {
        clientBuilder.addInterceptor(DebugOkHttpHelper.getInterceptor())
//        val logging = HttpLoggingInterceptor()
//        logging.level = HttpLoggingInterceptor.Level.BODY
//        clientBuilder.addInterceptor(logging)
    }
}
