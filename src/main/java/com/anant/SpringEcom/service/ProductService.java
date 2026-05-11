package com.anant.SpringEcom.service;

import com.anant.SpringEcom.model.Product;
import com.anant.SpringEcom.repo.ProductRepo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class ProductService {

    @Autowired
    private ProductRepo productrepo;
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private AiImageGenService aiImageGenService;
    @Autowired
    private VectorStore vectorStore;
    public List<Product> getAllProducts() {
        return productrepo.findAll();
    }

    public Product getProductById(int id) {
        return productrepo.findById(id).get();
    }

    public Product addProduct(Product product, MultipartFile image) throws IOException {
        product.setImageName(image.getOriginalFilename());
        product.setImageType((image.getContentType()));
        product.setImageData(image.getBytes());
        Product savedProduct = productrepo.save(product);

        String content = String.format("""
                
                Product Name: %s
                Description: %s
                Brand: %s
                Category: %s
                Price: %.2f
                Release Date: %s
                Available: %s
                Stock: %s
                """,
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getBrand(),
                savedProduct.getCategory(),
                savedProduct.getPrice(),
                savedProduct.getReleaseDate(),
                savedProduct.isProductAvailable(),
                savedProduct.getStockQuantity()
        );

        Document document = new Document(
                UUID.randomUUID().toString(),
                content,
                Map.of("productId", String.valueOf(savedProduct.getId()))
        );
        vectorStore.add(List.of(document));
        return savedProduct;
    }

    public Product updatedProduct(int id, Product newProduct) {
        Product existing = productrepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        existing.setName(newProduct.getName());
        existing.setPrice(newProduct.getPrice());
        existing.setDescription(newProduct.getDescription());
        // don’t overwrite image unless provided
        return productrepo.save(existing);
    }

    public Product updateImage(int id, MultipartFile imageFile) throws IOException {
        Product product = productrepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setImageName(imageFile.getOriginalFilename()); // better than .getName()
        product.setImageType(imageFile.getContentType());
        product.setImageData(imageFile.getBytes());

        return productrepo.save(product); // save expects Product
    }

    public void deleteProduct(int id) {
        Product product=productrepo.findById(id).get();
        productrepo.delete(product);

    }

    public List<Product> searchProducts(String keyword) {
        return productrepo.searchProducts(keyword);
    }

    public String generateDescription(String name, String category) {
        String descPrompt=String.format("""
                Write a concise and professional product description for an e-commerce listing.
                
                Product Name: %s
                category: %s
                
                Keep it simple,engaging,and highlight its primary features or benefits.
                Avoid technicaljargon and keep it customer-friendly.
                Limit the description to 250 characters maximum.
                """,name,category);
        String desc=chatClient.prompt(descPrompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
        return desc;
    }

    public byte[] generateImage(String name, String category, String description) {

        String imagePrompt = String.format("""
                Generate a highly realistic, professional-grade e-commerce product image.
                
                     Product Details:
                     - Category: %s
                     - Name: '%s'
                     - Description: %s
                
                     Requirements:
                     - Use a clean, minimalistic, white or very light grey background.
                     - Ensure the product is well-lit with soft, natural-looking lighting.
                     - Add realistic shadows and soft reflections to ground the product naturally.
                     - No humans, brand logos, watermarks, or text overlays should be visible.
                     - Showcase the product from its most flattering angle that highlights key features.
                     - Ensure the product occupies a prominent position in the frame, centered or slightly off-centered.
                     - Maintain a high resolution and sharpness, ensuring all textures, colors, and details are clear.
                     - Follow the typical visual style of top e-commerce websites like Amazon, Flipkart, or Shopify.
                     - Make the product appear life-like and professionally photographed in a studio setup.
                     - The final image should look immediately ready for use on an e-commerce website without further editing.
                     """, category, name, description);

        byte[] aiImage = aiImageGenService.generateImage(imagePrompt);
        return aiImage;
    }
}
