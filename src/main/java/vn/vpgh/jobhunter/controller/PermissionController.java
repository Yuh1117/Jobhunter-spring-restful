package vn.vpgh.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.vpgh.jobhunter.domain.Permission;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.service.PermissionService;
import vn.vpgh.jobhunter.util.annotation.ApiMessage;
import vn.vpgh.jobhunter.util.error.IdInvalidException;

@Controller
@RequestMapping("/api/v0.1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createNewPermission(@Valid @RequestBody Permission reqPermission)
            throws IdInvalidException {
        boolean isPermissionExist = this.permissionService.isPermissionExist(reqPermission);
        if (isPermissionExist) {
            throw new IdInvalidException("Permission already exists");
        }
        Permission permission = this.permissionService.handleSavePermission(reqPermission);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    @GetMapping("/permissions")
    @ApiMessage("Get all permission")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions(@Filter Specification<Permission> specification,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.permissionService.getAllPermissions(specification, pageable));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@RequestBody Permission reqPermission) throws IdInvalidException {
        Permission permission = this.permissionService.getPermissionById(reqPermission.getId());
        if (permission == null) {
            throw new IdInvalidException("Permission not found");
        }

        boolean isPermissionExist = this.permissionService.isPermissionExist(reqPermission);
        if (isPermissionExist) {
            throw new IdInvalidException("Permission already exists");
        }

        return ResponseEntity.ok().body(this.permissionService.handleUpdatePermission(reqPermission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws IdInvalidException {
        Permission permission = this.permissionService.getPermissionById(id);
        if (permission == null) {
            throw new IdInvalidException("Permission not found");
        }
        this.permissionService.deletePermissionById(id);
        return ResponseEntity.ok().build();
    }

}
