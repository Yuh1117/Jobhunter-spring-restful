package vn.vpgh.jobhunter.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.vpgh.jobhunter.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>,  JpaSpecificationExecutor<Role> {
    boolean existsByName(String name);

    Page<Role> findAll(Specification<Role> specification, Pageable pageable);

    List<Role> findByIdIn(List<Long> id);
}
