package com.apapedia.catalog.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "catalog")
@JsonIgnoreProperties(value = {"image"}, allowSetters = true)
public class Catalog {
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    @Column(name = "seller", nullable = false)
    private UUID seller;

    @NotNull
    @Positive
    @Column(name = "price", nullable = false)
    private Integer price;

    @NotNull
    @NotBlank
    @Column(name = "product_name", nullable = false)
    private String productName;

    @NotNull
    @NotBlank
    @Column(name = "product_description", nullable = false)
    private String productDescription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category categoryId;

    @PositiveOrZero
    @NotNull
    @Column(name = "stok", nullable = false)
    private Integer stok;

    @NotNull(message = "Image is required")
    @Column(name = "image", nullable = false)
    @Lob
    private byte[] image;
}
