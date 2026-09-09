package com.Happypaws.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="permissions", uniqueConstraints=@UniqueConstraint(name="uk_permissions_code", columnNames="code"))
public class Permission {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name = "id_permiso")
    private Long idPermiso;
    @NotBlank @Column(nullable=false, unique=true, length=100) private String code;
    @NotBlank @Column(nullable=false, length=100) private String name;
    @Column(length=150) private String description;
    @NotBlank @Column(nullable=false, length=30) private String action;
    @Column(nullable=false) private Boolean enabled=true;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="id_modulo", nullable=false, foreignKey=@ForeignKey(name="fk_permissions_module"))
    private Module module;

    public Permission() {}
    public Permission(String code, String name, String description, String action, Module module) {
        this.code=code; this.name=name; this.description=description; this.action=action; this.module=module;
    }
    public Long getIdPermiso(){return idPermiso;} public String getCode(){return code;} public void setCode(String v){code=v;}
    public String getName(){return name;} public void setName(String v){name=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getAction(){return action;} public void setAction(String v){action=v;} public Boolean getEnabled(){return enabled;} public void setEnabled(Boolean v){enabled=v;}
    public Module getModule(){return module;} public void setModule(Module v){module=v;}

    public void setIdPermiso(Long id) { this.idPermiso = id; }

    /** Compatibilidad de API: el identificador persistido es idPermiso. */
    public Long getId() { return idPermiso; }
}
