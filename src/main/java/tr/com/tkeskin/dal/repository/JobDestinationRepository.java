package tr.com.tkeskin.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.history.RevisionRepository;
import tr.com.tkeskin.dal.entity.JobDestinationEntity;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.UUID;

public interface JobDestinationRepository extends
        RevisionRepository<JobDestinationEntity, UUID, Integer>, JpaRepository<JobDestinationEntity,
        UUID> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<JobDestinationEntity> findByJobEntityId(UUID jobId);

    List<JobDestinationEntity> findByDownloadFalseAndJobEntityId(UUID jobId);

    List<JobDestinationEntity> findByDownloadTrueAndSendFalseAndJobEntityId(UUID jobId);

    List<JobDestinationEntity> findByDownloadFalse();

    JobDestinationEntity findByDownloadFalseAndId(UUID id);

    JobDestinationEntity getById(UUID id);
}