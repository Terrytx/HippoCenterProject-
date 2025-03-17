package tw.com.hippo_center_backend.h3controller.b2;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T21ProductBean;
import tw.com.hippo_center_backend.h0bean.T28ProductImageBean;
import tw.com.hippo_center_backend.h2service.b2.ProductService;
import tw.com.hippo_center_backend.h4dto.b2.ProductDTO;
import tw.com.hippo_center_backend.h4dto.b2.ProductResponse;

@RestController
@RequestMapping("/mowmow/user/products")
public class UserProductController {

    @Autowired
    private ProductService productService;

    // 查詢所有產品（動態條件）
    @PostMapping("/find")
    public ResponseEntity<ProductResponse> findProducts(@RequestBody(required = false) String json) {
        try {
            // 確保 JSON 非空
            JSONObject obj = new JSONObject(json != null ? json : "{}");

            // 提取參數
            Integer categoryId = obj.has("categoryId") ? obj.getInt("categoryId") : null;
            String productName = obj.optString("productName", null);
            String status = obj.optString("status", null);
            Integer minPrice = obj.has("minPrice") ? obj.getInt("minPrice") : null;
            Integer maxPrice = obj.has("maxPrice") ? obj.getInt("maxPrice") : null;

            // 呼叫 Service
            List<ProductDTO> products = productService.findProducts(categoryId, productName, status, minPrice,
                    maxPrice);

            // 組裝回應
            ProductResponse response = new ProductResponse(true, "查詢成功", products, (long) products.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
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

    // 查詢所有產品（簡化版）
    @GetMapping("/findAll")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}
