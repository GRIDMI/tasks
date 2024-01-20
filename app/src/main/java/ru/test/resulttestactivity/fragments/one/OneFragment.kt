package ru.test.resulttestactivity.fragments.one

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.test.resulttestactivity.MainActivity
import ru.test.resulttestactivity.R

class OneFragment: Fragment() {

    private val model: OneModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.userdata = (requireActivity() as MainActivity).userData
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.one_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.nameView).run {
            model.nameData.observe(viewLifecycleOwner) {
                text = it
            }
            setOnClickListener {
                model.onRequestName()
            }
        }

        view.findViewById<Button>(R.id.cancelTaskView).run {
            setOnClickListener {
                model.work?.cancel()
            }
        }

    }

}