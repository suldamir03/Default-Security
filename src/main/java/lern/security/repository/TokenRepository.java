package lern.security.repository;

import lern.security.config.auth.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findTokenByTokenAndType(String token, String passwordResetToken);
}
