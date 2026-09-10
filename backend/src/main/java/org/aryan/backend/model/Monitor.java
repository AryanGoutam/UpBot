package org.aryan.backend.model;

import jakarta.annotation.security.DenyAll;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Websites website;

    // one to many on websites db but not now

    private LocalDateTime checked_at;
    private boolean up;
    private Integer status_code;
    private Long response_time;
    private String error_message;

}
