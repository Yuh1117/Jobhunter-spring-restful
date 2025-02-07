package vn.vpgh.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.vpgh.jobhunter.domain.Role;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.service.RoleService;
import vn.vpgh.jobhunter.util.annotation.ApiMessage;
import vn.vpgh.jobhunter.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createNewRole(@Valid @RequestBody Role reqRole) throws IdInvalidException {
        boolean isNameExist = this.roleService.isNameExist(reqRole.getName());
        if (isNameExist) {
            throw new IdInvalidException("Name already exists");
        }
        Role role = this.roleService.handleSaveRole(reqRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Get a role")
    public ResponseEntity<Role> getRole(@PathVariable("id") long id) throws IdInvalidException {
        Role role = this.roleService.getRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(role);
    }

    @GetMapping("/roles")
    @ApiMessage("Get all roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(@Filter Specification<Role> specification,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.getAllRoles(specification, pageable));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@RequestBody Role reqRole) throws IdInvalidException {
        Role role = this.roleService.getRoleById(reqRole.getId());
        if (role == null) {
            throw new IdInvalidException("Role not found");
        }

        // boolean isNameExist = this.roleService.isNameExist(reqRole.getName());
        // if (isNameExist) {
        // throw new IdInvalidException("Name already exists");
        // }

        return ResponseEntity.ok().body(this.roleService.handleUpdateRole(reqRole));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        Role role = this.roleService.getRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role not found");
        }
        this.roleService.deleteRoleById(id);
        return ResponseEntity.ok().build();
    }
}
