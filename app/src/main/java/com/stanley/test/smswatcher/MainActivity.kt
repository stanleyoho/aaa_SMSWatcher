package com.stanley.test.smswatcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_key.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var textWatcherPreference : KeyWordPreference
    private lateinit var keyWordInput : EditText
    private lateinit var btnKeyWordEnter : Button
    private lateinit var recyclerDisplay : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        keyWordInput = editText
        btnKeyWordEnter = button
        recyclerDisplay = recycler
        textWatcherPreference = KeyWordPreference(this@MainActivity)
    }

    override fun onResume() {
        super.onResume()
        val gridLayoutManager = GridLayoutManager(this@MainActivity,2)
        recyclerDisplay.layoutManager = gridLayoutManager
        recyclerDisplay.adapter = KeyWordAdapter(this@MainActivity)
        btnKeyWordEnter.setOnClickListener {
            val input = keyWordInput.text.toString()
            when{
                input.isEmpty()->{
                    Toast.makeText(this@MainActivity,"do not empty",Toast.LENGTH_SHORT).show()
                }
                textWatcherPreference.getKeyWords().toTypedArray().contains(input)->{
                    Toast.makeText(this@MainActivity,"already exist",Toast.LENGTH_SHORT).show()
                }
                else->{
                    textWatcherPreference.addKeyWord(input)
                    updateDisplay()
                    keyWordInput.setText("")
                }
            }
        }
    }

    fun updateDisplay(){
        recyclerDisplay.adapter?.let {
            (it as KeyWordAdapter).updateKeyWords()
        }
    }

    class KeyWordAdapter(private val context: Context):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        private val keyPreference = KeyWordPreference(context)
        private var keyArray = KeyWordPreference(context).getKeyWords().toTypedArray()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return KeyVH(LayoutInflater.from(context).inflate(R.layout.item_key,parent,false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(holder is KeyVH){
                holder.key.text = keyArray[position]
                holder.keyDelete.setOnClickListener {
                    keyPreference.deleteKeyWord(keyArray[position])
                    updateKeyWords()
                }
            }
        }

        override fun getItemCount(): Int {
            return keyArray.size
        }

        fun updateKeyWords(){
            keyArray = KeyWordPreference(context).getKeyWords().toTypedArray()
            notifyDataSetChanged()
        }

        class KeyVH(itemView: View):RecyclerView.ViewHolder(itemView){
            var key:TextView = itemView.textView2
            var keyDelete: ImageView = itemView.imageView
        }
    }
}