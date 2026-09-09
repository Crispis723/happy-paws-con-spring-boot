package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Permission;
import com.Happypaws.demo.model.Role;
import com.Happypaws.demo.repository.PermissionRepository;
import com.Happypaws.demo.service.RoleService;
import com.Happypaws.demo.service.UserService;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService; private final UserService userService; private final PermissionRepository permissionRepository;
    public RoleController(RoleService roleService, UserService userService, PermissionRepository permissionRepository){this.roleService=roleService;this.userService=userService;this.permissionRepository=permissionRepository;}

    @GetMapping @PreAuthorize("hasAnyAuthority('ROLES_VER', 'ROLE_ADMIN')")
    public String listar(Model model){
        List<Role> roles=roleService.listar(); model.addAttribute("roles",roles); model.addAttribute("roleViews",roles.stream().map(this::toView).toList()); return "views/roles/index";
    }
    @GetMapping("/nuevo") @PreAuthorize("hasAnyAuthority('ROLES_CREAR', 'ROLE_ADMIN')")
    public String nuevo(Model model){prepare(model,new Role(),"create"); return "views/roles/action";}
    @GetMapping({"/edit/{id}","/editar/{id}"}) @PreAuthorize("hasAnyAuthority('ROLES_EDITAR', 'ROLE_ADMIN')")
    public String editar(@PathVariable Long id, Model model){Role role=roleService.buscarPorId(id).orElseThrow(()->new IllegalArgumentException("Rol no encontrado")); prepare(model,role,"edit"); return "views/roles/action";}
    @PostMapping("/guardar") @PreAuthorize("hasAnyAuthority('ROLES_CREAR', 'ROLES_EDITAR', 'ROLE_ADMIN')")
    public String guardar(@Valid @ModelAttribute("role") Role role, BindingResult br, @RequestParam(name="permissionIds",required=false) Set<Long> permissionIds, Model model, RedirectAttributes ra){
        if(br.hasErrors()){prepare(model,role,role.getIdRol()==null?"create":"edit"); return "views/roles/action";}
        if(role.getIdRol()==null) roleService.guardar(role,permissionIds); else roleService.actualizar(role.getIdRol(),role.getName(),role.getDescription(),role.getEnabled(),permissionIds);
        ra.addFlashAttribute("success","Rol y permisos guardados correctamente"); return "redirect:/roles";
    }
    @PostMapping({"/delete/{id}","/eliminar/{id}"}) @PreAuthorize("hasAnyAuthority('ROLES_ELIMINAR', 'ROLE_ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra){roleService.eliminar(id);ra.addFlashAttribute("success","Rol eliminado correctamente");return "redirect:/roles";}
    @GetMapping({"/asignar", "/asignar/"}) @PreAuthorize("hasAnyAuthority('ROLES_VER','ROLES_ASIGNAR','ROLE_ADMIN')")
    public String asignarRolGet(){
        // Evita un 404 si el navegador intenta abrir directamente la URL de asignación.
        // La asignación real se realiza exclusivamente mediante POST.
        return "redirect:/roles";
    }

    @PostMapping({"/asignar", "/asignar/"}) @PreAuthorize("hasAnyAuthority('ROLES_ASIGNAR','ROLE_ADMIN')")
    public String asignarRol(@RequestParam String email,@RequestParam Long roleId,RedirectAttributes ra){
        userService.asignarRolAEmail(email,roleId);
        ra.addFlashAttribute("success","Rol asignado correctamente al usuario");
        return "redirect:/roles";
    }

    private void prepare(Model model,Role role,String mode){model.addAttribute("role",role);model.addAttribute("mode",mode);try { model.addAttribute("permissions",permissionRepository.findAllByOrderByModule_DisplayOrderAscActionAscNameAsc()); } catch (RuntimeException ex) { model.addAttribute("permissions",permissionRepository.findAll()); }model.addAttribute("selectedPermissionIds",role.getPermissions()==null?Set.of():role.getPermissions().stream().map(Permission::getId).collect(java.util.stream.Collectors.toSet()));}
    private RoleView toView(Role role){String n=role.getName();String badge=switch(n){case "ADMIN"->"role-badge--danger";case "VETERINARIO"->"role-badge--primary";case "RECEPCIONISTA"->"role-badge--success";case "CLIENTE"->"role-badge--info";default->"role-badge--neutral";};String icon=switch(n){case "ADMIN"->"bi-shield-lock";case "VETERINARIO"->"bi-heart-pulse";case "RECEPCIONISTA"->"bi-telephone";case "CLIENTE"->"bi-person";default->"bi-diagram-3";};String desc=role.getDescription()!=null?role.getDescription():"Rol personalizado del sistema.";String color=switch(n){case "ADMIN"->"#dc3545";case "VETERINARIO"->"#0d6efd";case "RECEPCIONISTA"->"#198754";case "CLIENTE"->"#0dcaf0";default->"#6c757d";}; return new RoleView(role.getIdRol(),n,desc,badge,icon,n,color);}
    private record RoleView(Long id,String name,String description,String badgeClass,String icon,String scope,String color){}
}
