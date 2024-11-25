package vn.vpgh.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.vpgh.jobhunter.domain.Job;
import vn.vpgh.jobhunter.domain.Resume;
import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.vpgh.jobhunter.domain.response.resume.ResResumeDTO;
import vn.vpgh.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.vpgh.jobhunter.repository.JobRepository;
import vn.vpgh.jobhunter.repository.ResumeRepository;
import vn.vpgh.jobhunter.repository.UserRepository;
import vn.vpgh.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ResumeService(ResumeRepository resumeRepository, JobRepository jobRepository, UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public Resume getResumeById(long id) {
        Optional<Resume> optionalResume = this.resumeRepository.findById(id);
        return optionalResume.isPresent() ? optionalResume.get() : null;
    }

    public ResCreateResumeDTO handleSaveResume(Resume resume) throws IdInvalidException {
        //Check exist
        if (resume.getUser() != null) {
            Optional<User> optionalUser = this.userRepository.findById(resume.getUser().getId());
            User user = optionalUser.isPresent() ? optionalUser.get() : null;
            if (user == null) {
                throw new IdInvalidException("User not found");
            }
        }

        if (resume.getJob() != null) {
            Optional<Job> optionalJob = this.jobRepository.findById(resume.getJob().getId());
            Job job = optionalJob.isPresent() ? optionalJob.get() : null;
            if (job == null) {
                throw new IdInvalidException("Job not found");
            }
        }

        Resume currentResume = this.resumeRepository.save(resume);

        ResCreateResumeDTO resCreateResumeDTO = new ResCreateResumeDTO();
        resCreateResumeDTO.setId(currentResume.getId());
        resCreateResumeDTO.setCreatedAt(currentResume.getCreatedAt());
        resCreateResumeDTO.setCreatedBy(currentResume.getCreatedBy());

        return resCreateResumeDTO;
    }

    public ResResumeDTO convertToResResumeDTO(Resume resume) {
        ResResumeDTO resResumeDTO = new ResResumeDTO();

        resResumeDTO.setId(resume.getId());
        resResumeDTO.setEmail(resume.getEmail());
        resResumeDTO.setUrl(resume.getUrl());
        resResumeDTO.setStatus(resume.getStatus());
        resResumeDTO.setCreatedAt(resume.getCreatedAt());
        resResumeDTO.setUpdatedAt(resume.getUpdatedAt());
        resResumeDTO.setCreatedBy(resume.getCreatedBy());
        resResumeDTO.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getUser() != null) {
            resResumeDTO.setUser(new ResResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        }

        if (resume.getJob() != null) {
            resResumeDTO.setJob(new ResResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
            resResumeDTO.setCompanyName(resume.getJob().getCompany().getName());
        }

        return resResumeDTO;
    }

    public ResultPaginationDTO getAllResume(Specification<Resume> specification, Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Resume> pageResume = this.resumeRepository.findAll(specification, pageable);

        meta.setPage(pageResume.getNumber() + 1);
        meta.setPageSize(pageResume.getSize());
        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getTotalElements());

        List<ResResumeDTO> resumes = pageResume.getContent().stream().map(item -> convertToResResumeDTO(item)).collect(Collectors.toList());

        res.setMeta(meta);
        res.setResult(resumes);

        return res;
    }

    public Resume handleUpdateResume(Resume reqResume) {
        Resume resume = this.getResumeById(reqResume.getId());
        if (resume != null) {
            if (reqResume.getStatus() != null) {
                resume.setStatus(reqResume.getStatus());
                resume = this.resumeRepository.save(resume);
            }
            return resume;
        }
        return null;
    }

    public ResUpdateResumeDTO convertToResUpdateDTO(Resume resume) {
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdateBy(resume.getUpdatedBy());
        return res;
    }

    public void deleteResumeById(long id) {
        this.resumeRepository.deleteById(id);
    }
}
