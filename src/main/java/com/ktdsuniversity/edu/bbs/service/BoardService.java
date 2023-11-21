package com.ktdsuniversity.edu.bbs.service;

import org.springframework.web.multipart.MultipartFile;

import com.ktdsuniversity.edu.bbs.vo.BoardListVO;
import com.ktdsuniversity.edu.bbs.vo.BoardVO;
import com.ktdsuniversity.edu.bbs.vo.SearchBoardVO;

public interface BoardService {
	/**
	 * 게시글의 목록과 게시글의 건수를 모두 조회한다
	 * @param serchBoardVO 페이지네이션을 위한 파라미터. null을 전달할 경우, 모든 게시글을 조회한다.
	 * @return
	 */
	public BoardListVO getAllBoard(SearchBoardVO searchBoardVO);
	
	/**
	 * 새로운 게시글 등록 처리
	 * @param boardVO 사용자가 입력한 게시글 정보
	 * @param file 사용자가 업로드한 파일의 점보
	 * @return 정상적으로 등록되었는지 여부
	 */
	public boolean createNewBoard(BoardVO boardVO, MultipartFile file);
	
	/**
	 * 파라미터로 전달받은 id로 게시글을 조회함.
	 * 게시글 조회 시 조회수도 1 증가.
	 * @param id 조회할 게시글의 id
	 * @param isIncrease 값이 true면 조회수 증가.
	 * @return 게시글의 정보
	 */
	public BoardVO getOneBoard(int id, boolean isIncrease);
	
	/**
	 * 게시글의 정보를 수정함
	 * BoardVO의 id값에 수정할 게시글의 ID값이 있어야한다.
	 * @param boardVO 사용자가 수정한 게시글의 정보
	 * @param file 업로드한 파일(기존의 파일이 있다면 삭제하고 새롭게 등록.)
	 * @return 정상적으로 수정되었는지 여부
	 */
	public boolean updateOneBoard(BoardVO boardVO, MultipartFile file);
	
	/**
	 * 파라미터로 전달받은 게시글 ID의 게시글을 삭제한다.
	 * @param id 게시글의 ID
	 * @return 정상적으로 삭제되었는지 여부
	 */
	public boolean deleteOneBoard(int id);

}
