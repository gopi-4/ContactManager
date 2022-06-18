package com.smart.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.smart.entities.Messages;

public interface MessageRepository extends JpaRepository<Messages, Integer>{

	@Transactional
	@Query("from Messages as m where ((m.outgoing_msg_id =:outgoing_id) AND (m.incoming_msg_id=:incoming_id)) OR ((m.outgoing_msg_id =:incoming_id) AND (m.incoming_msg_id=:outgoing_id))")
	public List<Messages> findMessages(@Param("outgoing_id") Integer outgoing_id, @Param("incoming_id") Integer incoming_id);
}
