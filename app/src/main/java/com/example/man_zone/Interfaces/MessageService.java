package com.example.man_zone.Interfaces;

import com.example.man_zone.Model.MessageResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageService {
    @GET("conversations/{conversationId}/messages")
    Call<MessageResponse> getMessages(
        @Path("conversationId") int conversationId,
        @Query("page") int page,
        @Query("size") int size,
        @Query("sort") String sort
    );
}
