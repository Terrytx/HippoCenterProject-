package tw.com.hippo_center_backend.h9config.b1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import tw.com.hippo_center_backend.h7interceptor.T37TrafficStatsInterceptor;




@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private T37TrafficStatsInterceptor trafficStatsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(trafficStatsInterceptor);
    }
}




//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    
//    private final UploadProperties uploadProperties;
//
//    public WebConfig(UploadProperties uploadProperties) {
//        this.uploadProperties = uploadProperties;
//    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/api/images/**")
//                .addResourceLocations("file:" + uploadProperties.getPath() + "/");
//    }
//}