package com.stu.app.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.stu.app.dto.MsgDTO;
import com.stu.app.exceptions.AccountsRTException;
import com.stu.app.model.Messages;
import com.stu.app.model.Users;
import com.stu.app.repository.MessagesRepo;
import com.stu.app.repository.UsersRepo;
import com.stu.app.util.AppResponse;

@Service
@Log4j2
public class MsgService {
	
	@Autowired
	UsersRepo usersRepo;
	@Autowired
	MessagesRepo msgRepo;
	
	/**
	 * @param postDTO
	 * @param request
	 * @return
	 */
	public Object sendMsg(@Valid MsgDTO postDTO, HttpServletRequest request) {
		Optional<Users> fromUser = usersRepo.findById(postDTO.getFromUserId());
		if(!fromUser.isPresent()){
			log.error("From User Id not present in the input");
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"From User Id not present");
		}
		Optional<Users> toUser = usersRepo.findById(postDTO.getToUserId());
		if(!toUser.isPresent()){
			log.error("To User Id not present in the input");
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"To User Id not present");
		}
		Messages msg = new Messages();
		msg.setCreateDtm(new Date());
		msg.setFromUserId(fromUser.get());
		msg.setToUserId(toUser.get());
		msg.setMessage(postDTO.getMessage());
		msg.setViewInd(Boolean.FALSE);
		msgRepo.save(msg);
		return AppResponse.SUCCESS;
	}

	public Object  getMessages(HttpServletRequest request) {
		Integer userId = (Integer)request.getAttribute("userId");
		Boolean unread = Boolean.getBoolean(request.getParameter("unread"));
		Integer size = 20;
		Integer offset =0;
		if(request.getParameter("size") != null)
			size = Integer.parseInt(request.getParameter("size"));
		if(request.getParameter("offset") != null)
			offset = Integer.parseInt(request.getParameter("offset"));

		List<Messages> msgs = null;
		if(unread)
			msgs = msgRepo.findAllByToUserId(userId,unread,  PageRequest.of(offset, size));
		else
			msgs = msgRepo.findAllByToUserId(userId,  PageRequest.of(offset, size));
		List<MsgDTO> msgDto = new ArrayList<MsgDTO>();
		msgs.forEach(m->{
			MsgDTO msgdto = new MsgDTO();
			msgdto.setMessage(m.getMessage());
			msgdto.setCreateDtm(m.getCreateDtm());
			msgdto.setFromUserId(m.getFromUserId().getId());
			msgdto.setToUserId(m.getToUserId().getId());
			msgdto.setFromName(userId == m.getFromUserId().getId()?"Me":m.getFromUserId().getFirstName());
			msgdto.setToName(userId == m.getToUserId().getId()?"Me":m.getToUserId().getFirstName());
			msgDto.add(msgdto);
		});
		return msgDto;
	}
	
	@Async
	public Object updateMsg(Integer msgId) {
		Messages msg = msgRepo.findById(msgId).get();
		//List<Messages> msgs = msgRepo.findAllBy
		//msg.getCreateDtm()
		msg.setViewInd(Boolean.TRUE);
		msg.setUpdateDtm(new Date());
		msgRepo.save(msg);
		return AppResponse.SUCCESS;
	}

    
}
