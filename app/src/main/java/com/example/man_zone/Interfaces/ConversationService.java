package com.example.man_zone.Interfaces;

import com.example.man_zone.Model.ConversationResponse;
import com.example.man_zone.Model.SingleConversationResponse;
import com.example.man_zone.Model.CreateConversationRequest;
import com.example.man_zone.Model.UpdateConversationRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Header;

public interface ConversationService {
    String CONVERSATIONS = "api/conversations";

    @GET(CONVERSATIONS + "/user/{userId}")
    Call<ConversationResponse> getConversationsByUserId(
            @Header("Authorization") String authorization,
            @Path("userId") int userId);

    @POST(CONVERSATIONS)
    Call<SingleConversationResponse> createConversation(
            @Header("Authorization") String authorization,
            @Body CreateConversationRequest request);

    @PUT(CONVERSATIONS + "/{id}")
    Call<SingleConversationResponse> updateConversation(
            @Header("Authorization") String authorization,
            @Path("id") int id,
            @Body UpdateConversationRequest request);

    @POST(CONVERSATIONS + "/markdone/{conversationId}")
    Call<Void> markConversationAsDone(
            @Header("Authorization") String authorization,
            @Path("conversationId") int conversationId);

    @GET(CONVERSATIONS)
    Call<ConversationResponse> getAllConversations(
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort);
}
