package com.ktdsuniversity.edu.bbs.service;

import java.util.List;

import com.ktdsuniversity.edu.bbs.vo.ReplyVO;

public interface ReplyService {
	public List<ReplyVO> getAllReplies(int boardId);
	public boolean createNewReply(ReplyVO replyVO);
	public boolean deleteOneReply(int replyId, String email);
	public boolean modifyOneReply(ReplyVO replyVO);
	public boolean recomendOneReply(int replyId, String email);

}
