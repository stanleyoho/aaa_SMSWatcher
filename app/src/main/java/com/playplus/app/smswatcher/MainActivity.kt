package com.playplus.app.smswatcher

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.playplus.app.smswatcher.smsObserverLib.SmsObserver
import com.playplus.app.smswatcher.smsObserverLib.SmsResponseCallback
import com.playplus.app.smswatcher.smsObserverLib.VerificationCodeSmsFilter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_key.view.*

class MainActivity : AppCompatActivity() , SmsResponseCallback {

    private lateinit var textWatcherPreference : KeyWordPreference
    private lateinit var layoutParent : ConstraintLayout
    private lateinit var editDevice : EditText
    private lateinit var editPhoneNumber : EditText
    private lateinit var listPermissions : RecyclerView
    private lateinit var btnRegister : Button
    private var smsObserver: SmsObserver? = null
    private val permissions = arrayListOf<String>(
        PermissionConstants.SMS,
        PermissionConstants.PHONE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutParent = layout_parent
        textWatcherPreference = KeyWordPreference(this@MainActivity)
        editDevice = edit_device_name
        editPhoneNumber = edit_phone_number
        listPermissions = list_permission
        btnRegister = btn_register

        smsObserver = SmsObserver(this, this, VerificationCodeSmsFilter("180"))
        smsObserver?.registerSMSObserver()
    }

    override fun onResume() {
        super.onResume()
        listPermissions.layoutManager = LinearLayoutManager(this@MainActivity)
        askPermission()
    }

    private fun askPermission() {
        var isPermissionPass = true
        val permissionData = ArrayList<PermissionModel>()

        for (item in permissions) {
            val isPass = PermissionUtils.isGranted(item)
            permissionData.add(PermissionModel(item,isPass))
            if (!isPass) {
                isPermissionPass = false
            }
        }
        updateRecycler(permissionData)

        if(!isPermissionPass){
            PermissionUtils.permission(PermissionConstants.SMS, PermissionConstants.PHONE)
                .callback(object : PermissionUtils.FullCallback{
                    override fun onGranted(granted: MutableList<String>) {
                        if(granted.size <2){
                            askPermission()
                        }
                        (listPermissions.adapter as KeyWordAdapter).updateGrantPermission(granted)
                    }

                    override fun onDenied(
                        deniedForever: MutableList<String>,
                        denied: MutableList<String>
                    ) {
                        if(denied.size>0){
                            askPermission()
                        }
                        (listPermissions.adapter as KeyWordAdapter).updateDeniedPermission(denied)
                    }
                })
                .request()
        }
    }

    fun updateRecycler(data:ArrayList<PermissionModel>){
        val adapter = listPermissions.adapter as KeyWordAdapter?
        if(adapter == null){
            listPermissions.adapter = KeyWordAdapter(this@MainActivity,data)
        }else{
            adapter.updateKeyWords(data)
        }
    }

    data class PermissionModel(
        val permissionName : String,
        var isPass : Boolean
    )

    class KeyWordAdapter(private val context: Context,private var permissionList : ArrayList<PermissionModel>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return KeyVH(LayoutInflater.from(context).inflate(R.layout.item_key,parent,false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(holder is KeyVH){
                val item = permissionList[position]
                if(item.isPass){
                    holder.textIcon.text = "V"
                    holder.textIcon.setTextColor(context.resources.getColor(android.R.color.holo_green_light))
                }else{
                    holder.textIcon.text = "X"
                    holder.textIcon.setTextColor(context.resources.getColor(android.R.color.holo_red_light))
                }

                when(item.permissionName){
                    PermissionConstants.PHONE ->{
                        holder.textPermission.text = "取得手機權限"
                    }
                    PermissionConstants.SMS ->{
                        holder.textPermission.text = "取得簡訊權限"
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return this.permissionList.size
        }

        fun updateKeyWords(permissions : ArrayList<PermissionModel>){
            this.permissionList = permissions
            notifyDataSetChanged()
        }

        fun updateGrantPermission(grantList:MutableList<String>){
            if(grantList.size == 0) return
            for(item in permissionList){
                for(item2 in grantList){
                    if(item2.contains("SMS")&&item.permissionName.contains("SMS")){
                        item.isPass = true
                    }else if(item2.contains("PHONE")&&item.permissionName.contains("PHONE")){
                        item.isPass = true
                    }
                }
            }
            notifyDataSetChanged()
        }

        fun updateDeniedPermission(deniedList:MutableList<String>){
            if(deniedList.size == 0) return
            for(item in permissionList){
                for(item2 in deniedList){
                    if(item2.contains("SMS")&&item.permissionName.contains("SMS")){
                        item.isPass = false
                    }else if(item2.contains("PHONE")&&item.permissionName.contains("PHONE")){
                        item.isPass = false
                    }
                }
            }
            notifyDataSetChanged()
        }

        class KeyVH(itemView: View):RecyclerView.ViewHolder(itemView){
            var textIcon:TextView = itemView.textIcon
            var textPermission: TextView = itemView.textPermission
        }
    }

    override fun onCallbackSmsContent(smsContent: String?) {
        Log.d("1234",smsContent?:"")
    }
}