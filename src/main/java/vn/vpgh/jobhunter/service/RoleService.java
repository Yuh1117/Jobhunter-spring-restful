package vn.vpgh.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.vpgh.jobhunter.domain.Permission;
import vn.vpgh.jobhunter.domain.Role;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.repository.PermissionRepository;
import vn.vpgh.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean isNameExist(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role handleSaveRole(Role reqRole) {
        if (reqRole.getPermissions() != null) {
            List<Long> id = reqRole.getPermissions().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Permission> permissions = this.permissionRepository.findByIdIn(id);
            reqRole.setPermissions(permissions);
        }

        return this.roleRepository.save(reqRole);
    }

    public ResultPaginationDTO getAllRoles(Specification<Role> specification, Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Role> pageRole = this.roleRepository.findAll(specification, pageable);

        meta.setPage(pageRole.getNumber() + 1);
        meta.setPageSize(pageRole.getSize());
        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());

        res.setMeta(meta);
        res.setResult(pageRole.getContent());

        return res;
    }

    public Role getRoleById(long id) {
        Optional<Role> optionalRole = this.roleRepository.findById(id);
        return optionalRole.isPresent() ? optionalRole.get() : null;
    }

    public Role handleUpdateRole(Role reqRole) {
        Role role = this.getRoleById(reqRole.getId());

        role.setName(reqRole.getName());
        role.setDescription(reqRole.getDescription());
        role.setActive(reqRole.isActive());
        if (reqRole.getPermissions() != null) {
            List<Long> id = reqRole.getPermissions().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Permission> permissions = this.permissionRepository.findByIdIn(id);
            role.setPermissions(permissions);
        }
        role = this.roleRepository.save(role);
        return role;
    }

    public void deleteRoleById(long id) {
        this.roleRepository.deleteById(id);
    }

}
