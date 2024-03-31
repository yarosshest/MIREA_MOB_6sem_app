package com.example.mirea_mob_6sem.recommendation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mirea_mob_6sem.AppViewModel
import com.example.mirea_mob_6sem.R
import com.example.mirea_mob_6sem.api.Api
import com.example.mirea_mob_6sem.api.Film
import com.example.mirea_mob_6sem.api.RetrofitHelper
import com.example.mirea_mob_6sem.databinding.FragmentRecommendationBinding
import com.example.mirea_mob_6sem.databinding.FragmentTimerBinding
import com.example.mirea_mob_6sem.find.FilmAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecommendationFragment : Fragment() {
    private lateinit var saveAdapter : FilmAdapter


    private val viewModel: AppViewModel by activityViewModels()

    private lateinit var binding: FragmentRecommendationBinding

    private lateinit var getButton: Button
    private lateinit var bar: ProgressBar
    private lateinit var textError: TextView
    private lateinit var listFilm: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentRecommendationBinding.inflate(layoutInflater)

        getButton  = binding.buttonRecommindations
        bar = binding.progressBar
        textError = binding.textError
        listFilm = binding.listFilm
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listFilm.layoutManager = GridLayoutManager(context, 3)


        val savedFilms = viewModel.getRecommendList()


        val recyclerViewObserver = Observer<List<Film>> {
            val adapter = createAdapter(it)
            listFilm.adapter = adapter
            getButton.visibility = View.VISIBLE
            bar.visibility = View.GONE
        }


        viewModel.getObserveRecommendList().observe(viewLifecycleOwner, recyclerViewObserver)

        val searching = viewModel.getStatusRecommendService()

        if (searching == true) {
            getButton.visibility = View.GONE
            bar.visibility = View.VISIBLE
        }


        if (this::saveAdapter.isInitialized){
            saveAdapter.setOnClickListener(object :
                FilmAdapter.OnClickListener {
                override fun onClick(position: Int, model: Film) {
                    val bundle = bundleOf("id_film" to model.id)
                    view.findNavController().navigate(
                        R.id.action_recommendationFragment_to_productFragment,
                        bundle
                    )
                }
            })
            listFilm.adapter = saveAdapter
        }else if (savedFilms != null){
            val adapter = createAdapter(savedFilms)
            listFilm.adapter = adapter
        }


        getButton.setOnClickListener {
            viewModel.saveStatusRecommendService(true)
            bar.visibility = View.VISIBLE
            getButton.visibility = View.GONE

            val api = RetrofitHelper.getInstance().create(Api::class.java)
            val call = api.get_recommendations()

            call.enqueue(object : Callback<List<Film>>{
                override fun onResponse(call: Call<List<Film>>, response: Response<List<Film>>) {
                    viewModel.saveStatusRecommendService(false)
                    bar.visibility = View.GONE
                    getButton.visibility = View.VISIBLE
                    if (response.isSuccessful) {
                        val films = response.body()
                        if (films != null) {
                            val adapter = createAdapter(films)
                            listFilm.adapter = adapter
                            saveAdapter = adapter
                            viewModel.saveRecommendList(films)
                        }
                    } else if (response.code() == 405) {
                        textError.text = "Less 2 positive rates"
                        textError.visibility = View.VISIBLE
                    }else if (response.code() == 406) {
                        textError.text = "Less 2 negative rates"
                        textError.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<List<Film>>, t: Throwable) {
                    textError.text = "Connection lost"
                    textError.visibility = View.VISIBLE
                    println("Network Error :: " + t.localizedMessage);
                }
            })
        }
    }

     fun createAdapter(films : List<Film>) : FilmAdapter{
        val adapter = FilmAdapter(films)
        adapter.setOnClickListener(object :
            FilmAdapter.OnClickListener {
            override fun onClick(position: Int, model: Film) {
                val bundle = bundleOf("id_film" to model.id)
                view?.findNavController()?.navigate(
                    R.id.action_recommendationFragment_to_productFragment,
                    bundle
                )
            }
        })
        return adapter
    }

}