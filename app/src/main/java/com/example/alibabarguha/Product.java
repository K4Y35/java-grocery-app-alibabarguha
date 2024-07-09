package com.example.alibabarguha;

public class Product {
    private String categoryId;
    private String productImageUrl;
    private String productName;
    private String productQuantity;
    private String productPrice;

    public Product() {

    }

    public Product(String categoryId, String productImageUrl, String productName, String productQuantity, String productPrice) {
        this.categoryId = categoryId;
        this.productImageUrl = productImageUrl;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
    }



    public String getProductImageUrl() {
        return productImageUrl;
    }



    public String getProductName() {
        return productName;
    }


    public String getProductQuantity() {
        return productQuantity;
    }


    public String getProductPrice() {
        return productPrice;
    }


}
