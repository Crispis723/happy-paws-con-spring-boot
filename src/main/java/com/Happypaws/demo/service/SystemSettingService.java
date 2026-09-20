package com.Happypaws.demo.service;

import com.Happypaws.demo.model.SystemSetting;
import com.Happypaws.demo.repository.SystemSettingRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class SystemSettingService {

    public static final String CITA_PRECIO_KEY = "cita.precio";
    private static final BigDecimal DEFAULT_CITA_PRECIO = new BigDecimal("50.00");

    private final SystemSettingRepository repository;

    public SystemSettingService(SystemSettingRepository repository) {
        this.repository = repository;
    }

    public BigDecimal obtenerPrecioCita() {
        return repository.findBySettingKey(CITA_PRECIO_KEY)
                .map(setting -> new BigDecimal(setting.getSettingValue()))
                .orElse(DEFAULT_CITA_PRECIO);
    }

    public BigDecimal guardarPrecioCita(BigDecimal precio) {
        BigDecimal valor = precio == null ? DEFAULT_CITA_PRECIO : precio;
        SystemSetting setting = repository.findBySettingKey(CITA_PRECIO_KEY)
                .orElseGet(() -> new SystemSetting(null, CITA_PRECIO_KEY, valor.toPlainString()));
        setting.setSettingValue(valor.toPlainString());
        repository.save(setting);
        return valor;
    }
}