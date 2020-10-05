package aaa.smsystem.cc.net

object UrlManager {

    private const val HOST = "https://sms.skyl00.com"
    const val API_GET_TOKEN = "$HOST/api/omniauth.json"
    const val API_SEND_MESSAGE = "$HOST/api/sms_records.json"
    const val API_REGISTER_DEVICE = "$HOST/api/devices.json"
}