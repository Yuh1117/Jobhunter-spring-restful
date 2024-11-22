package vn.vpgh.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.vpgh.jobhunter.domain.Job;
import vn.vpgh.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.vpgh.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.service.JobService;
import vn.vpgh.jobhunter.util.annotation.ApiMessage;
import vn.vpgh.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v0.1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> createNewJob(@Valid @RequestBody Job reqJob) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleSaveJob(reqJob));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get a job")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) throws IdInvalidException {
        Job job = this.jobService.getJobById(id);
        if (job == null) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(job);
    }

    @GetMapping("/jobs")
    @ApiMessage("Get all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(@Filter Specification<Job> specification,
                                                          Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.getAllJob(specification, pageable));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job reqJob) throws IdInvalidException {
        Job job = this.jobService.getJobById(reqJob.getId());
        if (job == null) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.handleUpdateJob(reqJob));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long id) throws IdInvalidException {
        Job job = this.jobService.getJobById(id);
        if (job == null) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.deleteJobById(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
