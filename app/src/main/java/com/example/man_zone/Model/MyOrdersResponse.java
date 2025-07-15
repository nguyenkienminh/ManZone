package com.example.man_zone.Model;

public class MyOrdersResponse {
    private boolean success;
    private String message;
    private OrderData data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public OrderData getData() {
        return data;
    }

    public static class OrderData {
        private java.util.List<OrderModel> content;
        private PageInfo pageable;
        private int totalElements;
        private int totalPages;
        private boolean last;
        private int size;
        private int number;
        private int numberOfElements;
        private boolean first;
        private boolean empty;

        public java.util.List<OrderModel> getContent() {
            return content;
        }

        public PageInfo getPageable() {
            return pageable;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public boolean isLast() {
            return last;
        }

        public int getSize() {
            return size;
        }

        public int getNumber() {
            return number;
        }

        public int getNumberOfElements() {
            return numberOfElements;
        }

        public boolean isFirst() {
            return first;
        }

        public boolean isEmpty() {
            return empty;
        }
    }

    public static class PageInfo {
        private int pageNumber;
        private int pageSize;
        private int offset;
        private boolean paged;
        private boolean unpaged;

        public int getPageNumber() {
            return pageNumber;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getOffset() {
            return offset;
        }

        public boolean isPaged() {
            return paged;
        }

        public boolean isUnpaged() {
            return unpaged;
        }
    }
}
