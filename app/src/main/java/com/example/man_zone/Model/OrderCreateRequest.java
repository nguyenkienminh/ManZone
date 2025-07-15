package com.example.man_zone.Model;

import java.util.List;

public class OrderCreateRequest {
    private String customerName;
    private String phoneNumber;
    private String shippingAddress;
    private String note;
    private int paymentId;
    private List<OrderDetailRequest> orderDetails;

    // Getters và Setters

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public List<OrderDetailRequest> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetailRequest> orderDetails) { this.orderDetails = orderDetails; }

    // Lồng OrderDetailRequest vào luôn
    public static class OrderDetailRequest {
        private int productId;
        private int quantity;

        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
