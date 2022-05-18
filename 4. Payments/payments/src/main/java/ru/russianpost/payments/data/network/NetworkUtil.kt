package ru.russianpost.payments.data.network

import android.content.Context
import coil.Coil
import coil.ImageLoader
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal class TestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response = chain.proceed(request)
        val isS = response.isSuccessful
//  check: response.body?.string()
        return response
    }
}

internal class MockPochtaIdInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader("Authorization", "BearereyJraWQiOiIxIiwiYWxnIjoiUlM1MTIiLCJ0eXAiOiJhdCtqd3QifQ.eyJpc3MiOiJodHRwczovL3Bhc3Nwb3J0LnBvY2h0YS5ydS9wYy8iLCJleHAiOjE2NDk2Mjc2MTEsInN1YiI6IjE2MTdiYzU0LTAxZWYtNGE3OC04NmY4LTZlODFjOTdjNWU0ZCIsImNsaWVudF9pZCI6IkszOWhsSE4wczlNaHk0RmRxOWZVeXpjTzBXWWEiLCJjbGllbnRfbmFtZSI6Ik1vYmlsZUFwcGxpY2F0aW9uUHVibGljIiwiaWF0IjoxNjQ5NTkxNjExLCJqdGkiOiIwT0o1MDBJMEVESHp5b1FMIiwic2NvcGUiOiJvcGVuaWQiLCJpZHAiOiJwb2NodGFfY29ubmVjdCIsImRpZCI6IkVJRHE4azRxSDRuaTNRT0VEeUNsbVdralYzeW9NNlpSTWFEVHVpT21FMG89In0.Gar4nwmzEUZevjBFrFgNi8uSHpYKqLU4J6i82oWfEtGdmI7DUT1Gtxm8ktfD6JtL5RZVCO4fsJOUlCX3jbClelSOXtYrt9eP1bfhJLn5GuwKrdqYK-ErVKkpCTj5qQED49eN8ggIHBqT0HWAK520bXDWos5NtqA4poos0vtZw0DsS35J50Rzi3nhptjtltPRXB-WLJ1Z6EoPM0s6Ss8NQonA7zzSlDvox2VL5ymHwUfjTmMGDmjjt_-rBMTh_Ab39PEWXhYoeT1irBHpNCaVAUMn5_6-gbbAGm55n7X-X6C-vq5H0FlDy8klwwiOmdXXXDoPZrM9aSKpAgsmaWyDW8NlggpuZLIBlQF8CcEA-VNwKABSKG3SoIpoANP4MM65AwrM9xehz3mIgntzHd4MekFGDOdgtrv2Ru0O7JEdDfHKFH2uSYZUp7olEbdwsseRmlDdUIfmt-PRmhZE-RWomlfE8ncRjQ_ov7uwLUMcvZxoiHz5cYj7NPpbz34F5NBgj_NPfkKIgEcwvqpjb01aq3-FmoKtz3uBbvPmaHN97uBVHKWH3YiHs0wKCEkNAKurWiiVFWGGuJtzgCXm6kmFzBwj5g9_1IlKXQuz-omLJSTUv5wJFzMkeXJYSEjHlan6PIK8Q3Z6Q-CvDoewQ-4UGSnoSJ5QJO43QdZP2Jrv1eE")
                .build()
        )
    }
}

fun provideOkHttpClient(): OkHttpClient {
    // Create a trust manager that does not validate certificate chains
    val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
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
    val sslSocketFactory: SSLSocketFactory = sslContext.getSocketFactory()

    val client = OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
// TODO: не проходит (видимо заворачивается безопасностью) запрос наружу (для чека) с Authorization header-ом :) Решится когда у нас будет свой чек, либо авторизация с МП
//        .addInterceptor(MockPochtaIdInterceptor())
        .addInterceptor(TestInterceptor())
        .build()
    return client
}

fun setupCoil(context: Context) {
    val imageLoader = ImageLoader.Builder(context)
        // Create the OkHttpClient inside a lambda so it will be initialized lazily on a background thread.
        .okHttpClient { provideOkHttpClient() }
        .build()
    Coil.setImageLoader(imageLoader)
}