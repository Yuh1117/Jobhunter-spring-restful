package vn.vpgh.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.vpgh.jobhunter.domain.Permission;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission reqPermission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(reqPermission.getModule(),
                reqPermission.getApiPath(), reqPermission.getMethod());
    }

    public Permission handleSavePermission(Permission reqPermission) {
        return this.permissionRepository.save(reqPermission);
    }

    public ResultPaginationDTO getAllPermissions(Specification<Permission> specification, Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Permission> pagePermission = this.permissionRepository.findAll(specification, pageable);

        meta.setPage(pagePermission.getNumber() + 1);
        meta.setPageSize(pagePermission.getSize());
        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());

        res.setMeta(meta);
        res.setResult(pagePermission.getContent());

        return res;
    }

    public Permission getPermissionById(long id) {
        Optional<Permission> optionalPermission = this.permissionRepository.findById(id);
        return optionalPermission.isPresent() ? optionalPermission.get() : null;
    }

    public Permission handleUpdatePermission(Permission reqPermission) {
        Permission permission = this.getPermissionById(reqPermission.getId());
        permission.setName(reqPermission.getName());
        permission.setApiPath(reqPermission.getApiPath());
        permission.setModule(reqPermission.getModule());
        permission.setMethod(reqPermission.getMethod());

        permission = this.permissionRepository.save(permission);
        return permission;
    }

    public void deletePermissionById(long id) {
        Permission permission = this.getPermissionById(id);
        permission.getRoles().forEach(role -> role.getPermissions().remove(permission));

        this.permissionRepository.deleteById(id);
    }

    public boolean isSameName(Permission reqPermission) {
        Permission permission = this.getPermissionById(reqPermission.getId());
        if (permission.getName().equals(reqPermission.getName())) {
            return true;
        }
        return false;
    }

}
