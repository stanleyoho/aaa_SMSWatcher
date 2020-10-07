package com.playplus.app.smswatcher.aaa.net

import com.google.gson.annotations.SerializedName

class ResponseModels {

    /**
     * path:GetToken
     */
    data class GetTokenResponseModel(
        @SerializedName("status") val status : Boolean,
        @SerializedName("data") val token : String,
        @SerializedName("message") val message : String?
    )

    /**
     * path:DeviceRegister
     */
    data class GetDeviceRegisterResponseModel(
        @SerializedName("status") val status : Boolean,
        @SerializedName("data") val data : DeviceRegisterDetail?,
        @SerializedName("message") val message : String?
    )

    /**
     * path:DeviceRegister
     */
    data class DeviceRegisterDetail(
        @SerializedName("id") val id : Int,
        @SerializedName("name") val name : String,
        @SerializedName("phone") val phone : String,
        @SerializedName("uid") val uid : String,
        @SerializedName("status") val status : String,
        @SerializedName("remark") val remark : String?,
        @SerializedName("deleted_at") val deleted_at : String?,
        @SerializedName("created_at") val created_at : String?,
        @SerializedName("updated_at") val updated_at : String?,
        @SerializedName("tag_list") val tag_list : ArrayList<String>
    )

    data class SendMessageResponseModel(
        @SerializedName("status") val status : Boolean,
        @SerializedName("data") val data : SendMessageDetail?,
        @SerializedName("message") val message : String?
    )

    data class SendMessageDetail(
        @SerializedName("id") val id : Int,
        @SerializedName("name") val name : String,
        @SerializedName("phone") val phone : String,
        @SerializedName("content") val content : String,
        @SerializedName("deleted_at") val deleted_at : String?,
        @SerializedName("created_at") val created_at : String?,
        @SerializedName("updated_at") val updated_at : String?,
        @SerializedName("tag_list") val tag_list : ArrayList<String>
    )
}