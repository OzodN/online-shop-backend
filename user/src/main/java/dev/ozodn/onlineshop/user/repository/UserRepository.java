package dev.ozodn.onlineshop.user.repository;

import dev.ozodn.onlineshop.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link User} persistence operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves an active user by their email address.
     *
     * @param email email address to search for
     * @return an {@link Optional} containing the user if found and not deleted, or empty otherwise
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Retrieves an active user by their public external identifier.
     *
     * @param externalId external UUID of the user
     * @return an {@link Optional} containing the user if found and not deleted, or empty otherwise
     */
    @Query("SELECT u FROM User u WHERE u.externalId = :externalId AND u.deletedAt IS NULL")
    Optional<User> findByExternalId(@Param("externalId") UUID externalId);

    /**
     * Checks whether an active user with the given email exists.
     *
     * @param email email address to verify
     * @return {@code true} if an active user exists with the email, {@code false} otherwise
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    boolean existsByEmail(@Param("email") String email);
}