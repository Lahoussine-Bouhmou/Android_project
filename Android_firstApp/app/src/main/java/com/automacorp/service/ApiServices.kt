package com.automacorp.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ApiServices {
    companion object {
        private const val API_USERNAME = "user"
        private const val API_PASSWORD = "password"

        val roomsApiService: RoomsApiService by lazy {
            val client = getUnsafeOkHttpClient()
                .addInterceptor(BasicAuthInterceptor(API_USERNAME, API_PASSWORD))
                .build()

            Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client)
                .baseUrl("https://automacorp.devmind.cleverapps.io/api/")
                .build()
                .create(RoomsApiService::class.java)
        }

        val windowsApiService: WindowsApiService by lazy {
            val client = getUnsafeOkHttpClient()
                .addInterceptor(BasicAuthInterceptor(API_USERNAME, API_PASSWORD))
                .build()

            Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client)
                .baseUrl("https://automacorp.devmind.cleverapps.io/api/")
                .build()
                .create(WindowsApiService::class.java)
        }

        private fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
            return try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL").apply {
                    init(null, trustAllCerts, SecureRandom())
                }

                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory

                OkHttpClient.Builder().apply {
                    sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    hostnameVerifier { _, _ -> true }
                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}
