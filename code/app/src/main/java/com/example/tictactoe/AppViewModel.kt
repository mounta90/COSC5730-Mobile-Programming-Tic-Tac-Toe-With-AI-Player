package com.example.tictactoe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {

    private val mutableWinner: MutableLiveData<String?> = MutableLiveData(null)

    val winner: LiveData<String?> get() = mutableWinner

    fun setWinner(value: String?) {
        this.mutableWinner.value = value
    }

    // ----------------------------------------------------------------

    private val mutableLoser: MutableLiveData<String?> = MutableLiveData(null)

    val loser: LiveData<String?> get() = mutableLoser

    fun setLoser(value: String?) {
        this.mutableLoser.value = value
    }

    // ----------------------------------------------------------------

    private val mutableIsGameFinished: MutableLiveData<Boolean> = MutableLiveData(false)

    val isGameFinished: LiveData<Boolean> get() = mutableIsGameFinished

    fun setIsGameFinished(value: Boolean) {
        this.mutableIsGameFinished.value = value
    }

    // ----------------------------------------------------------------

    private val mutableIsVersusAi: MutableLiveData<Boolean> = MutableLiveData(false)

    val isVersusAi: LiveData<Boolean> get() = mutableIsVersusAi

    fun setIsVersusAi(value: Boolean) {
        this.mutableIsVersusAi.value = value
    }

    // ----------------------------------------------------------------

}