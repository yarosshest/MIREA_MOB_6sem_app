package com.example.mirea_mob_6sem.find

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mirea_mob_6sem.AppViewModel
import com.example.mirea_mob_6sem.R
import com.example.mirea_mob_6sem.api.Api
import com.example.mirea_mob_6sem.api.Film
import com.example.mirea_mob_6sem.api.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FindFragment : Fragment() {
    private lateinit var saveAdapter : FilmAdapter

    private val viewModel: AppViewModel by activityViewModels()

    private lateinit var listFilm : RecyclerView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textError : TextView = view.findViewById(R.id.textError)

        val buttonUpdate : Button = view.findViewById(R.id.buttonUpdate)
        buttonUpdate.setOnClickListener { v ->
            update(v)
        }

        val search = view.findViewById<SearchView>(R.id.searchName)
        search.setQuery(viewModel.getFindLine(), false)

        listFilm = view.findViewById(R.id.listFilm)
        val savedFilms = viewModel.getFindList()


        listFilm.layoutManager = GridLayoutManager(context, 3)

        if (this::saveAdapter.isInitialized){
            saveAdapter.setOnClickListener(object :
                FilmAdapter.OnClickListener {
                override fun onClick(position: Int, model: Film) {
                    val bundle = bundleOf("id_film" to model.id)
                    view.findNavController().navigate(
                        R.id.action_findFragment_to_productFragment,
                        bundle
                    )
                }
            })
            listFilm.adapter = saveAdapter
        } else if (savedFilms != null){
            val adapter = createAdapter(savedFilms)
            listFilm.adapter = adapter
        }

        val api = RetrofitHelper.getInstance().create(Api::class.java)


        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    viewModel.saveFindLine(p0)
                }
                p0?.let { api.find(line = it) }
                    ?.enqueue(object : Callback<List<Film>> {
                        override fun onResponse(
                            call: Call<List<Film>>,
                            response: Response<List<Film>>
                        ) {
                            buttonUpdate.visibility = View.GONE
                            if (response.isSuccessful) {
                                textError.visibility = View.GONE
                                val films = response.body()
                                if (films != null) {
                                    val adapter = createAdapter(films)
                                    listFilm.adapter = adapter
                                    saveAdapter = adapter
                                    viewModel.saveFindList(films)
                                }
                            }else if (response.code() == 404){
                                textError.text = "Films not found"
                                textError.visibility = View.VISIBLE
                            }
                        }

                        override fun onFailure(call: Call<List<Film>>, t: Throwable) {
                            textError.text = "Network Error :: " + t.localizedMessage
                            textError.visibility = View.VISIBLE
                            buttonUpdate.visibility = View.VISIBLE
                            println("Network Error :: " + t.localizedMessage);
                        }

                    })
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                return true
            }
        })
    }


    private fun createAdapter(films : List<Film>) : FilmAdapter{
        val adapter = FilmAdapter(films)
        adapter.setOnClickListener(object :
            FilmAdapter.OnClickListener {
            override fun onClick(position: Int, model: Film) {
                val bundle = bundleOf("id_film" to model.id)
                view?.findNavController()?.navigate(
                    R.id.action_findFragment_to_productFragment,
                    bundle
                )
            }
        })
        return adapter
    }

    fun update(view: View) {
        val api = RetrofitHelper.getInstance().create(Api::class.java)
        val textError : TextView = view.findViewById(R.id.textError)
        val buttonUpdate : Button = view.findViewById(R.id.buttonUpdate)

        viewModel.getFindLine().let { api.find(line = it) }
            .enqueue(object : Callback<List<Film>> {
                override fun onResponse(
                    call: Call<List<Film>>,
                    response: Response<List<Film>>
                ) {
                    buttonUpdate.visibility = View.GONE
                    if (response.isSuccessful) {
                        textError.visibility = View.GONE
                        val films = response.body()
                        if (films != null) {
                            val adapter = createAdapter(films)
                            listFilm.adapter = adapter
                            saveAdapter = adapter
                            viewModel.saveFindList(films)
                        }
                    }else if (response.code() == 404){
                        textError.text = "Films not found"
                        textError.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<List<Film>>, t: Throwable) {
                    textError.text = "Network Error :: " + t.localizedMessage
                    textError.visibility = View.VISIBLE
                    buttonUpdate.visibility = View.VISIBLE
                    println("Network Error :: " + t.localizedMessage);
                }

            })

    }

}


