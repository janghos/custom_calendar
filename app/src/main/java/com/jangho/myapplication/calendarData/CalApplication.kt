package com.jangho.myapplication.calendarData

import android.app.Application
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jangho.myapplication.BuildConfig
import com.jangho.myapplication.CalendarService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class CalApplication : Application() {

    companion object {
        private var instance: CalApplication? = null

        fun getInstance(): CalApplication {
            if (instance == null) {
                instance = CalApplication()
            }
            return instance!!
        }
    }

    private var retrofit: Retrofit? = null
    private lateinit var service: CalendarService

    private val CONNECT_TIMEOUT_SEC = 20000L

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getInstanceRetrofit(): Retrofit {
        if (retrofit == null) {
            val interceptor = HttpLoggingInterceptor()

            if (BuildConfig.DEBUG) {
                interceptor.level = HttpLoggingInterceptor.Level.BODY
            } else {
                interceptor.level = HttpLoggingInterceptor.Level.NONE
            }

            val client = OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                .writeTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl("https://apis.data.go.kr/")
                .client(client)
                .addConverterFactory(
                    (Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                    }.asConverterFactory("application/json".toMediaType()))
                )
                .build()
        }

        return retrofit!!
    }

    fun getService(): CalendarService {
        if (!::service.isInitialized) {
            service = getInstanceRetrofit().create(CalendarService::class.java)
        }
        return service
    }
}