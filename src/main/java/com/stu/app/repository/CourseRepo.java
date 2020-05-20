package com.stu.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stu.app.model.Course;

@Repository
public interface CourseRepo extends JpaRepository<Course, Integer>{

	@Query("SELECT DISTINCT p.name FROM Course p WHERE p.name like :kw order by name")
	List<String> getCourses(@Param("kw") String keyword);

	List<Course> findByNameContaining(String keyword);

}
 