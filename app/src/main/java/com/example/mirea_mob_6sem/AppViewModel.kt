package com.example.mirea_mob_6sem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mirea_mob_6sem.api.Film
import com.example.mirea_mob_6sem.find.FilmAdapter


class AppViewModel: ViewModel()  {
    private val findList = MutableLiveData<List<Film>>()
    private val recommendList = MutableLiveData<List<Film>>()

    private val statusRecommendService = MutableLiveData(false)

    fun saveFindList(list : List<Film>){
        findList.value = list
    }
    fun getFindList() : List<Film>? {
        return findList.value
    }

    fun saveRecommendList(list : List<Film>){
        recommendList.value = list
    }
    fun getRecommendList() : List<Film>? {
        return recommendList.value
    }

    fun saveStatusRecommendService(flag : Boolean){
        statusRecommendService.value = flag
    }
    fun getStatusRecommendService() : Boolean? {
        return statusRecommendService.value
    }

    fun getObserveRecommendList() : LiveData<List<Film>> {
        return recommendList
    }

}