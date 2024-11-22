package vn.vpgh.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.vpgh.jobhunter.domain.Skill;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.repository.SkillRepository;

import java.util.Optional;

@Service
public class SkillService {
    private SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleSaveSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill getSkillById(long id) {
        Optional<Skill> optionalSkill = this.skillRepository.findById(id);
        return optionalSkill.isPresent() ? optionalSkill.get() : null;
    }

    public ResultPaginationDTO getAllSkills(Specification<Skill> specification, Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Skill> pageSkill = this.skillRepository.findAll(specification, pageable);

        meta.setPage(pageSkill.getNumber() + 1);
        meta.setPageSize(pageSkill.getSize());
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        res.setMeta(meta);
        res.setResult(pageSkill.getContent());
        return res;
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill handleUpdateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public void deleteSkillById(long id) {
        // Delete job in job_skill table
        Skill skill = this.skillRepository.findById(id).get();
        skill.getJobs().forEach(job -> job.getSkills().remove(skill));

        this.skillRepository.deleteById(id);
    }
}
