package tr.com.aa.service;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.aa.dal.entity.JobDestinationEntity;
import tr.com.aa.dal.repository.JobDestinationRepository;
import tr.com.aa.mapper.JobDestinationMapper;
import tr.com.aa.models.JobDestinationDto;

@Service
@Transactional
public class JobDestinationServiceImpl implements JobDestinationService {

  @Autowired
  JobDestinationRepository jobDestinationRepository;

  @Override
  public List<JobDestinationDto> findByDownloadFalseAndJobEntityId(UUID jobId) {

    return JobDestinationMapper.INSTANCE
        .toJobDestinationDto(jobDestinationRepository.findByDownloadFalseAndJobEntityId(jobId));
  }

  @Override
  public List<JobDestinationDto> findByDownloadTrueAndSendFalseAndJobEntityId(UUID jobId) {

    return JobDestinationMapper.INSTANCE
        .toJobDestinationDto(
            jobDestinationRepository.findByDownloadTrueAndSendFalseAndJobEntityId(jobId));
  }

  @Override
  public String findByDownloadFalseAndId(List<JobDestinationDto> jobDestinationDto) {

    for (JobDestinationDto dto : jobDestinationDto) {
      JobDestinationEntity byDownload = jobDestinationRepository
          .findByDownloadFalseAndId(dto.getId());
      if (dto.getDownload()) {
        byDownload.setDownload(true);
        byDownload.setDownloadDateTime(dto.getDownloadDateTime());
        byDownload.setDownloadPath(dto.getDownloadPath());
        jobDestinationRepository.save(byDownload);
      }
    }
    return "aa";
  }

  @Override
  public String updateSend(List<JobDestinationDto> jobDestinationDto) {

    for (JobDestinationDto dto : jobDestinationDto) {
      JobDestinationEntity byUpload = jobDestinationRepository
          .getById(dto.getId());
      if (dto.getSend()) {
        byUpload.setSend(dto.getSend());
        byUpload.setSendDateTime(dto.getSendDateTime());
        jobDestinationRepository.save(byUpload);
      }
    }
    return "aa";
  }

  @Override
  public Boolean findAllfileSend(UUID id) {

    return jobDestinationRepository.findByJobEntityId(id)
        .stream()
        .allMatch(JobDestinationEntity::getSend);
  }
}