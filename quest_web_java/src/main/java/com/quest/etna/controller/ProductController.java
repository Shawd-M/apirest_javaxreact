package com.quest.etna.controller;

import com.quest.etna.config.service.ProductService;
import com.quest.etna.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return ResponseEntity.ok(productService.updateProduct(id, productDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        productService.deleteProduct((long) id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Product> uploadProductImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        String imageUrl = saveImage(image);
        product.setImageUrl(imageUrl);
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    private String saveImage(MultipartFile image) {
        String uploadDir = System.getProperty("user.dir") + "/uploads/"; // Change to absolute path
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String imagePath = uploadDir + image.getOriginalFilename();
        File imageFile = new File(imagePath);
        try {
            image.transferTo(imageFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
        return "/uploads/" + image.getOriginalFilename(); // Return relative path for access
    }
}
