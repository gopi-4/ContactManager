package com.smart.repository;

import com.smart.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer>{

	@Transactional
	@Query("from Message as m where ((m.outgoing =:outgoing_id) AND (m.incoming=:incoming_id)) OR ((m.outgoing =:incoming_id) AND (m.incoming=:outgoing_id))")
	List<Message> findMessages(@Param("outgoing_id") Integer outgoing_id, @Param("incoming_id") Integer incoming_id);
}
