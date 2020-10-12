package com.brainwellnessspa.Utility;

import com.brainwellnessspa.UserModule.Models.AddProfileModel;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface APIInterfaceProfile {

    /*TODO UserProfileActivity */

    //  TODO Profile Image Upload
    @Multipart
    @POST("/addprofileimage")
    void getAddProfiles(@Part("UserID") String UserID,
                          @Part("ProfileImage") TypedFile Avtar,
                          Callback<AddProfileModel> addProfileModelCallback);

}