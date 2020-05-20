package com.stu.app.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stu.app.model.Users;
import com.stu.app.repository.UsersRepo;

@Service
public class UserRepoService implements UserDetailsService {

    final
    UsersRepo userRepository;

    public UserRepoService(UsersRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(usernameOrEmail);
        		
        if(user ==null ) 
        	throw new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail);

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Integer id) {
        Users user = userRepository.findById(id).get();
        if(user ==null ) 
        	throw new UsernameNotFoundException("User not found with id : " + id);

        return UserPrincipal.create(user);
    }
}
