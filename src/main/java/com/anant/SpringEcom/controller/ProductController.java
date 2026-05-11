package com.anant.SpringEcom.controller;

import com.anant.SpringEcom.model.Product;
import com.anant.SpringEcom.service.AiImageGenService;
import com.anant.SpringEcom.service.ProductService;
import org.hibernate.query.sqm.function.SelfRenderingWindowFunctionSqlAstExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class ProductController {
    @Autowired
    ProductService productservice;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(){
        return new ResponseEntity<>(productservice.getAllProducts(), HttpStatus.OK);
    }
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id){
        Product product=productservice.getProductById(id);

        if(product!=null) {
            return new ResponseEntity<>(productservice.getProductById(id), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/product/{productId}/image")
    public ResponseEntity<byte[]> getImageById(@PathVariable int productId)
    {
        Product product =productservice.getProductById(productId);
        return new  ResponseEntity<>(product.getImageData(),HttpStatus.OK);
    }

    @PostMapping("/product")
    public ResponseEntity<?>  addProduct(@RequestPart Product product,@RequestPart MultipartFile imageFile){
        Product savedProduct= null;
        try {
            savedProduct = productservice.addProduct(product,imageFile);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(savedProduct,HttpStatus.CREATED);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable int id,
            @RequestPart("product") Product product,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        Product updated = productservice.updatedProduct(id, product);

        if (imageFile != null) {
            updated = productservice.updateImage(id, imageFile);
        }

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    @DeleteMapping("/product/{id}")
    public void deleteProduct(@PathVariable int id)
    {
         productservice.deleteProduct(id);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword){
        List<Product> products=productservice.searchProducts(keyword);
        System.out.println(keyword);
        return new ResponseEntity<>(products,HttpStatus.OK);
    }
    @PostMapping("/product/generate-description")
    public ResponseEntity<String> generateDescription(@RequestParam String name,@RequestParam String category)
    {
        try{
            String aiDesc=productservice.generateDescription(name,category);
            return new ResponseEntity<>(aiDesc,HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/product/generate-image")
    public ResponseEntity<?> generateImage(@RequestParam String name,@RequestParam String description,@RequestParam String category)
    {
        try {
            byte[] aiImage=productservice.generateImage(name,category,description);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}