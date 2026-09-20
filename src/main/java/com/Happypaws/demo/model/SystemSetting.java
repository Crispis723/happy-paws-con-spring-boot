package com.Happypaws.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_settings")
public class SystemSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Long idConfiguracion;

    @Column(name = "setting_key", nullable = false, unique = true, length = 100)
    private String settingKey;

    @Column(name = "setting_value", nullable = false, length = 255)
    private String settingValue;
    
    public SystemSetting() {}

    public SystemSetting(Long id, String settingKey, String settingValue) {
        this.idConfiguracion = id;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
    }


    public Long getIdConfiguracion() {
        return this.idConfiguracion;
    }

    public String getSettingKey() {
        return this.settingKey;
    }

    public String getSettingValue() {
        return this.settingValue;
    }

    public void setId(Long id) {
        this.idConfiguracion = id;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }


    public void setIdConfiguracion(Long id) { this.idConfiguracion = id; }

    /** Compatibilidad de API: el identificador persistido es idConfiguracion. */
    public Long getId() { return idConfiguracion; }
}