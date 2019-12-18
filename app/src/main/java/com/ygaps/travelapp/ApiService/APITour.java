package com.ygaps.travelapp.ApiService;

import com.ygaps.travelapp.Model.Auth;
import com.ygaps.travelapp.Model.AuthRegister;
import com.ygaps.travelapp.Model.CloneTour;
import com.ygaps.travelapp.Model.CommentList;
import com.ygaps.travelapp.Model.CreateTour;
import com.ygaps.travelapp.Model.GetStatusTours;
import com.ygaps.travelapp.Model.ListFeedbackService;
import com.ygaps.travelapp.Model.ListReviewPoint;
import com.ygaps.travelapp.Model.ListSuggestedStopPoint;
import com.ygaps.travelapp.Model.ListTour;
import com.ygaps.travelapp.Model.ListTourInvitation;
import com.ygaps.travelapp.Model.ListUserSearch;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.NotificationList;
import com.ygaps.travelapp.Model.NotificationOnRoadList;
import com.ygaps.travelapp.Model.RequestCoordinateList;
import com.ygaps.travelapp.Model.RequestOTPPassWord;
import com.ygaps.travelapp.Model.StopPoint;
import com.ygaps.travelapp.Model.TotalTourReview;
import com.ygaps.travelapp.Model.TourInfo;
import com.ygaps.travelapp.Model.UpdateStopPointsOfTour;
import com.ygaps.travelapp.Model.UpdateUserInfo;
import com.ygaps.travelapp.Model.UpdateUserTour;
import com.ygaps.travelapp.Model.UserInfo;
import com.ygaps.travelapp.Model.UserListTour;

import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Internal;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @POST("/tour/suggested-destination-list")
    Call<ListSuggestedStopPoint> getSuggesteDestination(@Header("Authorization") String token,
                                                        @Body RequestCoordinateList requestCoordList);
    @GET("/tour/search/service")
    Call<ListSuggestedStopPoint> searchService(@Header("Authorization") String token,
                                        @Query("searchKey") String searchKey,
                                        @Query("pageSize") Integer pageSize,
                                        @Query("pageIndex") Integer pageIndex);

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
    @FormUrlEncoded
    Call<MessageResponse> addReview(@Header("Authorization") String token,
                                    @Field("tourId") Integer tourId,
                                    @Field("point") Integer point,
                                    @Field("review") String review);

    @GET("/tour/get/review-point-stats")
    Call<ListReviewPoint> reviewPoint(@Header("Authorization") String token,
                                      @Query("tourId") Integer tourId);


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

    @POST("/user/update-avatar")
    @Multipart
    Call<MessageResponse> updateAvatar2(@Header("Authorization") String token,
                                        @Part MultipartBody.Part file);

    @GET("/tour/get/invitation")
    Call<ListTourInvitation> userInvitation(@Header("Authorization") String token,
                                            @Query("pageIndex") Integer pageIndex,
                                            @Query("pageSize") Integer pageSize);

    @POST("/tour/response/invitation")
    @FormUrlEncoded
    Call<MessageResponse> responseInvitation(@Header("Authorization") String token,
                                       @Field("tourId") Integer id,
                                       @Field("isAccepted") Boolean isAccepted);

    @POST("/tour/comment")
    @FormUrlEncoded
    Call<MessageResponse> sendComment(@Header("Authorization") String Token,
                                      @Field("tourId") Integer tourId,
                                      @Field("userId") Integer userId,
                                      @Field("comment") String comment);

    @GET("/tour/comment-list")
    Call<CommentList> getComment(@Header("Authorization") String token,
                                 @Query("tourId") Integer tourId,
                                 @Query("pageIndex") Integer pageIndex,
                                 @Query("pageSize") Integer pageSize);
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

    @POST("/tour/current-users-coordinate")
    @FormUrlEncoded
    Call<MessageResponse> currentCoordinate(@Header("Authorization") String token,
                                              @Field("userId") Integer userId,
                                              @Field("tourId") Integer tourId,
                                              @Field("lat") double mlat,
                                              @Field("long") double mlong);

    @POST("/user/request-otp-recovery")
    @FormUrlEncoded
    Call<RequestOTPPassWord> requestOTPPassword(@Field("type") String type,
                                                @Field("value") String value);

    @POST("/user/verify-otp-recovery")
    @FormUrlEncoded
    Call<MessageResponse> verifyPasswordRecovery(@Field("userId") Integer userId,
                                                 @Field("newPassword") String newPassword,
                                                 @Field("verifyCode") String verifyCode);


    @POST("/tour/notification")
    @FormUrlEncoded
    Call<MessageResponse> sendNotification(@Header("Authorization") String token,
                                           @Field("userId") Integer userId,
                                           @Field("tourId") Integer tourId,
                                           @Field("noti") String noti);

    @POST("/user/notification/put-token")
    @FormUrlEncoded
    Call<MessageResponse> registerFirebaseToken(@Header("Authorization") String token,
                                           @Field("fcmToken") String fcmToken,
                                           @Field("deviceId") String deviceId,
                                           @Field("platform") Integer platform,
                                           @Field("appVersion") String appVersion);

    @POST("/user/notification/remove-token")
    @FormUrlEncoded
    Call<MessageResponse> removeFirebaseToken(@Header("Authorization") String token,
                                                @Field("fcmToken") String fcmToken,
                                                @Field("deviceId") String deviceId);

    @POST("/tour/add/notification-on-road")
    @FormUrlEncoded
    Call<MessageResponse> createNotificationOnRoad(@Header("Authorization") String token,
                                                   @Field("lat") double mlat,
                                                   @Field("long") double mlong,
                                                   @Field("tourId") Integer tourId,
                                                   @Field("userId") Integer userId,
                                                   @Field("notificationType") Integer notificationType,
                                                   @Field("speed") Integer speed,
                                                   @Field("note") String note);
    @GET("/tour/notification-list")
    Call<NotificationList> getNotificationTour(@Header("Authorization") String token,
                                               @Query("tourId") Integer tourId,
                                               @Query("pageIndex") Integer pageIndex,
                                               @Query("pageSize") Integer pageSize);

    @GET("/tour/get/noti-on-road")
    Call<NotificationList> getNotificationOnRoadByTour(@Header("Authorization") String token,
                                               @Query("tourId") Integer tourId,
                                               @Query("pageIndex") Integer pageIndex,
                                               @Query("pageSize") Integer pageSize);

    @GET("/tour/get/noti-on-road-coordinate")
    Call<NotificationOnRoadList> getNotificationOnRoadByCoordinate(@Header("Authorization") String token,
                                                                   @Query("notificationType") Integer notificationType,
                                                                   @Query("lat") double mlat,
                                                                   @Query("long") double mlong);
    @POST("/tour/recording")
    @Multipart
    Call<MessageResponse> sendRecord(@Header("Authorization") String token,
                                     @Part MultipartBody.Part file,
                                     @Part("tourId") RequestBody tourId,
                                     @Part("fullname") RequestBody fullname,
                                     @Part("avatar") RequestBody avatar,
                                     @Part("lat") RequestBody mlat,
                                     @Part("long") RequestBody mlong);
    @POST("/tour/finish-trip")
    @FormUrlEncoded
    Call<MessageResponse> finishTour(@Header("Authorization") String token,
                                     @Field("tourId") Integer tourId);

    @POST("/tour/add/feedback-service")
    @FormUrlEncoded
    Call<MessageResponse> addServiceReview(@Header("Authorization") String token,
                                    @Field("serviceId") Integer tourId,
                                    @Field("feedback") String feedback,
                                    @Field("point") Integer review);

    @GET("/tour/get/feedback-point-stats")
    Call<ListReviewPoint> serviceReviewPoint(@Header("Authorization") String token,
                                      @Query("serviceId") Integer serviceId);

    @GET("/tour/get/review-list")
    Call<TotalTourReview> getTotalTourReview(@Header("Authorization") String token,
                                             @Query("tourId") Integer tourId,
                                             @Query("pageIndex") Integer numPage,
                                             @Query("pageSize") Integer numPerPage);

    @GET("/tour/get/feedback-service")
    Call<ListFeedbackService> getListFeedbackService(@Header("Authorization") String token,
                                                     @Query("serviceId") Integer serviceId,
                                                     @Query("pageIndex") Integer numPage,
                                                     @Query("pageSize") Integer numPerPage);

    @POST("/tour/report/feedback")
    @FormUrlEncoded
    Call<MessageResponse> reportFeedbackService(@Header("Authorization") String token,
                                           @Field("feedbackId") Integer feedbackId);

    @POST("/tour/report/review")
    @FormUrlEncoded
    Call<MessageResponse> reportReviewTour(@Header("Authorization") String token,
                                                @Field("reviewId") Integer reviewId);



}