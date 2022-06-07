package com.smart.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Messages {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int msg_id;
	
	private int incoming_msg_id;
	
	private int outgoing_msg_id;
	
	private String msg;

	public Messages(int incoming_msg_id, int outgoing_msg_id, String msg) {
		super();
		this.incoming_msg_id = incoming_msg_id;
		this.outgoing_msg_id = outgoing_msg_id;
		this.msg = msg;
	}

	public Messages() {
		super();
		// TODO Auto-generated constructor stub
	}



	public int getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(int msg_id) {
		this.msg_id = msg_id;
	}

	public int getIncoming_msg_id() {
		return incoming_msg_id;
	}

	public void setIncoming_msg_id(int incoming_msg_id) {
		this.incoming_msg_id = incoming_msg_id;
	}

	public int getOutgoing_msg_id() {
		return outgoing_msg_id;
	}

	public void setOutgoing_msg_id(int outgoing_msg_id) {
		this.outgoing_msg_id = outgoing_msg_id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "Messages [msg_id=" + msg_id + ", incoming_msg_id=" + incoming_msg_id + ", outgoing_msg_id="
				+ outgoing_msg_id + ", msg=" + msg + "]";
	}
}
