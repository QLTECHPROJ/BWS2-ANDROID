package com.brainwellnessspa.utility

import android.app.Application

object AppUtils : Application() {
    private const val MAIN_URL = "http://brainwellnessapp.com.au"
  /*  private const val STAGING_MAIN_URL = "http://brainwellnessapp.com.au/bwsapi/api/staging/v1/"
    const val Live_MAIN_URL = "http://brainwellnessapp.com.au/bwsapi/api/live/v1/"
    const val BASE_URL = STAGING_MAIN_URL*/
    private const val STAGING_MAIN_URL = "http://brainwellnessapp.com.au/bwsapi/api/staging/v2/"
    private const val LIVE_MAIN_URL = "http://brainwellnessapp.com.au/bwsapi/api/live/v2/"
    const val New_BASE_URL = STAGING_MAIN_URL
    const val DEVELOPER_KEY = "AIzaSyD43ZM6bESb_pdSPzgcuCzKy8yD_45mlT8"
    const val YOUTUBE_VIDEO_CODE = "y1rfRW6WX08"
    var tncs_url = "$MAIN_URL/terms-conditions/"
    var privacy_policy_url = "$MAIN_URL/privacy-policy/"
    var how_refer_works_url = "$MAIN_URL/how-refer-works/"
}