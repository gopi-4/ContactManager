package com.smart.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "_contact")
public class Contact {
	
	@Id
	@GeneratedValue()
	private Integer Id;
	private String name;
	@Email
	private String email;
	private String phone;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "image_id")
	private Image image;
	@Column(length = 5000)
	private String description;
	@JoinColumn(name = "user_id")
	private Integer userId;
	private boolean status;
	private boolean isRegister;
	private int position;

	@Override
	public int hashCode() {
		return Objects.hash(this.email);
	}
}
