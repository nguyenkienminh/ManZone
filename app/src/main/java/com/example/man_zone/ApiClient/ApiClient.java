package com.example.man_zone.ApiClient;

import com.example.man_zone.Interfaces.ConversationService;
import com.example.man_zone.Interfaces.MessageService;
import com.example.man_zone.Interfaces.OrderService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static String baseUrl = "https://manzone.wizlab.io.vn/";
    private static Retrofit retrofit;
    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Configure Gson with InstantAdapter for proper Instant handling
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantAdapter())
                    .create();

            retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getOkHttpClient())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }

    public static OrderService getOrderService() {
        return getClient().create(OrderService.class);
    }

    public static ConversationService getConversationService() {
        return getClient().create(ConversationService.class);
    }

    public static MessageService getMessageService() {
        return getClient().create(MessageService.class);
    }

    /**
     * Custom Gson adapter for Instant serialization/deserialization
     * Handles various ISO-8601 formats including microseconds
     * This is the same adapter used in WebSocketManager for consistency
     */
    private static class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
        @Override
        public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.toString());
        }

        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                String instantString = json.getAsString();

                // Handle microseconds by truncating to milliseconds if needed
                if (instantString.contains(".") && instantString.endsWith("Z")) {
                    String[] parts = instantString.split("\\.");
                    if (parts.length == 2) {
                        String secondsPart = parts[0];
                        String fractionPart = parts[1].substring(0, parts[1].length() - 1); // Remove 'Z'

                        // Truncate or pad fractional seconds to 9 digits (nanoseconds)
                        if (fractionPart.length() > 9) {
                            fractionPart = fractionPart.substring(0, 9);
                        } else {
                            while (fractionPart.length() < 9) {
                                fractionPart += "0";
                            }
                        }

                        instantString = secondsPart + "." + fractionPart + "Z";
                    }
                }

                return Instant.parse(instantString);
            } catch (Exception e) {
                throw new JsonParseException("Failed to parse Instant: " + json.getAsString(), e);
            }
        }
    }
}
