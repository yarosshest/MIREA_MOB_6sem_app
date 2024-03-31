package com.example.mirea_mob_6sem

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.mirea_mob_6sem.api.Api
import com.example.mirea_mob_6sem.api.Film
import com.example.mirea_mob_6sem.api.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val likeButton : ImageButton = view.findViewById(R.id.imageButtonLike)
        val dislikeButton : ImageButton = view.findViewById(R.id.imageButtonDislike)


        val api = RetrofitHelper.getInstance().create(Api::class.java)
        if (arguments != null) {
            val id = requireArguments().getInt("id_film")

            likeButton.setOnClickListener {
                rateFilm(true, id)
                view.findNavController().popBackStack()

            }

            dislikeButton.setOnClickListener {
                rateFilm(false, id)
                view.findNavController().popBackStack()
            }

            val photo : ImageView = view.findViewById(R.id.imageViewProduct)
            val description : TextView = view.findViewById(R.id.textViewDescription)
            val call = api.get_film(
                id = requireArguments().getInt("id_film"),
            )

            call.enqueue(object : Callback<Film> {
                override fun onResponse(
                    call: Call<Film>,
                    response: Response<Film>
                ) {
                    if (response.isSuccessful) {
                        val film = response.body()
                        if (film != null) {
                            description.text = film.description
                            val currentUrl: String = film.photo
                            Glide.with(view)
                                .load(currentUrl)
                                .into(photo)
                        } else {
                            if (response.code() == 404)
                                description.text = "Film not found"
                        }
                    }
                }

                override fun onFailure(call: Call<Film>, t: Throwable) {
                    println("Network Error :: " + t.localizedMessage);
                }

            })
        }
    }

    private fun rateFilm(rate: Boolean, id: Int){
        val api = RetrofitHelper.getInstance().create(Api::class.java)
        val call = api.rate(
            id = id,
            rate = rate
        )

        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val duration = Toast.LENGTH_SHORT
                    if (body != null) {

                        val toast = Toast.makeText(context, "Оценено", duration)
                        toast.setGravity(Gravity.BOTTOM, 0, 0)
                        toast.show()
                    } else {
                        if (response.code() == 404) {
                            val toast = Toast.makeText(context, "Ошибка сервиса", duration)
                            toast.setGravity(Gravity.BOTTOM, 0, 0)
                            toast.show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                println("Network Error :: " + t.localizedMessage);
            }

        })
    }

}
