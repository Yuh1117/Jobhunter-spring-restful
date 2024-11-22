package vn.vpgh.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.vpgh.jobhunter.domain.Job;
import vn.vpgh.jobhunter.domain.Skill;
import vn.vpgh.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.vpgh.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.repository.JobRepository;
import vn.vpgh.jobhunter.repository.SkillRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public ResCreateJobDTO handleSaveJob(Job job) {
        // Check skills
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        // Create job
        Job currentJob = this.jobRepository.save(job);

        // Convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills().stream().map(item -> item.getName()).collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }

    public Job getJobById(long id) {
        Optional<Job> optionalJob = this.jobRepository.findById(id);
        return optionalJob.isPresent() ? optionalJob.get() : null;
    }

    public ResultPaginationDTO getAllJob(Specification<Job> specification, Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Job> pageJob = this.jobRepository.findAll(specification, pageable);

        meta.setPage(pageJob.getNumber() + 1);
        meta.setPageSize(pageJob.getSize());
        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        res.setMeta(meta);
        res.setResult(pageJob.getContent());

        return res;
    }

    public ResUpdateJobDTO handleUpdateJob(Job job) {
        // Check skills
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        // Update job
        Job currentJob = this.jobRepository.save(job);

        // Convert response
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills().stream().map(item -> item.getName()).collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }

    public void deleteJobById(long id) {
        this.jobRepository.deleteById(id);
    }

}
