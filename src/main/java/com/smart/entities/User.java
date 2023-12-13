package com.smart.entities;

import com.smart.enums.AuthenticationProvider;
import com.smart.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User {
	
	@Id
	@GeneratedValue
	private Integer Id;
	private String name;
	@Email
	@Column(unique = true)
	private String email;
	private String password;
	@Enumerated(EnumType.STRING)
	private Role role;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "image_id")
	private Image image;
	@Column(length = 500)
	private String about;
	private boolean enabled;
	private String date;
	private boolean status;
	@Enumerated(EnumType.STRING)
	private AuthenticationProvider authProvider;
}
