package vn.vpgh.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.vpgh.jobhunter.domain.Company;
import vn.vpgh.jobhunter.domain.Job;
import vn.vpgh.jobhunter.domain.Skill;
import vn.vpgh.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.vpgh.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.repository.CompanyRepository;
import vn.vpgh.jobhunter.repository.JobRepository;
import vn.vpgh.jobhunter.repository.SkillRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public ResCreateJobDTO handleSaveJob(Job job) {
        // Check skills
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        if (job.getCompany() != null) {
            Optional<Company> optionalCompany = this.companyRepository.findById(job.getCompany().getId());
            if (optionalCompany.isPresent()) {
                job.setCompany(optionalCompany.get());
            }
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

    public ResUpdateJobDTO handleUpdateJob(Job reqJob) {
        Job job = this.getJobById(reqJob.getId());
        // Check skills
        if (reqJob.getSkills() != null) {
            List<Long> reqSkills = reqJob.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        if (reqJob.getCompany() != null) {
            Optional<Company> optionalCompany = this.companyRepository.findById(reqJob.getCompany().getId());
            if (optionalCompany.isPresent()) {
                job.setCompany(optionalCompany.get());
            }
        }

        job.setName(reqJob.getName());
        job.setSalary(reqJob.getSalary());
        job.setQuantity(reqJob.getQuantity());
        job.setLocation(reqJob.getLocation());
        job.setLevel(reqJob.getLevel());
        job.setStartDate(reqJob.getStartDate());
        job.setEndDate(reqJob.getEndDate());
        job.setActive(reqJob.isActive());

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
