package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Permission;
import com.Happypaws.demo.model.Role;
import com.Happypaws.demo.repository.PermissionRepository;
import com.Happypaws.demo.repository.RoleRepository;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {
    private static final Set<String> ROLES_DEL_SISTEMA = Set.of("ADMIN","VETERINARIO","RECEPCIONISTA","CLIENTE");
    private final RoleRepository repository; private final PermissionRepository permissionRepository;
    public RoleService(RoleRepository repository, PermissionRepository permissionRepository){this.repository=repository;this.permissionRepository=permissionRepository;}
    public List<Role> listar(){return repository.findAll();}
    @Transactional(readOnly=true) public Optional<Role> buscarPorId(Long id){return repository.findById(id);}
    @Transactional public Role guardar(Role role, Collection<Long> permissionIds){
        role.setPermissions(resolvePermissions(permissionIds));
        if(role.getEnabled()==null) role.setEnabled(true);
        return repository.save(role);
    }
    @Transactional public Role actualizar(Long id, String name, String description, Boolean enabled, Collection<Long> permissionIds){
        Role current=repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Rol no encontrado"));
        current.setName(name); current.setDescription(description); current.setEnabled(Boolean.TRUE.equals(enabled));
        current.setPermissions(resolvePermissions(permissionIds));
        return repository.save(current);
    }
    private Set<Permission> resolvePermissions(Collection<Long> ids){
        if(ids==null || ids.isEmpty()) return new LinkedHashSet<>();
        List<Permission> found = permissionRepository.findAllById(ids);
        if(found.size() != new HashSet<>(ids).size()) {
            throw new IllegalArgumentException("Uno o más permisos seleccionados no existen");
        }
        return new LinkedHashSet<>(found);
    }
    @Transactional public void eliminar(Long id){
        Role role=repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Rol no encontrado"));
        if(ROLES_DEL_SISTEMA.contains(role.getName())) throw new IllegalStateException("El rol "+role.getName()+" es un rol del sistema y no se puede eliminar");
        repository.delete(role);
    }
}
