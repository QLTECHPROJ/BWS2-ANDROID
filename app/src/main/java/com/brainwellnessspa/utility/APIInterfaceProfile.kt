package com.brainwellnessspa.utility

import com.brainwellnessspa.dashboardModule.models.AddProfileModel
import retrofit.Callback
import retrofit.http.Multipart
import retrofit.http.POST
import retrofit.http.Part
import retrofit.mime.TypedFile

interface APIInterfaceProfile {/*TODO UserProfileActivity */

    //  TODO Profile Image Upload
    @Multipart
    @POST("/updateprofileimg")
    fun getAddProfiles(@Part("UserId") CoUserId: String?, @Part("ProfileImage") avtar: TypedFile?, addProfileModelCallback: Callback<AddProfileModel>?)
}