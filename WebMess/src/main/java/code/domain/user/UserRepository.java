package code.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>
{
    @Query(value="SELECT * FROM user WHERE name=:name", nativeQuery = true)
    Optional<UserEntity> findByName(@Param("name") String name);

    @Query(value="SELECT * FROM user WHERE user_no=:no", nativeQuery = true)
    Optional<UserEntity> findByUserNo(@Param("no") Long userNo);

}
