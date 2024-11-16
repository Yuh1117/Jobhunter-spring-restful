package vn.vpgh.jobhunter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vpgh.jobhunter.domain.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Page<Company> findAll(Pageable pageable);
}
