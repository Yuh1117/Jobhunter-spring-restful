package vn.vpgh.jobhunter.controller;

import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.vpgh.jobhunter.domain.Skill;
import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.domain.response.ResUpdateUserDTO;
import vn.vpgh.jobhunter.domain.response.ResUserDTO;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.service.SkillService;
import vn.vpgh.jobhunter.util.annotation.ApiMessage;
import vn.vpgh.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v0.1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create a skill")
    public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill reqSkill) throws IdInvalidException {
        boolean isNameExist = this.skillService.isNameExist(reqSkill.getName());
        if (isNameExist) {
            throw new IdInvalidException("Name already exists");
        }
        Skill skill = this.skillService.handleSaveSkill(reqSkill);
        return ResponseEntity.status(HttpStatus.OK).body(skill);
    }

    @GetMapping("/skills/{id}")
    @ApiMessage("Get a skill")
    public ResponseEntity<Skill> getSkill(@PathVariable("id") long id) throws IdInvalidException {
        Skill skill = this.skillService.getSkillById(id);
        if (skill == null) {
            throw new IdInvalidException("There is no skill with id " + id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(skill);
    }

    @GetMapping("/skills")
    @ApiMessage("Get all skills")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(@Filter Specification<Skill> specification,
                                                           Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.getAllSkills(specification, pageable));
    }

    @PutMapping("/skills")
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> updateUser(@RequestBody Skill reqSkill) throws IdInvalidException {
        Skill skill = this.skillService.getSkillById(reqSkill.getId());
        if (skill == null) {
            throw new IdInvalidException("There is no skill with id " + reqSkill.getId());
        }

        if (reqSkill.getName() != null) {
            if (this.skillService.isNameExist(reqSkill.getName()))
                throw new IdInvalidException("Name already exists");
            skill.setName(reqSkill.getName());
            this.skillService.handleUpdateSkill(skill);
        }
        return ResponseEntity.status(HttpStatus.OK).body(skill);
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        Skill skill = this.skillService.getSkillById(id);
        if (skill == null) {
            throw new IdInvalidException("There is no skill with id " + id);
        }
        this.skillService.deleteSkillById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
