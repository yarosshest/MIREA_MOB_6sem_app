package com.example.mirea_mob_6sem.aftorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.mirea_mob_6sem.MainAppActivity
import com.example.mirea_mob_6sem.R
import com.example.mirea_mob_6sem.api.Api
import com.example.mirea_mob_6sem.api.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onLoginClick(view: View) {
        val intent = Intent(this, MainAppActivity::class.java)

        val textLogin = findViewById<EditText>(R.id.edittTextTextLogin)
        val textPassword = findViewById<EditText>(R.id.editTextTextPassword)
        val textError = findViewById<TextView>(R.id.textError)

        val api = RetrofitHelper.getInstance().create(Api::class.java)
        val call = api.login(
            login = textLogin.text.toString(),
            password = textPassword.text.toString()
        )

        call.enqueue(object : Callback<Map<String, Int>> {
            override fun onResponse(
                call: Call<Map<String, Int>>,
                response: Response<Map<String, Int>>
            ) {
                if (response.isSuccessful) {
                    startActivity(intent)
                }else{
                    if (response.code() == 404)
                        textError.text = "User not found"
                }
            }

            override fun onFailure(call: Call<Map<String, Int>>, t: Throwable) {
                println("Network Error :: " + t.localizedMessage);
            }

        })
    }


    fun onClickRegister(view: View) {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }
}