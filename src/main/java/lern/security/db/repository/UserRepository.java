package lern.security.db.repository;

import lern.security.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Boolean existsByEmail(String email);
    @Query(value = "select u.* from users u join tokens vt on u.id = vt.user_id where token=?1 and type=?2",
    nativeQuery = true)
    Optional<User> getUserByPasswordResetToken(String token, String passwordResetToken);
}
