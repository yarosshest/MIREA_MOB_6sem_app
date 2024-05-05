package com.example.mirea_mob_6sem.find

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
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


const val HISTORY_SIZE: Int = 10

class FindFragment : Fragment() {
    private lateinit var saveAdapter : FilmAdapter
    private val viewModel: AppViewModel by activityViewModels()
    private lateinit var listFilm : RecyclerView

    private val api = RetrofitHelper.getInstance().create(Api::class.java)
    private lateinit var historyLinearLayout : LinearLayout
    private lateinit var buttonUpdate : Button
    private lateinit var textError : TextView
    private lateinit var search : SearchView

    private var mainThreadHandler: Handler? = null


    private val searchRunnable = Runnable { searchRequest() }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainThreadHandler = Handler(Looper.getMainLooper())
        return inflater.inflate(R.layout.fragment_find, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textError = view.findViewById(R.id.textError)
        historyLinearLayout = view.findViewById(R.id.historyLayout)

        buttonUpdate = view.findViewById(R.id.buttonUpdate)
        buttonUpdate.setOnClickListener { v ->
            update(v)
        }

        search = view.findViewById(R.id.searchName)
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


        search.setOnQueryTextFocusChangeListener { v, hasFocus ->
            showHistory(hasFocus, historyLinearLayout)
        }
        
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    viewModel.saveFindLine(p0)
                }
                callApiSearch(p0.toString())
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                searchDebounce()
                return true
            }
        })
    }

    private fun showHistory(hasFocus: Boolean,linearLayout: LinearLayout){
        if (hasFocus) {
            linearLayout.visibility = View.VISIBLE
            val hist = getHistory()

            for (i in hist) {
                val inflater = LayoutInflater.from(context)
                val tv = inflater.inflate(R.layout.item_history, linearLayout, false) as TextView
                tv.text = i
                tv.setOnClickListener { v -> searchInHistory(v as TextView) }
                linearLayout.addView(tv)
            }
        }else{
            linearLayout.visibility = View.GONE
        }

    }

    private fun getHistory(): MutableList<String>{
        val sharedPreferences =activity?.getSharedPreferences(getString(R.string.history),
            Context.MODE_PRIVATE)
        val historySet = sharedPreferences?.getStringSet(getString(R.string.history), HashSet<String>())
        return ArrayList(historySet)
    }


    fun addInHistory(q: String){
        var searchHistory: MutableList<String> = getHistory()
        searchHistory.remove(q)
        searchHistory.add(0, q)

        if (searchHistory.size > HISTORY_SIZE){
            searchHistory = searchHistory.subList(0, HISTORY_SIZE)
        }

        val sharedPreferences =activity?.getSharedPreferences(getString(R.string.history),
            Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.putStringSet(getString(R.string.history), HashSet(searchHistory))
            ?.apply()
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

    private fun searchInHistory(v: TextView){
        callApiSearch(v.text.toString())
    }


    private fun callApiSearch(line: String){
        api.find(line = line)
            .enqueue(object : Callback<List<Film>> {
                override fun onResponse(
                    call: Call<List<Film>>,
                    response: Response<List<Film>>
                ) {
                    historyLinearLayout.visibility = View.GONE
                    buttonUpdate.visibility = View.GONE
                    if (response.isSuccessful) {
                        addInHistory(line)
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

    private fun searchRequest(){
        callApiSearch(search.query.toString())
    }

    private fun searchDebounce() {
        mainThreadHandler?.removeCallbacks(searchRunnable)
        mainThreadHandler?.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }



    fun update(view: View) {
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


