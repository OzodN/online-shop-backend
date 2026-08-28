package dev.ozodn.onlineshop.user.repository;

import dev.ozodn.onlineshop.user.entity.RefreshToken;
import dev.ozodn.onlineshop.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link RefreshToken} persistence operations.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Retrieves a refresh token by its token value.
     *
     * @param token raw token string
     * @return an {@link Optional} containing the token if found, or empty otherwise
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Deletes all refresh tokens associated with the specified user.
     *
     * @param user user whose tokens should be deleted
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);
}