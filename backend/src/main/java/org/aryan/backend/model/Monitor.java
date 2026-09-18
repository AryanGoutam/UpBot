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

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;
    private boolean up;
    @Column(name = "status_code")
    private Integer statusCode;
    @Column(name = "response_time")
    private Long responseTime;
    @Column(name = "error_message")
    private String errorMessage;

}
