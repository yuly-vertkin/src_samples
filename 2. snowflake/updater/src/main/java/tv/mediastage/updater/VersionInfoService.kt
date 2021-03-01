package tv.mediastage.updater

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import com.google.gson.annotations.SerializedName
import io.reactivex.Single

class VersionInfo {
    @SerializedName("versionNumber")
    val versionNumber: String? = null

    @SerializedName("versionUrl")
    val versionUrl: String? = null
}

interface VersionInfoService {
    @GET("sumUpd_cfg.php?versionCheck")
    fun getApp(@Query("mac") mac: String, @Query("ip") ip: String, @Query("v") version: String): Single<VersionInfo>

    @GET
    fun downloadApp(@Url url: String): Single<ResponseBody>
}
