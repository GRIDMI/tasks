package ru.test.resulttestactivity.fragments.two

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.test.resulttestactivity.MainActivity
import ru.test.resulttestactivity.R

class TwoFragment: Fragment() {

    private val model: TwoModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.userdata = (requireActivity() as MainActivity).userData
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.two_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.doneView).setOnClickListener {

            val nameView = view.findViewById<EditText>(R.id.nameView)
            model.onTypedName(nameView.text.toString())

            requireActivity().supportFragmentManager.popBackStack()

        }

    }

}