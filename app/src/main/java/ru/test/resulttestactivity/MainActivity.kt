package ru.test.resulttestactivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.test.resulttestactivity.tasks.Manager
import ru.test.resulttestactivity.fragments.one.OneFragment
import ru.test.resulttestactivity.fragments.two.TwoFragment
import ru.test.resulttestactivity.tasks.Exchanger

class MainActivity : AppCompatActivity() {

    val userData = UserData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.addOnBackStackChangedListener {
            userData.notifyPendingResponses()
        }
        showFragment(OneFragment())
    }

    private fun showFragment(fragment: Fragment) {
        val tr = supportFragmentManager.beginTransaction()
        tr.replace(R.id.fragmentView, fragment)
        tr.addToBackStack(null)
        tr.commitAllowingStateLoss()
    }

    inner class UserData: Manager() {

        val name = bind(object : Exchanger.Behavior<NameRequest, NameResponse> {
            override fun onRequest(request: NameRequest) = showFragment(TwoFragment())
        })

    }

    class NameRequest(id: String): Manager.IO.Request(id)
    class NameResponse(
        id: String,
        private val name: String?,
        private val error: Throwable?,
    ): Manager.IO.Response(id) {

        constructor(id: String, name: String): this(id, name, null)
        constructor(id: String, throwable: Throwable): this(id, null, throwable)

        fun get(): String {
            name?.let {
                return it
            }
            throw error ?: Throwable("NO PAYLOAD AND ERROR")
        }

    }

}