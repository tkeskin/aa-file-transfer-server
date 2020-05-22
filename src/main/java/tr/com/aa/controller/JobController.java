package tr.com.aa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.dsl.Files;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tr.com.aa.artemis.JmsProducer;
import tr.com.aa.exception.BadRequestException;
import tr.com.aa.exception.ErrorDetails;
import tr.com.aa.models.JobList;
import tr.com.aa.models.JobsDto;
import tr.com.aa.service.JobService;
import tr.com.aa.util.Const;

@Slf4j
@Tag(name = "job", description = "process")
@RestController
public class JobController {

  @Autowired
  JobService jobService;

  @Autowired
  JmsProducer jmsProducer;

  private final Map<String, SseEmitter> sses = new ConcurrentHashMap<>();

  /**
   * job - list
   *
   * @return .
   */
  @Operation(
      summary = "Find all job",
      description = "Find all job",
      tags = "Job"
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "successful operation",
              content = @Content(
                  array = @ArraySchema(
                      schema = @Schema(implementation = JobList.class)
                  )
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Bad request",
              content = @Content(
                  schema = @Schema(implementation = ErrorDetails.class)
              )
          )
      }
  )
  @GetMapping(value = Const.Request.JOB, produces = Const.JSON)
  public ResponseEntity<?> all() {

    try {
      return ResponseEntity.ok(jobService.findAll());
    } catch (Exception e) {
      throw new BadRequestException(e.getLocalizedMessage());
    }

  }

  /**
   * job - save
   *
   * @return .
   */
  @Operation(
      summary = "Create new job",
      description = "Create a new job",
      tags = "Job"
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "successful operation",
              content = @Content(
                  schema = @Schema(implementation = JobsDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Bad request",
              content = @Content(
                  schema = @Schema(implementation = ErrorDetails.class)
              )
          ),
          @ApiResponse(
              responseCode = "409",
              description = "already exists",
              content = @Content(
                  schema = @Schema(implementation = ErrorDetails.class)
              )
          )
      }
  )
  @PostMapping(value = Const.Request.JOB, consumes = Const.JSON,
      produces = Const.JSON)
  public ResponseEntity<?> saveJob(@Valid @RequestBody JobsDto jobsDto) {

    try {
      return ResponseEntity.ok(jobService.saveJob(jobsDto));
    } catch (Exception exp) {
      throw new BadRequestException(exp.getMessage());
    }

  }

  @Operation(
      summary = "Start Job",
      description = "Start Job",
      tags = "Job"
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "successful operation",
              content = @Content(
                  schema = @Schema(implementation = ResponseEntity.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Bad request",
              content = @Content(
                  schema = @Schema(implementation = ErrorDetails.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "not found",
              content = @Content(
                  schema = @Schema(implementation = ErrorDetails.class)
              )
          )
      }
  )
  @GetMapping(value = Const.Request.START_JOB, produces = Const.JSON)
  public ResponseEntity<?> startJob(@PathVariable(value = "id") UUID id) {

    try {
      jmsProducer.sendUpload(id);
      return ResponseEntity.ok("ok");
    } catch (Exception exp) {
      throw new BadRequestException(exp.getLocalizedMessage());
    }
  }

  @Operation(
      summary = "Delete a job",
      description = "Delete a job",
      tags = "Job"
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "successful operation",
              content = @Content(
                  schema = @Schema(implementation = ResponseEntity.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Bad request",
              content = @Content(
                  schema = @Schema(implementation = ErrorDetails.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "not found",
              content = @Content(
                  schema = @Schema(implementation = ErrorDetails.class)
              )
          )
      }
  )
  @GetMapping(value = Const.Request.DELETE_JOB, produces = Const.JSON)
  public ResponseEntity<?> deleteJob(@PathVariable(value = "id") UUID id) throws Exception {

    try {
      return ResponseEntity.ok(jobService.deleteJob(id));
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  @Operation(
      summary = "Start Download",
      description = "Start Download",
      tags = "Download"
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "successful",
              content = @Content(
                  schema = @Schema(implementation = ResponseEntity.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Bad request",
              content = @Content(
                  schema = @Schema(implementation = ErrorDetails.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "not found",
              content = @Content(
                  schema = @Schema(implementation = ErrorDetails.class)
              )
          )
      }
  )
  @GetMapping(value = Const.Request.START_DOWNLOAD, produces = Const.JSON)
  public ResponseEntity<?> startDownload(@PathVariable(value = "id") UUID id) {

    try {
      jmsProducer.sendDownload(id);
      return ResponseEntity.ok("ok");
    } catch (Exception exp) {
      throw new BadRequestException(exp.getLocalizedMessage());
    }
  }

  @GetMapping(value = "/stream-sse-mvc/{id}", produces = Const.JSON)
  public SseEmitter streamSseMvc(@PathVariable(value = "id") UUID id) {

    SseEmitter emitter = new SseEmitter(60 * 1000L);
    sses.put("id", emitter);
    return emitter;
  }

  @Bean
  IntegrationFlow inboundFlow(@Value("${input-dir:file://${HOME}/in}") File in) {

    return IntegrationFlows.from(Files.inboundAdapter(in).autoCreateDirectory(true),
        poller -> poller.poller(spec -> spec.fixedRate(1000L)))
        .transform(File.class, File::getAbsolutePath)
        .handle(String.class, (path, map) -> {
          sses.forEach((k, sse) -> {
            try {
              sse.send(path);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
          return null;
        })
        .get();
  }

}