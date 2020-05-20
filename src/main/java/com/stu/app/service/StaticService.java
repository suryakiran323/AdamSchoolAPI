package com.stu.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stu.app.model.Course;
import com.stu.app.repository.CourseRepo;
import com.stu.app.repository.LocationRepo;

@Service
public class StaticService {

	@Autowired
	LocationRepo locationRepo;
	
	@Autowired
	CourseRepo courseRepo;

	public List<String> getCountries(String kw){
		return locationRepo.getCountriesMatch(kw+'%');
	}
	
	public List<String> getStates(String country, String kw){
		return locationRepo.getStatesMatch(country, kw+'%');
	}
	
	public List<String> getCities(String country,String state, String kw){
		return locationRepo.getCities(country, state, kw+'%');
	}

	public List<String> getCourses(String keyword) {
		return courseRepo.getCourses(keyword+'%'); 
	}

	public List<Course> getCourseInfo(String keyword) {
		return courseRepo.findByNameContaining(keyword);
	}

}
