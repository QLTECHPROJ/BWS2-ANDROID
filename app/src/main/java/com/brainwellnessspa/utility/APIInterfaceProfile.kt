package com.brainwellnessspa.utility;

import com.brainwellnessspa.dashboardModule.models.AddProfileModel;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface APIInterfaceProfile {

    /*TODO UserProfileActivity */

    //  TODO Profile Image Upload
    @Multipart
    @POST("/updateprofileimg")
    void getAddProfiles(@Part("UserId") String CoUserId,
                        @Part("ProfileImage") TypedFile Avtar,
                        Callback<AddProfileModel> addProfileModelCallback);

}