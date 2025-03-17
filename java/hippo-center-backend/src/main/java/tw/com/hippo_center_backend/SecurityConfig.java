package tw.com.hippo_center_backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Step 1: SecurityFilterChain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 觀察此訊息，確認此設定檔有被載入
        System.out.println("=== SecurityConfig.filterChain() 啟動 ===");

        // 先把 CORS/CSRF 都開到最寬鬆，之後再一步步調整
        http.cors().and().csrf().disable()

                // 先允許所有請求，不做認證
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll());

        return http.build();
    }

    // Step 2: WebMvcConfigurer for CORS
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 觀察此訊息，確認跨域設定有被載入
                System.out.println("=== corsConfigurer.addCorsMappings() ===");

                // 為了最小可行測試，先開放所有來源、方法、標頭
                // 等確認能跨域成功後，再針對你實際需要的網域或方法做限制
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:9173", "http://localhost:5173")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}