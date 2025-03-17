package tw.com.hippo_center_backend.h7interceptor;


import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tw.com.hippo_center_backend.h2service.b1.T37TrafficStatsService;

@Component
public class T37TrafficStatsInterceptor implements HandlerInterceptor {

    private final T37TrafficStatsService trafficStatsService;

    public T37TrafficStatsInterceptor(T37TrafficStatsService trafficStatsService) {
        this.trafficStatsService = trafficStatsService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        trafficStatsService.logVisit(request);
        return true;
    }
}
