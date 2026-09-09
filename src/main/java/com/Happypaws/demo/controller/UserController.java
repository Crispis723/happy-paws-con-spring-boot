package com.Happypaws.demo.controller;

import com.Happypaws.demo.dto.UserDTO;
import com.Happypaws.demo.model.Role;
import com.Happypaws.demo.model.User;
import com.Happypaws.demo.service.RoleService;
import com.Happypaws.demo.service.UserService;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("users", userService.listar());
        return "views/users/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", roleService.listar());
        return "views/users/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("user") UserDTO dto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, Authentication auth) {
        if (dto.getId() == null && (dto.getPassword() == null || dto.getPassword().isBlank())) {
            bindingResult.rejectValue("password", "required", "La contraseña es obligatoria para un usuario nuevo");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.listar());
            return "views/users/formulario";
        }
        try {
            if (dto.getId() == null) {
                userService.guardar(dto);
            } else {
                userService.actualizar(dto, auth.getName());
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("roles", roleService.listar());
            model.addAttribute("error", e.getMessage());
            return "views/users/formulario";
        }
        redirectAttributes.addFlashAttribute("success", "Usuario guardado correctamente");
        return "redirect:/usuarios";
    }

    @GetMapping("/edit/{id}")
    public String editar(@PathVariable Long id, Model model) {
        User user = userService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        UserDTO dto = new UserDTO();
        dto.setId(user.getIdUsuario());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.getEnabled());
        dto.setRoleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toList()));
        model.addAttribute("user", dto);
        model.addAttribute("roles", roleService.listar());
        return "views/users/formulario";
    }

    @PostMapping("/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        User target = userService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (target.getEmail().equals(auth.getName())) {
            redirectAttributes.addFlashAttribute("error", "No puedes eliminar tu propia cuenta");
            return "redirect:/usuarios";
        }
        userService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente");
        return "redirect:/usuarios";
    }
}
