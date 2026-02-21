package com.skybrisk.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Entity class representing a Book in the bookstore system.
 * Contains book metadata, pricing, and stock information.
 */
@Entity
@Table(name = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String genre;

    @Column(unique = true)
    private String isbn;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer stockQuantity;

    private String imageUrl;
}
