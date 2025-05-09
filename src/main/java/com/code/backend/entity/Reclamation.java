package com.code.backend.entity;

import jakarta.persistence.*;

@Entity
public class Reclamation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;
    private String purchaseDate;
    private String name;
    private String email;
    private String phone;
    private String issueType;
    private String productName;

    @Column(length = 2000)
    private String description;

    private String photoFilenames;

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", issueType='" + issueType + '\'' +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", photoFilenames='" + photoFilenames + '\'' +
                '}';
    }

    public Reclamation() {
    }

    public Reclamation(Long id, String orderNumber, String purchaseDate, String name, String email, String phone, String issueType, String productName, String description, String photoFilenames) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.purchaseDate = purchaseDate;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.issueType = issueType;
        this.productName = productName;
        this.description = description;
        this.photoFilenames = photoFilenames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPhotoFilenames() {
        return photoFilenames;
    }

    public void setPhotoFilenames(String photoFilenames) {
        this.photoFilenames = photoFilenames;
    }
}
