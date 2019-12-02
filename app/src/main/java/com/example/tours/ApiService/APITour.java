package com.example.tours.ApiService;

import com.example.tours.Model.Auth;
import com.example.tours.Model.AuthRegister;
import com.example.tours.Model.CloneTour;
import com.example.tours.Model.CreateTour;
import com.example.tours.Model.GetStatusTours;
import com.example.tours.Model.ListTour;
import com.example.tours.Model.ListTourInvitation;
import com.example.tours.Model.ListUserSearch;
import com.example.tours.Model.MessageResponse;
import com.example.tours.Model.StopPoint;
import com.example.tours.Model.TourInfo;
import com.example.tours.Model.UpdateStopPointsOfTour;
import com.example.tours.Model.UpdateUserInfo;
import com.example.tours.Model.UpdateUserTour;
import com.example.tours.Model.UserInfo;
import com.example.tours.Model.UserListTour;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APITour {


    //dinh nghia endpoint va cac field o day

    @POST("/user/login")
    @FormUrlEncoded
    Call<Auth> normalLogin(@Field("emailPhone") String emailPhone,
                           @Field("password") String password);

    @POST("/user/login/by-facebook")
    @FormUrlEncoded
    Call<Auth> facebookLogin(@Field("accessToken") String accessToken);

    @POST("/user/login/by-google")
    @FormUrlEncoded
    Call<Auth> googleLogin(@Field("accessToken") String accessToken);


    @POST("/user/register")
    @FormUrlEncoded
    Call<AuthRegister> Register(@Field("password") String password,
                                @Field("fullName") String fullName,
                                @Field("email") String email,
                                @Field("phone") String phone,
                                @Field("address") String address,
                                @Field("dob") String dob,
                                @Field("gender") Number gender);

    @GET("/tour/list")
    Call<ListTour> listTour(@Header("Authorization") String token,
                             @Query("rowPerPage") Integer rowPerPage,
                             @Query("pageNum") Integer pageNum,
                             @Query("orderBy") String orderBY,
                             @Query("isDescoptional") Boolean isDescoptional);

    @POST("/tour/create")
    @FormUrlEncoded
    Call<CreateTour> createTour(@Header("Authorization") String token,
                                @Field("name") String tourName,
                                @Field("startDate") Number startDate,
                                @Field("endDate") Number endDate,
                                @Field("isPrivate") Boolean isPrivate,
                                @Field("adults") Number adults,
                                @Field("childs") Number childs,
                                @Field("minCost") Number minCost,
                                @Field("maxCost") Number maxCost,
                                @Field("avatar") String avatar);


    @POST("/tour/set-stop-points")
    Call<MessageResponse> addStopPointToTour(@Header("Authorization") String token,
                                             @Body UpdateStopPointsOfTour updateStopPointsOfTour);

    @GET("/tour/info")
    Call<TourInfo> getTourInfo(@Header("Authorization") String token,
                               @Query("tourId") Integer tourId);

    @GET("/user/search")
    Call<ListUserSearch> searchUser(@Header("Authorization") String token,
                                    @Query("searchKey") String searchKey,
                                    @Query("pageIndex") Integer pageIndex,
                                    @Query("pageSize") Integer pageSize);

    @POST("/tour/add/member")
    @FormUrlEncoded
    Call<MessageResponse> inviteMember(@Header("Authorization") String token,
                                       @Field("tourId") String tourId,
                                       @Field("invitedUserId") String invitedUserId,
                                       @Field("isInvited") Boolean isInvited);

    @GET("/tour/history-user")
    Call<UserListTour> userListTour(@Header("Authorization") String token,
                                    @Query("pageIndex") Integer pageIndex,
                                    @Query("pageSize") Integer pageSize);

    @POST("/tour/update-tour")
    @FormUrlEncoded
    Call<UpdateUserTour> updateUserTour(@Header("Authorization") String token,
                                        @Field("id") String id,
                                        @Field("name") String tourName,
                                        @Field("startDate") Number startDate,
                                        @Field("endDate") Number endDate,
                                        @Field("isPrivate") Boolean isPrivate,
                                        @Field("adults") Number adults,
                                        @Field("childs") Number childs,
                                        @Field("minCost") Number minCost,
                                        @Field("maxCost") Number maxCost,
                                        @Field("status") Number status,
                                        @Field("avatar") String avatar);

    @POST("/tour/update-stop-point")
    @FormUrlEncoded
    Call<StopPoint> updateStopPointInfo(@Header("Authorization") String token,
                                        @Field("id") String id,
                                        @Field("name") String tourName,
                                        @Field("arrivalAt") Long arrivalAt,
                                        @Field("leaveAt") Long leaveAt,
                                        @Field("serviceTypeId") Integer serviceTypeId,
                                        @Field("minCost") Number minCost,
                                        @Field("maxCost") Number maxCost,
                                        @Field("avatar") String avatar,
                                        @Field("index") Integer index);

    @POST("/tour/add/review")
    Call<MessageResponse> addReview(@Header("Authorization") String token,
                                    @Field("tourId") Integer tourId,
                                    @Field("point") Integer point,
                                    @Field("review") Integer review);

    @POST("/tour/clone")
    @FormUrlEncoded
    Call<CloneTour> cloneTour(@Header("Authorization") String token,
                                        @Field("tourId") Number id);

    @GET("/tour/history-user-by-status")
    Call<GetStatusTours> getStatusTours(@Header("Authorization") String token);

    @GET("/user/info")
    Call<UserInfo> getUserInfo(@Header("Authorization") String token);

    @POST("/user/update-avatar")
    @FormUrlEncoded
    Call<MessageResponse> updateAvatar(@Header("Authorization") String token,
                              @Field("file") String imageBase64);

    @GET("/tour/get/invitation")
    Call<ListTourInvitation> userInvitation(@Header("Authorization") String token,
                                            @Query("pageIndex") Integer pageIndex,
                                            @Query("pageSize") Integer pageSize);

    @POST("/tour/response/invitation")
    @FormUrlEncoded
    Call<MessageResponse> responseInvitation(@Header("Authorization") String token,
                                       @Field("tourId") Integer id,
                                       @Field("isAccepted") Boolean isAccepted);

    @POST("/user/edit-info")
    @FormUrlEncoded
    Call<UpdateUserInfo> updateUserInfo(@Header("Authorization") String token,
                                        @Field("fullName") String name,
                                        @Field("email") String email,
                                        @Field("phone") String phone,
                                        @Field("gender") Number gender,
                                        @Field("dob") Date dob);

    @POST("/user/update-password")
    @FormUrlEncoded
    Call<UpdateUserInfo> updatePassword(@Header("Authorization") String token,
                                        @Field("userId") Number id,
                                        @Field("currentPassword") String currentPassword,
                                        @Field("newPassword") String newPassword);
}