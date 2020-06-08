package com.stu.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stu.app.model.Subject;

@Repository
public interface SubjectRepo extends JpaRepository<Subject, Integer>{

	Subject findByName(String s);


}
 