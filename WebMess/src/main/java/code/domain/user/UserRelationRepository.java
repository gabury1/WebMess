package code.domain.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelationEntity, Long>
{
    @Query(value="SELECT * FROM user_relation WHERE main_no=:mainNo and sub_no=:subNo", nativeQuery=true)
    Optional<UserRelationEntity> findByMainNoAndSubNo(@Param("mainNo") Long mainNo, @Param("subNo") Long subNo);

    @Query(value="SELECT * FROM user_relation WHERE main_name=:mainName and sub_name=:subName", nativeQuery=true)
    Optional<UserRelationEntity> findByMainNameAndSubName(@Param("mainName") String mainName, @Param("subName") String subName);

    @Query(value="SELECT * FROM user_relation WHERE main_no=:mainNo", nativeQuery = true)
    List<UserRelationEntity> findByMainNo( @Param("mainNo") Long mainNo );

}
