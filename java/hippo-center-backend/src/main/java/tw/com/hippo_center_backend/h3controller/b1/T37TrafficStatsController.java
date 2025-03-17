package tw.com.hippo_center_backend.h3controller.b1;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.com.hippo_center_backend.h0bean.T37TrafficStats;
import tw.com.hippo_center_backend.h1repository.T37TrafficStatsRepository;

import java.util.List;

@RestController
@RequestMapping("/api/traffic")
public class T37TrafficStatsController {

	private final T37TrafficStatsRepository trafficStatsRepository;

    public T37TrafficStatsController(T37TrafficStatsRepository trafficStatsRepository) {
        this.trafficStatsRepository = trafficStatsRepository;
    }

    @GetMapping("/stats")
    public List<T37TrafficStats> getTrafficStats() {
        return trafficStatsRepository.findAll();
    }
    }
    
    
    




