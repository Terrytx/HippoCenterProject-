package tw.com.hippo_center_backend.h3controller.b2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tw.com.hippo_center_backend.h0bean.T21ProductBean;
import tw.com.hippo_center_backend.h0bean.T28ProductImageBean;
import tw.com.hippo_center_backend.h1repository.T28ProductImageRepository;
import tw.com.hippo_center_backend.h2service.b2.ProductService;
import tw.com.hippo_center_backend.h4dto.b2.CategoryDTO;
import tw.com.hippo_center_backend.h4dto.b2.ProductDTO;
import tw.com.hippo_center_backend.h4dto.b2.ProductRequest;
import tw.com.hippo_center_backend.h4dto.b2.ProductResponse;

@RestController
@RequestMapping("/mowmow/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private T28ProductImageRepository productImageRepository;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = productService.findAllCategories();
        return ResponseEntity.ok(categories);
    }

    // 查詢所有產品（動態條件）
    @PostMapping("/find")
    public ResponseEntity<ProductResponse> findProducts(@RequestBody(required = false) String json) {
        try {
            JSONObject obj = new JSONObject(json != null ? json : "{}");

            // 動態解析查詢條件
            Integer categoryId = obj.has("categoryId") ? obj.getInt("categoryId") : null; // 類型條件
            String productName = obj.optString("productName", null);
            String status = obj.optString("status", null);
            Integer minPrice = obj.has("minPrice") ? obj.getInt("minPrice") : null;
            Integer maxPrice = obj.has("maxPrice") ? obj.getInt("maxPrice") : null;
            // 查詢產品
            List<ProductDTO> products = productService.findProducts(categoryId, productName, status, minPrice,
                    maxPrice);

            // 建立 ProductResponse
            ProductResponse response = new ProductResponse();
            response.setSuccess(true);
            response.setMessage("查詢成功");
            response.setList(products);
            response.setCount((long) products.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 捕獲異常
            return ResponseEntity.badRequest().body(new ProductResponse(false, "查詢失敗: " + e.getMessage(), null, 0L));
        }
    }

    // 查詢單筆產品
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer id) {
        try {
            // 調用 Service 查詢商品
            T21ProductBean product = productService.getProductById(id);

            // 將實體轉換為 DTO
            ProductDTO dto = new ProductDTO();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setStock(product.getStock());
            dto.setStatus(product.getStatus());
            dto.setCategoryName(product.getCategory().getName());

            // 處理圖片
            List<String> imageUrls = product.getImages() != null
                    ? product.getImages().stream().map(T28ProductImageBean::getImageUrl).toList()
                    : List.of();
            dto.setImageUrls(imageUrls);

            return ResponseEntity.ok(dto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 新增產品
    @PostMapping
    public ResponseEntity<?> addProduct(
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") Integer price,
            @RequestParam("stock") Integer stock,
            @RequestParam("status") String status,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "images", required = false) List<MultipartFile> images // 接收圖片
    ) {
        try {
            ProductRequest request = new ProductRequest();
            request.setProductName(productName);
            request.setDescription(description);
            request.setPrice(price);
            request.setStock(stock);
            request.setStatus(status);
            request.setCategoryId(categoryId);

            // 調用 Service
            T21ProductBean savedProduct = productService.insertProductWithImages(request, images);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("新增產品失敗：" + e.getMessage());
        }
    }

    // @PostMapping
    // public ResponseEntity<T21ProductBean> addProduct(@RequestBody T21ProductBean
    // product) {
    // T21ProductBean savedProduct = productService.insertProduct(product);
    // return ResponseEntity.ok(savedProduct);
    // }

    // 修改產品
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id,
            @RequestPart("data") ProductRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "deleteImages", required = false) List<String> deleteImages) {
        try {
            // 🔹 這裡要傳遞 deleteImages
            T21ProductBean updatedProduct = productService.updateProductWithImages(id, request, images, deleteImages);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新產品失敗：" + e.getMessage());
        }
    }
    

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateProductStatus(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null || (!status.equals("ACTIVE") && !status.equals("INACTIVE"))) {
                return ResponseEntity.badRequest().body("❌ 無效的狀態");
            }

            T21ProductBean product = productService.updateProductStatus(id, status);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ 更新產品狀態失敗：" + e.getMessage());
        }
    }

    // 刪除產品
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // 查詢所有產品
    @GetMapping("/findAll")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/image")
public ResponseEntity<?> deleteProductImage(@RequestParam("imageUrl") String imageUrl) {
    try {
        String decodedUrl = java.net.URLDecoder.decode(imageUrl, StandardCharsets.UTF_8);
        System.out.println("📌 解碼後的圖片 URL：" + decodedUrl);

        // 1️⃣ 查找圖片
        T28ProductImageBean image = productImageRepository.findByImageUrl(decodedUrl)
                .orElseThrow(() -> new IllegalArgumentException("❌ 找不到圖片：" + decodedUrl));

        // 2️⃣ 刪除本地圖片檔案（⚠️ 只刪除本地存放的圖片，URL 連結的圖片不動）
        if (!decodedUrl.startsWith("http")) {
            try {
                Path imagePath = Paths.get(decodedUrl.replace("http://192.168.23.89:8080/productimage/",
                        "C:/image/productimage/"));
                Files.deleteIfExists(imagePath);
                System.out.println("✅ 已刪除本地圖片：" + imagePath);
            } catch (IOException e) {
                System.err.println("❌ 無法刪除圖片：" + decodedUrl);
            }
        }

        // 3️⃣ 從資料庫刪除記錄
        productImageRepository.delete(image);

        return ResponseEntity.ok("✅ 圖片已刪除：" + decodedUrl);
    } catch (Exception e) {
        return ResponseEntity.status(500).body("❌ 刪除圖片失敗：" + e.getMessage());
    }
}


}
