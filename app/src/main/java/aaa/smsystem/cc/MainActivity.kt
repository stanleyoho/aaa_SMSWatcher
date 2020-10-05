package aaa.smsystem.cc

import aaa.smsystem.cc.net.NetManager
import aaa.smsystem.cc.net.ResponseModels
import aaa.smsystem.cc.service.MySMSService
import aaa.smsystem.cc.utils.MyDeviceUtils
import aaa.smsystem.cc.utils.MyPermissionUtil
import aaa.smsystem.cc.utils.MyPhoneUtils
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_key.view.*

class MainActivity : AppCompatActivity(){

    private val TAG = javaClass.name
    private lateinit var textWatcherPreference : DevicePreference
    private lateinit var layoutParent : ConstraintLayout
    private lateinit var textDevice : TextView
    private lateinit var editPhoneNumber : EditText
    private lateinit var listPermissions : RecyclerView
    private lateinit var btnRegister : Button
    private var loadingDialog : LoadingDialog? = null
    private val permissionRequestCode = 1001
    private var isPermissionCheckPass = false
    private var isDeviceRegistered = false
    val permissionData = MyPermissionUtil.getPermissionArray(
        android.Manifest.permission.RECEIVE_SMS,
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.READ_PHONE_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textWatcherPreference = DevicePreference(this@MainActivity)
        loadingDialog = LoadingDialog(this@MainActivity)
        layoutParent = layout_parent
        textDevice = edit_device_name
        editPhoneNumber = edit_phone_number
        listPermissions = list_permission
        btnRegister = btn_register

        btnRegister.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (checkIsInputCorrect()) {
                    getToken()
                } else {
                    showToast("請輸入電話號碼")
                }
            }
        })

        isPermissionCheckPass = isAllPermissionsPass()
        isDeviceRegistered = checkIsDeviceRegistered()

        //init
        textDevice.text = "${MyDeviceUtils.getManufacturer()}_${MyDeviceUtils.getModel()}"
        listPermissions.layoutManager = LinearLayoutManager(this@MainActivity)

        MyPhoneUtils.logPhoneInfo(this)
    }

    override fun onResume() {
        super.onResume()
        askPermission()
        updateRecycler()
        if(isAllPermissionsPass()){
            showRegister(true)
        }else{
            showRegister(false)
        }

        if(checkIsDeviceRegistered()){
            showRegisteredView()
        }else{
            showUnRegisteredView()
        }

        if(isPermissionCheckPass && isDeviceRegistered){
            startSMSListenerService()
        }else{
            stopSMSListenerService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun checkIsInputCorrect():Boolean{
        var checkResult = false
        checkResult = when{
            textDevice.text.isEmpty()->{
                false
            }
            editPhoneNumber.text.toString().trim().isEmpty()->{
                false
            }
            else->{
                true
            }
        }
        return checkResult
    }

    fun getToken(){
        loadingDialog?.show()
        NetManager.getToken(object : ApiCallBackInterface {
            override fun onSuccess(tag: Int, responseString: String?) {
                if (responseString == null) {
                    loadingDialog?.dismiss()
                    showToast("getToken message null")
                } else {
                    val responseModel = Gson().fromJson<ResponseModels.GetTokenResponseModel>(
                        responseString,
                        ResponseModels.GetTokenResponseModel::class.java
                    )
                    if (responseModel.status) {
                        textWatcherPreference.setToken(responseModel.token)
                        registerDevice(
                            textWatcherPreference.getToken(),
                            textDevice.text.toString(),
                            MyDeviceUtils.getUUID(),
                            editPhoneNumber.text.toString()
                        )
                    } else {
                        loadingDialog?.dismiss()
                        showToast("set token fail")
                    }
                }
            }

            override fun onFail(tag: Int, errorMessage: String?) {
                loadingDialog?.dismiss()
                showToast(errorMessage ?: "getToken fail")
            }
        })
    }

    fun registerDevice(token: String, device: String, uid: String, phone: String){
        NetManager.registerDevice(token, device, uid, phone, object : ApiCallBackInterface {
            override fun onSuccess(tag: Int, responseString: String?) {
                if (responseString == null) {
                    loadingDialog?.dismiss()
                    showToast("registerDevice message null")
                } else {
                    val responseModel =
                        Gson().fromJson<ResponseModels.GetDeviceRegisterResponseModel>(
                            responseString,
                            ResponseModels.GetDeviceRegisterResponseModel::class.java
                        )
                    if (responseModel.status) {
                        responseModel.data?.let {
                            textWatcherPreference.setID(it.id)
                            textWatcherPreference.setUID(it.uid)
                            textWatcherPreference.setPhone(editPhoneNumber.text.toString())
                            isDeviceRegistered = true
                            loadingDialog?.dismiss()
                            showToast("裝置註冊成功")
                            showRegisteredView()
                            if (isPermissionCheckPass && isDeviceRegistered) {
                                startSMSListenerService()
                            } else {
                                stopSMSListenerService()
                            }
                        }
                    } else {
                        loadingDialog?.dismiss()
                        showToast("registerDevice fail")
                    }
                }
            }

            override fun onFail(tag: Int, errorMessage: String?) {
                loadingDialog?.dismiss()
                showToast(errorMessage ?: "registerDevice fail")
            }
        })
    }

    fun showToast(message: String){
        Handler(Looper.getMainLooper()).post(Runnable {
            Toast.makeText(
                this@MainActivity,
                message,
                Toast.LENGTH_LONG
            ).show()
        })
    }

    private fun askPermission() {

        val deniedArray = MyPermissionUtil.getPermissionDeniedArray(
            this@MainActivity,
            permissionData
        )

        if(deniedArray.isNotEmpty()){
            //get permissions
            MyPermissionUtil.askPermission(this@MainActivity, deniedArray, permissionRequestCode)
            Log.d("askPermission", "ask")
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

    fun showRegister(isShow: Boolean){
        if (isShow){
            btnRegister.visibility = View.VISIBLE
        }else{
            btnRegister.visibility = View.GONE
        }

    }

    private fun updateRecycler(){
        val adapter = listPermissions.adapter as KeyWordAdapter?
        if(adapter == null){
            listPermissions.adapter = KeyWordAdapter(this@MainActivity, callback)
        }else{
            adapter.updateGrantPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

    private val callback = object : KeyWordAdapter.Callback {
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

    class KeyWordAdapter(private val context: Context, val callback: Callback):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        private val permissionsArray = arrayOf("SMS", "PHONE")
        var phoneCheck = false
        var smsCheck = false

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return PermissionVH(
                context, LayoutInflater.from(context).inflate(
                    R.layout.item_key,
                    parent,
                    false
                )
            )
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
                    0 -> {
                        holder.textPermission.text = "取得簡訊權限"
                    }
                    1 -> {
                        holder.textPermission.text = "取得手機號碼權限"
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return this.permissionsArray.size
        }

        fun updateGrantPermission(){
            notifyDataSetChanged()
        }

        private fun checkPermission(type: String):Boolean{
            var result = false
            when(type){
                "SMS" -> {
                    val permissionResult =
                        context.checkSelfPermission(android.Manifest.permission.READ_SMS)
                    result = permissionResult == PackageManager.PERMISSION_GRANTED
                    smsCheck = permissionResult == PackageManager.PERMISSION_GRANTED
                }
                "PHONE" -> {
                    val permissionResult =
                        context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
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

        class PermissionVH(private val context: Context, itemView: View):RecyclerView.ViewHolder(
            itemView
        ){
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
                    Log.d("123", "123")
                }
            }
        }
    }

    private fun checkIsDeviceRegistered() : Boolean{
        return when {
            textWatcherPreference.getToken().isEmpty()->{
                false
            }
            textWatcherPreference.getPhone().isEmpty()->{
                false
            }
            textWatcherPreference.getUID().isEmpty()->{
                false
            }
            textWatcherPreference.getID() == 0 ->{
                false
            }
            else->{
                true
            }
        }
    }

    private fun showRegisteredView(){
        btnRegister.isEnabled = false
        editPhoneNumber.inputType = InputType.TYPE_NULL
        editPhoneNumber.setText(textWatcherPreference.getPhone())
        btnRegister.text = "裝置已註冊"
    }

    private fun showUnRegisteredView(){
        btnRegister.isEnabled = true
        editPhoneNumber.inputType = InputType.TYPE_CLASS_PHONE
        btnRegister.text = "裝置尚未註冊,請註冊裝置"
    }

    fun startSMSListenerService(){
        startService(Intent(this, MySMSService::class.java))
    }

    fun stopSMSListenerService(){
        stopService(Intent(this, MySMSService::class.java))
    }
}