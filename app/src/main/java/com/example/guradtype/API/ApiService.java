package com.example.guradtype.API;

import com.example.guradtype.API.Requests.CheckSentenceRequest;
import com.example.guradtype.API.Requests.FirstActivationRequest;
import com.example.guradtype.API.Requests.KeyboardChangeRequest;
import com.example.guradtype.API.Requests.LoginRequest;
import com.example.guradtype.API.Requests.RecognizeWordEntryRequest;
import com.example.guradtype.API.Requests.SignUpRequest;
import com.example.guradtype.API.Responses.CheckSentenceResponse;
import com.example.guradtype.API.Responses.FirstActivationResponse;
import com.example.guradtype.API.Responses.FrequentWord;
import com.example.guradtype.API.Responses.KeyboardChange;
import com.example.guradtype.API.Responses.KeyboardChangeResponse;
import com.example.guradtype.API.Responses.LoginResponse;
import com.example.guradtype.API.Responses.OffensiveHour;
import com.example.guradtype.API.Responses.RecognizeWordEntryResponse;
import com.example.guradtype.API.Responses.SignUpResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @POST("/user/signup")
    Call<SignUpResponse> signup(@Body SignUpRequest signUpRequest);
    @POST("/activation/first_activation")
    Call<FirstActivationResponse> firstActivation(@Body FirstActivationRequest request);
    @POST("/keyboard/change")
    Call<KeyboardChangeResponse> keyboardChange(@Body KeyboardChangeRequest request);

    @POST("/recognize_word/entry")
    Call<RecognizeWordEntryResponse> createRecognizeWordEntry(@Body RecognizeWordEntryRequest request);

    @POST("/recognize_word/check")
    Call<CheckSentenceResponse> checkSentence(@Body CheckSentenceRequest request);
    @GET("statics/frequent-words/{username}")
    Call<List<FrequentWord>> getFrequentWords(@Path("username") String username);
    @GET("statics/keyboard-changes/{username}")
    Call<List<KeyboardChange>> getKeyboardChanges(@Path("username") String username);
    @GET("statics/offensive-content-hours/{username}")
    Call<List<OffensiveHour>> getOffensiveHours(@Path("username") String username);

}
