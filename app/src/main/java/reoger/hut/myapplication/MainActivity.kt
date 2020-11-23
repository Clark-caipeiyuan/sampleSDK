package reoger.hut.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        bindView()

    }

    private fun bindView() {
//        TODO("Not yet implemented")


    }

    private fun initView() {
//        TODO("Not yet implemented")
        textView=this.findViewById(R.id.text_hello)
        textView.text="this is the first normal test"
    }
}