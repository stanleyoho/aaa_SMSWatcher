package com.stanley.test.smswatcher

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.stanley.test.smswatcher.smsObserverLib.SmsObserver
import com.stanley.test.smswatcher.smsObserverLib.SmsResponseCallback
import com.stanley.test.smswatcher.smsObserverLib.VerificationCodeSmsFilter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_key.view.*

class MainActivity : AppCompatActivity() , SmsResponseCallback {

    private lateinit var textWatcherPreference : KeyWordPreference
    private lateinit var keyWordInput : EditText
    private lateinit var btnKeyWordEnter : Button
    private lateinit var recyclerDisplay : RecyclerView
    private var smsObserver: SmsObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        keyWordInput = editText
        btnKeyWordEnter = button
        recyclerDisplay = recycler
        textWatcherPreference = KeyWordPreference(this@MainActivity)

        smsObserver = SmsObserver(this, this, VerificationCodeSmsFilter("180"))
        smsObserver?.registerSMSObserver()
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.READ_SMS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) { /* ... */ }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) { /* ... */ }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) { /* ... */ }
            }).check()
    }

    override fun onResume() {
        super.onResume()
        val gridLayoutManager = GridLayoutManager(this@MainActivity,2)
        recyclerDisplay.layoutManager = gridLayoutManager
        recyclerDisplay.adapter = KeyWordAdapter(this@MainActivity,textWatcherPreference.getKeyWords().toTypedArray())
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

    private fun updateDisplay(){
        recyclerDisplay.adapter?.let {
            (it as KeyWordAdapter).updateKeyWords(textWatcherPreference.getKeyWords().toTypedArray())
        }
    }

    class KeyWordAdapter(private val context: Context,private var keyArray : Array<String>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        private val keyPreference = KeyWordPreference(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return KeyVH(LayoutInflater.from(context).inflate(R.layout.item_key,parent,false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(holder is KeyVH){
                holder.key.text = keyArray[position]
                holder.keyDelete.setOnClickListener {
                    keyPreference.deleteKeyWord(keyArray[position])
                    updateKeyWords(keyPreference.getKeyWords().toTypedArray())
                }
            }
        }

        override fun getItemCount(): Int {
            return this.keyArray.size
        }

        fun updateKeyWords(keyArray : Array<String>){
            this.keyArray = keyArray
            notifyDataSetChanged()
        }

        class KeyVH(itemView: View):RecyclerView.ViewHolder(itemView){
            var key:TextView = itemView.textView2
            var keyDelete: ImageView = itemView.imageView
        }
    }

    override fun onCallbackSmsContent(smsContent: String?) {
        Log.d("1234",smsContent?:"")
    }
}