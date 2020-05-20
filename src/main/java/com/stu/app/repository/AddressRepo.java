package com.stu.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stu.app.model.Address;

@Repository
public interface AddressRepo  extends JpaRepository<Address, Integer>{

}
