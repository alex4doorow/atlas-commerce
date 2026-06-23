package com.afa.atlas.commerce.catalog.documents;

import com.afa.atlas.commerce.catalog.enums.ProductReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product_reviews")
public class ProductReviewDocument {

    @Id
    private String id;

    @Indexed
    private String productId;

    private String authorName;

    private Integer rating;

    private String title;

    private String text;

    private List<String> pros;

    private List<String> cons;

    private ProductReviewStatus status;

    @CreatedDate
    private Instant createdAt;
}