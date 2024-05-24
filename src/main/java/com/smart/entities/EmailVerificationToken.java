package com.smart.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "_emailverificationtoken")
public class EmailVerificationToken {

    @Id
    @GeneratedValue()
    private Integer Id;
    private String token;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "_user_id")
    private User user;

    public EmailVerificationToken(User user) {
        this.user = user;
        this.createdDate = new Date();
        this.token = UUID.randomUUID().toString();
    }
}
