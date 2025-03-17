package tw.com.hippo_center_backend.h1repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.hippo_center_backend.h0bean.T43ResetPasswordBean;

public interface T43ResetPasswordRepository extends JpaRepository<T43ResetPasswordBean, Integer> {
	Optional<T43ResetPasswordBean> findByMember_MemberId(Integer memberId);  // 根據 memberId 查詢
	
    Optional<T43ResetPasswordBean> findByResetCode(String resetCode);
    
    Optional<T43ResetPasswordBean> findByUuid(String uuid);

}
