package com.stu.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stu.app.model.Location;

@Repository
public interface LocationRepo extends JpaRepository<Location, Integer>{
	
	@Query("SELECT DISTINCT p.country FROM Location p WHERE p.country like :kw order by country")
	List<String> getCountriesMatch(@Param("kw") String kw);
	
	@Query("SELECT DISTINCT p.state FROM Location p WHERE p.country=:country and p.state like :kw order by state")
	List<String> getStatesMatch(@Param("country") String country, @Param("kw") String kw);
	
	@Query("SELECT DISTINCT p.city FROM Location p WHERE p.country=:country and p.state=:state and p.city like :kw order by city")
	List<String> getCities(@Param("country") String country,@Param("state")String state, @Param("kw") String kw);
	
	
}
