package tw.com.hippo_center_backend.h1repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.hippo_center_backend.h0bean.T41MemberBean;

public interface T41MemberRepository extends JpaRepository<T41MemberBean, Integer> {
	Optional<T41MemberBean> findByAccount(String account);

	//管理員，多項選項模糊查詢
	@Query(value = "SELECT * FROM T41_member WHERE name LIKE %:keyword% OR account LIKE %:keyword% OR phone LIKE %:keyword% OR address LIKE %:keyword%", nativeQuery = true)
    List<T41MemberBean> searchByKeywordNative(@Param("keyword") String keyword);
	
	//管理員-只查詢管理員
	@Query(value = "SELECT * FROM T41_member WHERE memberType = admin", nativeQuery = true)
	Page<T41MemberBean> searchAdmin(Pageable pageable);
	
	//管理員-只查詢一般使用者
	@Query(value = "SELECT * FROM T41_member WHERE memberType = general", nativeQuery = true)
	Page<T41MemberBean> searchGeneral(Pageable pageable);
	
	// 這是簡單的分頁查詢方法，會自動根據Pageable進行分頁查詢
	Page<T41MemberBean> findAll(Pageable pageable);
}
