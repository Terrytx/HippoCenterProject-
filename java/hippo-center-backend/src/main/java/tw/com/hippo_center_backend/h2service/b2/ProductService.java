package tw.com.hippo_center_backend.h2service.b2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import tw.com.hippo_center_backend.h0bean.T21ProductBean;
import tw.com.hippo_center_backend.h0bean.T22CategoriesBean;
import tw.com.hippo_center_backend.h0bean.T28ProductImageBean;
import tw.com.hippo_center_backend.h1repository.T21ProductRepository;
import tw.com.hippo_center_backend.h1repository.T22CategoriesRepository;
import tw.com.hippo_center_backend.h1repository.T28ProductImageRepository;
import tw.com.hippo_center_backend.h4dto.b2.CategoryDTO;
import tw.com.hippo_center_backend.h4dto.b2.ProductDTO;
import tw.com.hippo_center_backend.h4dto.b2.ProductRequest;

@Service
public class ProductService {
    @Autowired
    private T21ProductRepository productRepository;

    @Autowired
    private T22CategoriesRepository categoryRepository;

    @Autowired
    private T28ProductImageRepository productImageRepository;

    // 新增產品
    @Transactional
    public T21ProductBean insertProductWithImages(ProductRequest request, List<MultipartFile> images) {
        if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("產品名稱不可為空");
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("產品類別不可為空");
        }

        // **確保類別存在**
        T22CategoriesBean category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("類別 ID " + request.getCategoryId() + " 不存在"));

        // **建立新產品**
        T21ProductBean newProduct = new T21ProductBean();
        newProduct.setProductName(request.getProductName());
        newProduct.setDescription(request.getDescription());
        newProduct.setPrice(request.getPrice());
        newProduct.setStock(request.getStock());
        newProduct.setStatus("INACTIVE");
        newProduct.setCategory(category);
        newProduct.setCreatedAt(LocalDateTime.now());
        newProduct.setUpdatedAt(LocalDateTime.now());

        // **先儲存產品，取得 `productId`**
        T21ProductBean savedProduct = productRepository.save(newProduct);

        // **處理圖片**
        if (images != null && !images.isEmpty()) {
            List<T28ProductImageBean> imageBeans = new ArrayList<>();
            String categoryName = category.getName();
            Path imageDir = Paths.get("C:/image/productimage/", categoryName);

            try {
                Files.createDirectories(imageDir); // 確保目錄存在
                for (MultipartFile image : images) {
                    String fileName = categoryName + savedProduct.getProductId() + "_" + (imageBeans.size() + 1)
                            + ".jpg";
                    Path targetPath = imageDir.resolve(fileName);
                    Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                    String imageUrl = "http://192.168.23.89:8080/productimage/" + categoryName + "/" + fileName;

                    T28ProductImageBean imageBean = new T28ProductImageBean();
                    imageBean.setProduct(savedProduct);
                    imageBean.setImageUrl(imageUrl);
                    imageBeans.add(imageBean);
                }

                productImageRepository.saveAll(imageBeans);
                savedProduct.setImages(imageBeans);
            } catch (IOException e) {
                throw new RuntimeException("圖片保存失敗：" + e.getMessage());
            }
        }

        return savedProduct;
    }

    // 更新產品 (給CartService使用)
    public T21ProductBean updateProduct(T21ProductBean bean) {
        // 如果 bean 是 null 或 productId 是 null，拋出例外
        if (bean == null || bean.getProductId() == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        // 如果 productId 不存在，拋出例外
        if (!productRepository.existsById(bean.getProductId())) {
            throw new IllegalStateException("Product with ID " + bean.getProductId() + "not found");
        }

        // 從資料庫中取出現有的產品資料
        T21ProductBean existingProduct = productRepository.findById(bean.getProductId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        bean.getProductId() + " not found"));

        // 保留原有的 createdAt 值
        bean.setCreatedAt(existingProduct.getCreatedAt());
        // 設置更新時間
        bean.setUpdatedAt(LocalDateTime.now());

        // 保存並返回更新的商品
        return productRepository.save(bean);
    }

    // 更新產品
    @Transactional
    public T21ProductBean updateProductWithImages(Integer productId, ProductRequest request,
            List<MultipartFile> images, List<String> deleteImages) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
    
        // 1️⃣ 確保產品存在
        T21ProductBean existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product with ID " + productId + " not found"));
    
        // 2️⃣ 更新 `updatedAt`
        existingProduct.setUpdatedAt(LocalDateTime.now());
    
        // 3️⃣ 允許不修改的欄位
        if (request.getProductName() != null && !request.getProductName().trim().isEmpty()) {
            existingProduct.setProductName(request.getProductName());
        }
        if (request.getDescription() != null) {
            existingProduct.setDescription(request.getDescription().trim());
        }
        if (request.getCategoryId() != null) {
            T22CategoriesBean category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("類別 ID " + request.getCategoryId() + " 不存在"));
            existingProduct.setCategory(category);
        }
        if (request.getPrice() != null && request.getPrice() > 0) {
            existingProduct.setPrice(request.getPrice());
        }
        if (request.getStock() != null && request.getStock() >= 0) {
            existingProduct.setStock(request.getStock());
        }
        if (request.getStatus() != null) {
            existingProduct.setStatus(request.getStatus());
        }
    
        // 4️⃣ 更新產品資訊
        T21ProductBean updatedProduct = productRepository.save(existingProduct);
    
        // 5️⃣ **處理圖片邏輯**
        Hibernate.initialize(existingProduct.getCategory());
        T22CategoriesBean category = existingProduct.getCategory();
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("產品的類別名稱無效，無法存放圖片");
        }
        String categoryName = category.getName();
    
        Path imageDir = Paths.get("C:/image/productimage/", categoryName);
        try {
            Files.createDirectories(imageDir);
        } catch (IOException e) {
            throw new RuntimeException("無法建立圖片目錄：" + imageDir, e);
        }
    
        // **刪除選擇的舊圖片**
        if (deleteImages != null && !deleteImages.isEmpty()) {
            for (String imageUrl : deleteImages) {
                T28ProductImageBean image = productImageRepository.findByImageUrl(imageUrl)
                        .orElseThrow(() -> new IllegalArgumentException("❌ 找不到圖片：" + imageUrl));
    
                if (!imageUrl.startsWith("http")) {
                    try {
                        Path imagePath = Paths.get(imageUrl.replace("http://192.168.23.89:8080/productimage/",
                                "C:/image/productimage/"));
                        Files.deleteIfExists(imagePath);
                    } catch (IOException e) {
                        System.err.println("❌ 無法刪除圖片：" + imageUrl);
                    }
                }
                productImageRepository.delete(image);
            }
        }
    
        // **儲存新圖片**
        List<T28ProductImageBean> newImages = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                String fileName = categoryName + updatedProduct.getProductId() + "_" + (newImages.size() + 1) + ".jpg";
                Path targetPath = imageDir.resolve(fileName);
                try {
                    Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    
                    // 產生 URL
                    String imageUrl = "http://192.168.23.89:8080/productimage/" + categoryName + "/" + fileName;
                    T28ProductImageBean imageBean = new T28ProductImageBean();
                    imageBean.setProduct(updatedProduct);
                    imageBean.setImageUrl(imageUrl);
                    newImages.add(imageBean);
                } catch (IOException e) {
                    System.err.println("❌ 圖片處理失敗：" + image.getOriginalFilename() + "，錯誤：" + e.getMessage());
                }
            }
        }
    
        // 儲存新的圖片記錄
        if (!newImages.isEmpty()) {
            productImageRepository.saveAll(newImages);
            updatedProduct.setImages(newImages);
        }
    
        return updatedProduct;
    }
    

    // 刪除產品
    public void deleteProduct(Integer productId) {
        // 驗證 productId 是否存在
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        T21ProductBean product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found"));

        // 刪除關聯的圖片
        List<T28ProductImageBean> images = product.getImages();
        if (images != null && !images.isEmpty()) {
            for (T28ProductImageBean image : images) {
                productImageRepository.delete(image);
            }
        }

        // 刪除商品
        productRepository.delete(product);
    }

    // 初始化 DTO 映射
    private ProductDTO mapToProductDTO(T21ProductBean product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setStatus(product.getStatus());

        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }

        List<String> imageUrls = product.getImages() != null
                ? product.getImages().stream()
                        .map(T28ProductImageBean::getImageUrl)
                        .collect(Collectors.toList())
                : List.of();
        dto.setImageUrls(imageUrls);

        return dto;
    }

    // 查詢所有產品
    public List<ProductDTO> getAllProducts() {
        List<T21ProductBean> products = productRepository.findAll();

        // 初始化延遲加載的屬性（如果需要）
        products.forEach(product -> {
            if (product.getCategory() != null) {
                Hibernate.initialize(product.getCategory().getProducts());
            }
        });

        // 映射為 DTO
        return products.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }

    // 依照產品 ID 查詢單一產品 用於後端邏輯 (給CartService使用)
    public T21ProductBean getProductById(Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        T21ProductBean product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found"));

        // 初始化延遲加載的關聯屬性
        Hibernate.initialize(product.getImages());
        Hibernate.initialize(product.getCategory());

        return product;
    }

    // 動態查詢產品
    public List<ProductDTO> findProducts(Integer categoryId, String productName, String status, Integer minPrice,
            Integer maxPrice) {
        // 使用 ExampleMatcher 動態查詢
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        // 建立篩選條件的 Bean
        T21ProductBean filter = new T21ProductBean();
        filter.setProductName(productName);
        filter.setStatus(status);

        // 如果有 categoryId，設置類別條件
        if (categoryId != null) {
            T22CategoriesBean category = new T22CategoriesBean();
            category.setCategoryId(categoryId);
            filter.setCategory(category);
        }

        // 建立 Example
        Example<T21ProductBean> example = Example.of(filter, matcher);

        // 查詢結果
        List<T21ProductBean> results = productRepository.findAll(example);

        // 僅保留價格範圍內的產品
        if (minPrice != null || maxPrice != null) {
            results = results.stream()
                    .filter(product -> (minPrice == null || product.getPrice() >= minPrice)
                            && (maxPrice == null || product.getPrice() <= maxPrice))
                    .collect(Collectors.toList());
        }

        // 映射為 DTO
        return results.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> findAllCategories() {
        List<T22CategoriesBean> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> {
                    CategoryDTO dto = new CategoryDTO();
                    dto.setCategoryId(category.getCategoryId());
                    dto.setCategoryName(category.getName());
                    dto.setDescription(category.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public T21ProductBean updateProductStatus(Integer productId, String newStatus) {
        T21ProductBean product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("找不到產品 ID：" + productId));

        product.setStatus(newStatus);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    @Transactional
public void deleteProductImage(String imageUrl) {
    if (imageUrl == null || imageUrl.trim().isEmpty()) {
        throw new IllegalArgumentException("❌ 圖片 URL 不能為空");
    }

    T28ProductImageBean image = productImageRepository.findByImageUrl(imageUrl)
            .orElseThrow(() -> new IllegalArgumentException("❌ 找不到圖片：" + imageUrl));

    // 🔹 刪除本地圖片檔案
    if (!imageUrl.startsWith("http")) {
        try {
            Path imagePath = Paths.get(imageUrl.replace("http://192.168.23.89:8080/productimage/",
                    "C:/image/productimage/"));
            Files.deleteIfExists(imagePath);
            System.out.println("✅ 已刪除本地圖片：" + imagePath);
        } catch (IOException e) {
            System.err.println("❌ 無法刪除圖片：" + imageUrl);
        }
    }

    // 🔹 刪除資料庫記錄
    productImageRepository.delete(image);
}


}
