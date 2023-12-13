package com.smart.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "_message")
public class Messages {

	@Id
	@GeneratedValue()
	private int msg_id;
	
	private int incoming;
	
	private int outgoing;
	
	private String msg;

	public Messages(int incoming, int outgoing, String msg) {
		this.incoming = incoming;
		this.outgoing = outgoing;
		this.msg = msg;
	}

}
