package com.xh.mian.myapp.appui.main.person

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PersonViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "我的"
    }
    val text: LiveData<String> = _text

}