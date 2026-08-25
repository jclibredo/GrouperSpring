package phil.grouper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import phil.grouper.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // JpaRepository gives you save(), findAll(), findById(), etc. for free!
    User findByUsername(String username);
}
