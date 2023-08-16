package com.example.chatgptapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etQuestion=findViewById<EditText>(R.id.etQuestion)
        val btnSubmit=findViewById<Button>(R.id.btnSubmit)
        val btnReset=findViewById<Button>(R.id.btnReset)
        val txtResponse=findViewById<TextView>(R.id.txtResponse)


        btnSubmit.setOnClickListener{
            val question=etQuestion.text.toString()
            Toast.makeText(this,question.toString(),Toast.LENGTH_SHORT).show()
            getResponse(question){response->
                runOnUiThread{
                    txtResponse.text=response
                }
            }
        }
        btnReset.setOnClickListener{
            txtResponse.text=null
        }

    }

    fun getResponse(question: String, callback: (String) -> Unit){
        val apiKey="put your chatgpt api key here"
        val url="https://api.openai.com/v1/completions"
        val requestBody="""
            {
             "model": "text-davinci-003",
              "prompt": "$question",
              "max_tokens": 500,
              "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type","application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error","API field",e)
            }

            override fun onResponse(call: Call, response: Response) {
                    val body=response.body?.string()
                    if (body != null) {
                        Log.v("data",body)
                    }
                    else{
                        Log.v("data", "empty")
                    }
                    val jsonObject=JSONObject(body!!)
                    val jsonArray:JSONArray=jsonObject.getJSONArray("choices")
                    val textResult=jsonArray.getJSONObject(0).getString("text")
                    callback(textResult)
            }

        })
    }


}