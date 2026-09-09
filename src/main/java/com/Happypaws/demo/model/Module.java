package com.Happypaws.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "modules")
public class Module {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo")
    private Long idModulo;
    @NotBlank @Column(nullable=false, unique=true, length=50)
    private String code;
    @NotBlank @Column(nullable=false, length=100)
    private String name;
    @Column(length=150) private String description;
    @Column(length=100) private String icon;
    @Column(length=255) private String route;
    @Column(nullable=false) private Boolean enabled = true;
    @Column(name="display_order", nullable=false) private Integer displayOrder = 0;

    @OneToMany(mappedBy="module", fetch=FetchType.LAZY)
    @OrderBy("action ASC, name ASC")
    private Set<Permission> permissions = new LinkedHashSet<>();

    public Module() {}
    public Module(String code, String name, String description, String icon, String route, int displayOrder) {
        this.code=code; this.name=name; this.description=description; this.icon=icon; this.route=route; this.displayOrder=displayOrder;
    }
    public Long getIdModulo(){return idModulo;} public String getCode(){return code;} public void setCode(String v){code=v;}
    public String getName(){return name;} public void setName(String v){name=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getIcon(){return icon;} public void setIcon(String v){icon=v;} public String getRoute(){return route;} public void setRoute(String v){route=v;}
    public Boolean getEnabled(){return enabled;} public void setEnabled(Boolean v){enabled=v;} public Integer getDisplayOrder(){return displayOrder;} public void setDisplayOrder(Integer v){displayOrder=v;}
    public Set<Permission> getPermissions(){return permissions;}

    public void setIdModulo(Long id) { this.idModulo = id; }

    /** Compatibilidad de API: el identificador persistido es idModulo. */
    public Long getId() { return idModulo; }
}
