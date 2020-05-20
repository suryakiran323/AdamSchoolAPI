package com.stu.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stu.app.model.Documents;

@Repository
public interface DocumentsRepo extends JpaRepository<Documents, Integer>{

}
