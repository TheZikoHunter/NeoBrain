package com.code.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "produit")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Long id;
    @Column(name = "code_produit", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le code produit est obligatoire")
    @Size(max = 50, message = "Le code produit ne peut pas dépasser 50 caractères")
    private String codeProduit;
    @Column(name = "nom", nullable = false, length = 200)
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 200, message = "Le nom ne peut pas dépasser 200 caractères")
    private String name;
    @Column(name = "prix", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
    private double price;
    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", nullable = false)
    @NotNull(message = "La catégorie est obligatoire")
    private String category;
    private String image;
    @Column(name = "IS_NEW")
    private boolean isNew;
    private Long discount;
    @Column(name = "description", length = 1000)
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    @Column(name = "quantite_stock", nullable = false)
    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    private Integer quantiteStock;

    public Product() {
    }

    public Product(Long id, String name, double price, String category, String image, boolean isNew, Long discount, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
        this.isNew = isNew;
        this.discount =discount;
        this.description = description;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
