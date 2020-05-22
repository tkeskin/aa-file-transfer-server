package tr.com.aa.dal.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import tr.com.aa.dal.entity.FtpServerEntity;

@Repository
public interface FtpServerRepository extends JpaRepository<FtpServerEntity, UUID> {

  @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
  List<FtpServerEntity> findAll();

  Optional<FtpServerEntity> findById(String id);
}
