package com.example.man_zone.Model;

import java.util.List;

public class MessageResponse {
    public boolean success;
    public String message;
    public Data data;
    public String errors;

    public static class Data {
        public int totalElements;
        public int totalPages;
        public Pageable pageable;
        public int size;
        public List<Message> content;
        public int number;
        public int numberOfElements;
        public Sort sort;
        public boolean first;
        public boolean last;
        public boolean empty;
    }

    public static class Pageable {
        public boolean paged;
        public int pageNumber;
        public int pageSize;
        public int offset;
        public Sort sort;
        public boolean unpaged;
    }

    public static class Sort {
        public boolean sorted;
        public boolean empty;
        public boolean unsorted;
    }

    public static class Message {
        public long id;
        public String createdAt;
        public String updatedAt;
        public long senderId;
        public String senderEmail;
        public String type;
        public String message;
        public String imageUrl;
    }
}
