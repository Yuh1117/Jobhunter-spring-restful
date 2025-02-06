package vn.vpgh.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.vpgh.jobhunter.domain.Company;
import vn.vpgh.jobhunter.domain.Job;
import vn.vpgh.jobhunter.domain.Resume;
import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.vpgh.jobhunter.domain.response.resume.ResResumeDTO;
import vn.vpgh.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.vpgh.jobhunter.service.ResumeService;
import vn.vpgh.jobhunter.service.UserService;
import vn.vpgh.jobhunter.util.SecurityUtil;
import vn.vpgh.jobhunter.util.annotation.ApiMessage;
import vn.vpgh.jobhunter.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v0.1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;

    private final FilterSpecificationConverter filterSpecificationConverter;
    private final FilterBuilder filterBuilder;

    public ResumeController(ResumeService resumeService, UserService userService,
                            FilterSpecificationConverter filterSpecificationConverter, FilterBuilder filterBuilder) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.filterBuilder = filterBuilder;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> createNewResume(@Valid @RequestBody Resume reqResume)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.handleSaveResume(reqResume));
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get a resume")
    public ResponseEntity<ResResumeDTO> getResume(@PathVariable("id") long id) throws IdInvalidException {
        Resume resume = this.resumeService.getResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Resume not found");
        }
        return ResponseEntity.ok().body(this.resumeService.convertToResResumeDTO(resume));
    }

    @GetMapping("/resumes")
    @ApiMessage("Get all resumes")
    public ResponseEntity<ResultPaginationDTO> getAllResumes(@Filter Specification<Resume> specification,
                                                             Pageable pageable) {
        List<Long> jobIds = new ArrayList<>();

        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUser = this.userService.getUserByEmail(email);
        if (currentUser != null) {
            Company company = currentUser.getCompany();
            if (company != null) {
                List<Job> companyJobs = company.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    jobIds = companyJobs.stream().map(j -> j.getId()).collect(Collectors.toList());
                }
            }
        }

        List<Long> finalJobIds = jobIds;
        Specification<Resume> joinSpec = (root, query, criteriaBuilder) -> {
            return criteriaBuilder.in(root.get("job").get("id")).value(finalJobIds);
        };

        Specification<Resume> finalSpec = joinSpec.and(specification);


        return ResponseEntity.ok().body(this.resumeService.getAllResume(finalSpec, pageable));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume reqResume) throws IdInvalidException {
        Resume resume = this.resumeService.handleUpdateResume(reqResume);
        if (resume == null) {
            throw new IdInvalidException("Resume not found");
        }
        return ResponseEntity.ok().body(this.resumeService.convertToResUpdateDTO(resume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        Resume resume = this.resumeService.getResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Resume not found");
        }
        this.resumeService.deleteResumeById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resumes/by-user")
    public ResponseEntity<ResultPaginationDTO> getResumesByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.getResumesByUser(pageable));
    }

}
