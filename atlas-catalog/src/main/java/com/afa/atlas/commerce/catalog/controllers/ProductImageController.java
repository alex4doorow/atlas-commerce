package com.afa.atlas.commerce.catalog.controllers;

import com.afa.atlas.commerce.catalog.dto.ProductDto;
import com.afa.atlas.commerce.catalog.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.afa.atlas.commerce.catalog.controllers.internal.ControllerConstants.PRODUCTS;

@RestController
@RequiredArgsConstructor
@RequestMapping(PRODUCTS)
public class ProductImageController {

    private final ProductService productService;

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductDto uploadImage(
            @PathVariable final UUID id,
            @RequestPart("file") final MultipartFile file) throws IOException {
        return productService.uploadImage(id, file);
    }
}