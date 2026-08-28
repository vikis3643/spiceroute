package com.aditya.restaurant_backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "restaurant_id",
        nullable = false
)
private Restaurant restaurant;

    @NotBlank(message = "Menu item name is required")
    @Size(
            min = 2,
            max = 150,
            message = "Name must contain 2 to 150 characters"
    )
    @Column(nullable = false, length = 150)
    private String name;

    @Size(
            max = 1000,
            message = "Description cannot exceed 1000 characters"
    )
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than zero"
    )
    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal price;

    @Column(length = 1000)
    private String imageUrl;

    @Column(nullable = false)
    private boolean vegetarian;

    @Column(nullable = false)
    private boolean available = true;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "spice_level",
            length = 20
    )
    private SpiceLevel spiceLevel =
            SpiceLevel.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "taste_type",
            length = 20
    )
    private TasteType tasteType =
            TasteType.SAVOURY;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "protein_level",
            length = 20
    )
    private ProteinLevel proteinLevel =
            ProteinLevel.NORMAL;

    @NotNull(message = "Category is required")
    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

    public MenuItem() {
    }

    @PrePersist
    public void beforeSave() {

        if (spiceLevel == null) {
            spiceLevel =
                    SpiceLevel.MEDIUM;
        }

        if (tasteType == null) {
            tasteType =
                    TasteType.SAVOURY;
        }

        if (proteinLevel == null) {
            proteinLevel =
                    ProteinLevel.NORMAL;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(
            Long id
    ) {
        this.id = id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(
            Restaurant restaurant
    ) {
        this.restaurant =
                restaurant;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name =
                name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description =
                description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(
            BigDecimal price
    ) {
        this.price =
                price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(
            String imageUrl
    ) {
        this.imageUrl =
                imageUrl;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(
            boolean vegetarian
    ) {
        this.vegetarian =
                vegetarian;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(
            boolean available
    ) {
        this.available =
                available;
    }

    public SpiceLevel getSpiceLevel() {
        return spiceLevel;
    }

    public void setSpiceLevel(
            SpiceLevel spiceLevel
    ) {
        this.spiceLevel =
                spiceLevel;
    }

    public TasteType getTasteType() {
        return tasteType;
    }

    public void setTasteType(
            TasteType tasteType
    ) {
        this.tasteType =
                tasteType;
    }

    public ProteinLevel getProteinLevel() {
        return proteinLevel;
    }

    public void setProteinLevel(
            ProteinLevel proteinLevel
    ) {
        this.proteinLevel =
                proteinLevel;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(
            Category category
    ) {
        this.category =
                category;
    }
}