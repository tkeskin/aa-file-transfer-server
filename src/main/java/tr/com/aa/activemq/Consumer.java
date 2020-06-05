package tr.com.aa.activemq;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import tr.com.aa.service.FileDownloadService;
import tr.com.aa.service.FileUploadService;
import tr.com.aa.service.JobDestinationService;
import tr.com.aa.service.JobService;

@Slf4j
@Component
public class Consumer {

  @Autowired
  Producer producer;

  @Autowired
  JobDestinationService jobDestinationService;

  @Autowired
  FileDownloadService fileDownloadService;

  @Autowired
  FileUploadService fileUploadService;

  @Autowired
  JobService jobService;

  @JmsListener(destination = "${aa.download.queue}")
  public void receiveDownload(String id) throws InterruptedException {

    log.info("Received download file id= {}", id);
    Thread.sleep(750);
    fileDownloadService.downloadFile("/test", jobDestinationService.findById(UUID.fromString(id)))
        .thenApply(c -> jobDestinationService.findByDownloadFalseAndId(c))
        .whenComplete((result, a) -> producer.sendUpload(result.getId().toString()));
  }

  @JmsListener(destination = "${aa.upload.queue}")
  public void receiveUpload(String id) throws InterruptedException {

    log.info("Received upload file id= {}", id);
    Thread.sleep(9000);
    fileUploadService.uploadFile(jobDestinationService.findById(UUID.fromString(id)))
        .thenApply(x -> jobDestinationService.updateSend(x));
  }

}