package ru.test.resulttestactivity.fragments.two

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.test.resulttestactivity.MainActivity

class TwoModel: ViewModel() {

    lateinit var userdata: MainActivity.UserData

    private var onTypedJob: Job? = null

    fun onTypedName(name: String) {
        onTypedJob = viewModelScope.launch(Dispatchers.IO) {
            userdata.name.response(MainActivity.NameResponse("requestNameId", name))
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (onTypedJob == null) {
            userdata.name.response(MainActivity.NameResponse("requestNameId", Throwable("NOT SELECTED")))
        }
    }

}