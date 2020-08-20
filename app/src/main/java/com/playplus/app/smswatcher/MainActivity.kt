package com.playplus.app.smswatcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.playplus.app.smswatcher.smsObserverLib.SmsObserver
import com.playplus.app.smswatcher.smsObserverLib.SmsResponseCallback
import com.playplus.app.smswatcher.smsObserverLib.VerificationCodeSmsFilter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_key.view.*

class MainActivity : AppCompatActivity()  {

    private lateinit var textWatcherPreference : KeyWordPreference
    private lateinit var layoutParent : ConstraintLayout
    private lateinit var editDevice : EditText
    private lateinit var editPhoneNumber : EditText
    private lateinit var listPermissions : RecyclerView
    private lateinit var btnRegister : Button
    private var smsObserver: SmsObserver? = null
    private val permissionRequestCode = 1001
    private var isPermissionCheckPass = false
    val permissionData = MyPermissionUtil.getPermissionArray(
        android.Manifest.permission.RECEIVE_SMS,
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.READ_PHONE_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutParent = layout_parent
        textWatcherPreference = KeyWordPreference(this@MainActivity)
        editDevice = edit_device_name
        editPhoneNumber = edit_phone_number
        listPermissions = list_permission
        btnRegister = btn_register

//        smsObserver = SmsObserver(this, this, VerificationCodeSmsFilter("180"))
//        smsObserver?.registerSMSObserver()
        askPermission()
        val intent = Intent(this@MainActivity,MySMSService::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startService(intent)
    }

    override fun onResume() {
        super.onResume()
        listPermissions.layoutManager = LinearLayoutManager(this@MainActivity)
        updateRecycler()
        if(isAllPermissionsPass()){
            showRegister(true)
        }else{
            showRegister(false)
        }
    }

    private fun askPermission() {

        val deniedArray = MyPermissionUtil.getPermissionDeniedArray(this@MainActivity,permissionData)

        if(deniedArray.isNotEmpty()){
            //get permissions
            MyPermissionUtil.askPermission(this@MainActivity,deniedArray,permissionRequestCode)
            Log.d("askPermission","ask")
        }else{
            nextAction()
        }
    }

    fun isAllPermissionsPass():Boolean{
        var isAllPass = true
        for(item in permissionData){
            if(checkSelfPermission(item) == PackageManager.PERMISSION_DENIED){
                isAllPass = false
            }
        }

        return isAllPass
    }

    fun nextAction(){
        showRegister(true)
    }

    fun showRegister(isShow:Boolean){
        if (isShow){
            btnRegister.visibility = View.VISIBLE
        }else{
            btnRegister.visibility = View.GONE
        }

    }

    private fun updateRecycler(){
        val adapter = listPermissions.adapter as KeyWordAdapter?
        if(adapter == null){
            listPermissions.adapter = KeyWordAdapter(this@MainActivity,callback)
        }else{
            adapter.updateGrantPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == permissionRequestCode){
            var isNeedShowAgain = false
            for(element in permissions){
                if(shouldShowRequestPermissionRationale(element)) {
                    isNeedShowAgain = true
                }
            }

            //check is permission pass
            if(isNeedShowAgain){
                askPermission()
            }

            updateRecycler()
        }
    }

    private val callback = object : KeyWordAdapter.Callback{
        override fun onCheckPass() {
            isPermissionCheckPass = true
            showRegister(true)
            nextAction()
        }

        override fun onCheckFail() {
            isPermissionCheckPass = false
            showRegister(false)
        }
    }

    class KeyWordAdapter(private val context: Context,val callback : Callback):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        private val permissionsArray = arrayOf("SMS","PHONE")
        var phoneCheck = false
        var smsCheck = false

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return PermissionVH(context,LayoutInflater.from(context).inflate(R.layout.item_key,parent,false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(holder is PermissionVH){
                val permissionResult = checkPermission(permissionsArray[position])

                if(permissionResult){
                    holder.setPassStyle()
                }else{
                    holder.setFailStyle()
                }
                when(position){
                    0->{ holder.textPermission.text = "取得簡訊權限" }
                    1->{ holder.textPermission.text = "取得手機號碼權限" }
                }
            }
        }

        override fun getItemCount(): Int {
            return this.permissionsArray.size
        }

        fun updateGrantPermission(){
            notifyDataSetChanged()
        }

        private fun checkPermission(type:String):Boolean{
            var result = false
            when(type){
                "SMS"->{
                    val permissionResult = context.checkSelfPermission(android.Manifest.permission.READ_SMS)
                    result = permissionResult == PackageManager.PERMISSION_GRANTED
                    smsCheck = permissionResult == PackageManager.PERMISSION_GRANTED
                }
                "PHONE"->{
                    val permissionResult = context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    result = permissionResult == PackageManager.PERMISSION_GRANTED
                    phoneCheck = permissionResult == PackageManager.PERMISSION_GRANTED
                }
            }
            if(smsCheck && phoneCheck) {
                callback.onCheckPass()
            }else{
                callback.onCheckFail()
            }

            return result
        }

        interface Callback{
            fun onCheckPass()
            fun onCheckFail()
        }

        class PermissionVH(private val context:Context, itemView: View):RecyclerView.ViewHolder(itemView){
            var textIcon:TextView = itemView.textIcon
            var textPermission: TextView = itemView.textPermission
            var btnSetting: Button = itemView.btn_setting

            fun setPassStyle(){
                textIcon.text = "V"
                textIcon.setTextColor(context.resources.getColor(android.R.color.holo_green_light))
                btnSetting.visibility = View.GONE
            }

            fun setFailStyle(){
                textIcon.text = "X"
                textIcon.setTextColor(context.resources.getColor(android.R.color.holo_red_light))
                btnSetting.visibility = View.VISIBLE
                btnSetting.setOnClickListener {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri: Uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                    Log.d("123","123")
                }
            }
        }
    }
}