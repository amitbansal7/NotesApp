package com.amitbansal.notesapp.api

import com.amitbansal.notesapp.util.Constants.BASE_URL
import com.amitbansal.notesapp.util.Constants.SECRET_KEY
import com.amitbansal.notesapp.util.Constants.SECRET_VALUE
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private val retrofit: Retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            var client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val req =
                        chain.request().newBuilder().addHeader(SECRET_KEY, SECRET_VALUE).build()
                    chain.proceed(req)
                }.build()


            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val authApi by lazy {
            retrofit.create(AuthApi::class.java)
        }

        val notesApi by lazy {
            retrofit.create(NotesApi::class.java)
        }
    }
}