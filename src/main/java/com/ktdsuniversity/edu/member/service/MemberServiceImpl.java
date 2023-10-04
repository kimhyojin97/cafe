package com.ktdsuniversity.edu.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ktdsuniversity.edu.member.dao.MemberDAO;
import com.ktdsuniversity.edu.member.vo.MemberVO;

@Service
public class MemberServiceImpl implements MemberService {
	@Autowired
	private MemberDAO memberDAO;

	@Override
	public boolean checkAvailableEmail(String email) {
		int emailCount = memberDAO.getEmailCount(email);
		return emailCount == 0;
	}
	
	@Override
	public boolean createNewMember(MemberVO memberVO) {
		int emailCount = memberDAO.getEmailCount(memberVO.getEmail());
		if(emailCount > 0) {
			throw new IllegalArgumentException("Email이 이미 사용중입니다.");
		}
		//TODO 암호화 처리 한 이후에 삭제해야함.
		memberVO.setSalt("-");
		
		int insertCount = memberDAO.createNewMember(memberVO);
		return insertCount > 0;
	}

}
