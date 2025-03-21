package tw.com.hippo_center_backend.h6jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JwtConfig implements WebMvcConfigurer {
    @Autowired
    private JsonWebTokenInterceptor jsonWebTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jsonWebTokenInterceptor)
                .addPathPatterns("/secure/person", "/secure/person/*");

    }
}
