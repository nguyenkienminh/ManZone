package com.example.man_zone.Interfaces;

import com.example.man_zone.Model.ConversationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ConversationService {
    String CONVERSATIONS = "conversations";

    @GET(CONVERSATIONS + "/user/{userId}")
    Call<ConversationResponse> getConversationsByUserId(@Path("userId") int userId);
}
