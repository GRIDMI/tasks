package ru.test.resulttestactivity.fragments.one

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.test.resulttestactivity.MainActivity
import ru.test.resulttestactivity.tasks.Exchanger
import java.util.UUID

class OneModel: ViewModel() {

    lateinit var userdata: MainActivity.UserData

    val nameData = MutableLiveData("")

    fun onRequestNameOld() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                userdata.name.request(MainActivity.NameRequest("1")).get()
            }.onFailure {
                nameData.postValue("ERROR: $it")
            }.onSuccess {
                nameData.postValue(it)
            }
        }
    }

    var work: Exchanger.Work<MainActivity.NameRequest, MainActivity.NameResponse>? = null

    fun onRequestName() {

        nameData.postValue("New requestName -> ${UUID.randomUUID()}")

        work = userdata.name.work(MainActivity.NameRequest("requestNameId"))
        work?.execute(object : Exchanger.Work.Result<MainActivity.NameResponse> {
            override fun onSuccess(ret: MainActivity.NameResponse) = onFinal(ret.get())
            override fun onFailed(err: Throwable) = onFinal(err.toString())
            private fun onFinal(str: String) = nameData.postValue(str)
        })

    }

}