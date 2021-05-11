package com.brainwellnessspa.Utility;

import com.brainwellnessspa.DashboardTwoModule.Model.AddProfileModel;

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
    void getAddProfiles(@Part("CoUserId") String CoUserId,
                          @Part("ProfileImage") TypedFile Avtar,
                          Callback<AddProfileModel> addProfileModelCallback);

}