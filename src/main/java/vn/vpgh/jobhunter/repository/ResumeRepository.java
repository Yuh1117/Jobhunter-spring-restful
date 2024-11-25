package vn.vpgh.jobhunter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.vpgh.jobhunter.domain.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Page<Resume> findAll(Specification<Resume> specification, Pageable pageable);
}
