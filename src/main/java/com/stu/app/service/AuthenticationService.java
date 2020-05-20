package com.stu.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.stu.app.config.Constants;
import com.stu.app.dto.SignInResponseDTO;
import com.stu.app.dto.SignupDTO;
import com.stu.app.dto.UserCredentialsDTO;
import com.stu.app.dto.UserDTO;
import com.stu.app.exceptions.AccountsRTException;
import com.stu.app.model.Address;
import com.stu.app.model.Users;
import com.stu.app.repository.AddressRepo;
import com.stu.app.repository.CourseRepo;
import com.stu.app.repository.StudentRepo;
import com.stu.app.repository.UsersRepo;
import com.stu.app.security.TokenProvider;

@Service
@Log4j2
public class AuthenticationService {
	@Autowired
	UsersRepo usersRepo;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	TokenProvider tokenProvider;
	@Autowired
	StudentRepo studentRepo;
	@Autowired
	AddressRepo addressRepo;
	@Autowired
	CourseRepo courseRepo;
	
	@Autowired
	NotificationService notification;
	
	public SignInResponseDTO userSignin(
			@Valid UserCredentialsDTO userCredentialsDTO,
			HttpServletRequest request) {
		log.info("#*****AuthenticationService.userSignin()*****start*****#");

		SignInResponseDTO signinResponseDTO = new SignInResponseDTO();
		Users user = null;
		if (userCredentialsDTO.getUsername() != null) {
			String emailId = userCredentialsDTO.getUsername();

			user = usersRepo.existsByEmail(emailId) ? usersRepo
					.findByEmail(emailId) : null;
			if (user == null)
				throw new AccountsRTException(HttpStatus.BAD_REQUEST,
						"EMAIL_NOT_EXIST");
		}
		Authentication authentication;
		String password = userCredentialsDTO.getPassword();

		if (passwordEncoder.matches(password, user.getPassword())) {
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user
							.getEmail(), password));
		} else {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST, "WRONG_PASSWORD");
		}
		// storing in redis
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwtToken = getToken(authentication, user);

		signinResponseDTO.setAccessToken(jwtToken);
		signinResponseDTO.setTokenType("Bearer");
		signinResponseDTO.setUserType(user.getType());
		return signinResponseDTO;
	}

	/**
	 * @param authentication
	 *            - to get the user principal
	 * @param userId
	 *            - is used as key to store the user token
	 * @return token as response
	 */
	public String getToken(Authentication authentication, Users user) {
		log.info("#*****authenticationservice.getToken*****start*****#");
		String jwtToken = null;
		try {
			jwtToken = tokenProvider.generateToken(authentication, user);
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return jwtToken;
	}


	private Users getUserObject(SignupDTO postDTO) {
		Users users = new Users();
		try {
			Address addr = new Address();
			BeanUtils.copyProperties(addr, postDTO);
			BeanUtils.copyProperties(users, postDTO);
			addressRepo.save(addr);
			users.setAddress(addr);
			
			users.setType(postDTO.getUsertype()); //Constants.User.FACULTY
			users.setPassword(passwordEncoder.encode(postDTO.getSecretkey()));
			
			users.setCreateDtm(new Date());
		} catch (Exception e) {
			log.error(e);
		}
		return users;

	}

	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object createUser(@Valid SignupDTO postDTO,
			HttpServletRequest request) {
		Users user = usersRepo.findByEmail(postDTO.getEmail());
		if (user != null) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.UserExists.toString());
		}
		try{
			boolean sendInvite = false;
			if(postDTO.getSecretkey()==null){
				sendInvite = true;
				postDTO.setSecretkey("password");
			}
			Users users = getUserObject(postDTO);
			usersRepo.save(users);
			if(sendInvite){
				//send Invitation mial to faculty from administrator
				notification.sendMail(users.getEmail(), "Signup Verification", "Account creation for you is done, \nYou need to verify your email "
					+ "by providing below opt \n OPT: 9898\n\n Thanks, Support Team");
			}else{
				// send confirmation mail verification as from signup
				notification.sendMail(users.getEmail(), "Signup Verification", "Your signup process completed, \nPlease verify your email "
						+ "by providing below opt \n OPT: 9898\n\n Thanks, Support Team");
			}
			return Constants.Response.OK;
		
		}catch(Exception e){
			log.error(e);
			return Constants.Response.ERROR;
		}
	}

	/**
	 * @param request
	 * @return
	 */
	public List<UserDTO> getUsers(String type, HttpServletRequest request) {
		List<UserDTO> users = new ArrayList<>();
		List<Users> dbUsers = null;
		if(type == null){
			dbUsers = usersRepo.findAll();
		}else{
			dbUsers = usersRepo.findAllByType(type);
		}
		dbUsers.forEach(a->{
				users.add(getUserDTO(a));
			});
			return users;
	}

	private UserDTO getUserDTO(Users a) {
		UserDTO udto = new UserDTO();
		
		try{
			BeanUtils.copyProperties(udto, a);
			BeanUtils.copyProperties(udto, a.getAddress());
			udto.setKey(a.getId());
			udto.setType(null);
			
		}catch(Exception e){
			log.error(e);
		}
		return udto;
	}

	public Object updateUser(@Valid UserDTO fData,
			HttpServletRequest request) {
		if(fData.getKey()!=null && fData.getKey() != 0){
			Users f =  usersRepo.findById(fData.getKey()).get();
			if( f==null)
				throw new AccountsRTException(HttpStatus.BAD_REQUEST,
						"UserDoesNotExists");
			Address addr = f.getAddress();
			try{
				fData.setType(f.getType());
				BeanUtils.copyProperties(f, fData);
				BeanUtils.copyProperties(addr, fData);
				addressRepo.save(addr);
				f.setUpdateDtm(new Date());
				
				usersRepo.save(f);
			}catch(Exception e){
				log.error(e);
			}
		}else{
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"InvalidData");
		}
		return Constants.Response.OK;
	}

	public Object getUserById(Integer uid, HttpServletRequest request) {
		if(uid !=null && uid != 0){
			Users user =  usersRepo.findById(uid).get();
			if( user==null)
					throw new AccountsRTException(HttpStatus.BAD_REQUEST,
							"UserDoesNotExists");
			UserDTO dto = getUserDTO(user);
			return dto;
		}else{
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"InvalidData");
		}
	}
	
}
