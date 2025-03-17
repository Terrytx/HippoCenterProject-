package tw.com.hippo_center_backend.h2service.b5;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import tw.com.hippo_center_backend.h0bean.T51VenueBean;

@Component
public class VenueSpecification {
    
    public static Specification<T51VenueBean> getSpecification(JSONObject criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 場地名稱查詢
            if (criteria.has("name") && StringUtils.hasText(criteria.getString("name"))) {
                predicates.add(cb.like(root.get("venueName"), 
                    "%" + criteria.getString("name") + "%"));
            }
            
            // 容納人數範圍查詢
            if (criteria.has("minCapacity")) {
                predicates.add(cb.greaterThanOrEqualTo(
                    root.get("capacity"), criteria.getInt("minCapacity")));
            }
            if (criteria.has("maxCapacity")) {
                predicates.add(cb.lessThanOrEqualTo(
                    root.get("capacity"), criteria.getInt("maxCapacity")));
            }
            
            // 租用狀態查詢
            if (criteria.has("rentalStatus")) {
                predicates.add(cb.equal(
                    root.get("rentalStatus"), criteria.getBoolean("rentalStatus")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
