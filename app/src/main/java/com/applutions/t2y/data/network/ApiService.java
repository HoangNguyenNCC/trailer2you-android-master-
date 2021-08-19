package com.applutions.t2y.data.network;

import com.applutions.t2y.data.items.PostRatingBody;
import com.applutions.t2y.data.items.RentalEditReq;
import com.applutions.t2y.data.items.ReqCharges;
import com.applutions.t2y.data.items.TrailerViewReq;
import com.applutions.t2y.data.response.CancelRequestBody;
import com.applutions.t2y.data.response.ChangePasswordBody;
import com.applutions.t2y.data.response.LicenseeResponse;
import com.applutions.t2y.data.response.PostRatingsResponse;
import com.applutions.t2y.data.response.ProfileResponse;
import com.applutions.t2y.data.response.RatingsResponse;
import com.applutions.t2y.data.response.RescheduleRequestBody;
import com.applutions.t2y.data.response.booking.Booking;
import com.applutions.t2y.data.response.booking.BookingEditResponse;
import com.applutions.t2y.data.response.booking.BookingResponse;
import com.applutions.t2y.data.response.filter.FilterResponse;
import com.applutions.t2y.data.response.login.CommonResponse;
import com.applutions.t2y.data.response.login.SendEmailVeifyLinkBody;
import com.applutions.t2y.data.response.login.forgotPassword.ResetPasswordBody;
import com.applutions.t2y.data.response.login.forgotPassword.ResetPasswordLinkBody;
import com.applutions.t2y.data.response.login.forgotPassword.PasswordResetLinkResponse;
import com.applutions.t2y.data.response.login.forgotPassword.ResetPasswordResponse;
import com.applutions.t2y.data.response.login.otp.ResendOtpBody;
import com.applutions.t2y.data.response.login.otp.VerifyMobileBody;
import com.applutions.t2y.data.response.login.otp.VerifyMobileResponse;
import com.applutions.t2y.data.response.search.SearchResponse;
import com.applutions.t2y.data.response.search.SearchTrailerBody;
import com.applutions.t2y.data.response.search.TrailerDetails;
import com.applutions.t2y.data.response.login.signIn.LoginResponse;
import com.applutions.t2y.data.response.login.signIn.SignInObj;
import com.applutions.t2y.data.response.login.signUp.SignUpResponse;
import com.applutions.t2y.data.response.search.TrailerViewResponse;
import com.applutions.t2y.data.response.trailer.RentalItemResponse;
import com.applutions.t2y.ui.notifications.response.NotificationResponse;
import com.applutions.t2y.ui.notifications.response.RentalDetailsResponse;
import com.applutions.t2y.ui.notifications.response.RentalObj;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/signin")
    Call<LoginResponse> login(@Body SignInObj userSignIn);

    @Multipart
    @POST("/signup")
    Call<SignUpResponse> signUp(
            @Part MultipartBody.Part photo,
            @Part MultipartBody.Part driversLicenseScan,
            @Part("reqBody") RequestBody reqBody
    );


    @PUT("/forgotpassword")
    Call<PasswordResetLinkResponse> resetPasswordLink(
            @Body ResetPasswordLinkBody forgotPassword
    );

    @PUT("/resetpassword")
    Call<ResetPasswordResponse> resetPassword(
            @Body ResetPasswordBody resetPassword
    );



    @PUT("/user/password/change")
    Call<ResetPasswordResponse> changePassword(
            @Body ChangePasswordBody resetPassword
    );

    @POST("/signup/verify")
    Call<VerifyMobileResponse> verifyMobile(
        @Body VerifyMobileBody verifyMobileBody
    );

    @POST("/search")
    Call<SearchResponse> searchTrailers(
            @Body SearchTrailerBody searchTrailerBody
    );

    @POST("/signup/otp/resend")
    Call<VerifyMobileResponse> resendOtp (
            @Body ResendOtpBody resendOtpBody
    );

    @GET("/trailer")
    Call<RentalItemResponse> getRentalItem(
            @Query("id") String rentalId
    );

    @GET("/user")
    Call<ProfileResponse> getProfile();


    @Multipart
    @PUT("/user")
    Call<ProfileResponse> UpdateProfile(
            @Part MultipartBody.Part photo,
            @Part MultipartBody.Part driversLicenseScan,
            @Part("reqBody") RequestBody reqBody
    );
    @Multipart
    @PUT("/user")
    Call<ProfileResponse> UpdateProfileWithoutLicense(
            @Part MultipartBody.Part photo,
            @Part("reqBody") RequestBody reqBody
    );
    @POST("/customer/email/verify")
    Call<VerifyMobileResponse> sendEmailVerifyLink (
            @Body SendEmailVeifyLinkBody resendOtpBody
    );
    @GET("/user/reminders")
    Call<NotificationResponse> getReminders(
            @Query("type") String type
    );

    @POST("/rental/edit")
    Call<CommonResponse> cancelRentalRequest (
            @Body CancelRequestBody requestBody
    );

    @GET("/featured")
    Call<SearchResponse> featuredTrailers();

    @GET("/rental")
    Call<RentalDetailsResponse> getRentalDetails(
            @Query("id") String id
    );

    @POST("/rental/reschedule")
    Call<CommonResponse> rescheduleRequest (
            @Body RescheduleRequestBody requestBody
    );

    @GET("/trailer/licensee")
    Call<LicenseeResponse> getTrailerLicensee(
            @Query("id") String id
    );

    @GET("/rentalitemfilters")
    Call<FilterResponse> getFilters();

    @POST("/booking")
    Call<BookingResponse> requestBooking(
            @Body() Booking booking
    );

    @POST("/rental/edit")
    Call<BookingEditResponse> editBooking(
            @Body() RentalEditReq rentalEditReq
    );

    @POST("/trailer/view")
    Call<TrailerViewResponse> getTrailerView(
            @Body() TrailerViewReq trailerViewReq
    );

    @POST("/booking/charges")
    Call<RentalObj.Charges> getCharges(
            @Body() ReqCharges reqCharges
    );

    @GET("/getPendingRatingRentals")
    Call<ArrayList<RatingsResponse.Obj>> getPendingRatings();

    @POST("/setRatings")
    Call<PostRatingsResponse> postRatings(@Body() PostRatingBody body);

}
