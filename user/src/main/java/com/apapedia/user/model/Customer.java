package com.apapedia.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "id")
@JsonIgnoreProperties(value = {"id", "password", "email", "balance", "createdAt", "updatedAt", "cartId"}, allowSetters = true)
public class Customer extends User{
    @NotNull
    @Column(name = "cart_id")
    private UUID cartId = UUID.randomUUID();
}
