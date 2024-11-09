package vn.vpgh.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vpgh.jobhunter.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);

}
