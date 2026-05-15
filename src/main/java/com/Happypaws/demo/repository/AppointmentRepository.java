package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;   

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
}
