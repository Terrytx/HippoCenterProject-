package tw.com.hippo_center_backend.h2service.b1;

import jakarta.servlet.http.HttpServletRequest;
import tw.com.hippo_center_backend.h0bean.T37TrafficStats;
import tw.com.hippo_center_backend.h1repository.T37TrafficStatsRepository;
import java.util.List;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class T37TrafficStatsService {
private static final Logger logger = LoggerFactory.getLogger(T37TrafficStatsService.class);

    private final T37TrafficStatsRepository trafficStatsRepository;
    

    public T37TrafficStatsService(T37TrafficStatsRepository trafficStatsRepository) {
        this.trafficStatsRepository = trafficStatsRepository;
    }

    public void logVisit(HttpServletRequest request) {
        T37TrafficStats stats = new T37TrafficStats();
        stats.setIpAddress(request.getRemoteAddr());
        stats.setUserAgent(request.getHeader("User-Agent"));
        stats.setPageUrl(request.getRequestURI());
        stats.setReferer(request.getHeader("Referer"));
        stats.setVisitTime(LocalDateTime.now());

        trafficStatsRepository.save(stats);
    }
    
    

    public List<T37TrafficStats> getAllTrafficStats() {
        logger.info("Fetching all traffic stats");
        List<T37TrafficStats> stats = trafficStatsRepository.findAll();
        logger.info("Found {} traffic stats records", stats.size());
        return stats;
    }
    
}
