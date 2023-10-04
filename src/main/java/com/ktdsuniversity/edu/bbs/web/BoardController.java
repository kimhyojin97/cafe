package com.ktdsuniversity.edu.bbs.web;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ktdsuniversity.edu.bbs.service.BoardService;
import com.ktdsuniversity.edu.bbs.vo.BoardListVO;
import com.ktdsuniversity.edu.bbs.vo.BoardVO;
import com.ktdsuniversity.edu.beans.FileHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class BoardController {
	@Autowired
	private FileHandler fileHandler;
	
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/board/list")
	public ModelAndView viewBoardList() {
		BoardListVO boardListVO = boardService.getAllBoard();
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("board/boardlist");
		modelAndView.addObject("boardList", boardListVO);
		return modelAndView;
	}
		
	@GetMapping("/board/write")
	public String viewBoardWritePage() {
		return "board/boardwrite"; //jsp파일경로
	}
	
	@PostMapping("/board/write")
	public ModelAndView doBoardWrite(@Valid @ModelAttribute BoardVO boardVO
									//Validation 실패 결과를 갖고온다.
									//@Valid 바로 뒤에 와야함.
								   , BindingResult bindingResult
								   , @RequestParam MultipartFile file
								   , HttpServletRequest request) {
		//요청자의 ip정보를 ipAddr 변수에 할당.
		boardVO.setIpAddr(request.getRemoteAddr());
		System.out.println("제목: " + boardVO.getSubject());
		System.out.println("이메일: " + boardVO.getEmail());
		System.out.println("내용: " + boardVO.getContent());
		System.out.println("등록일: " + boardVO.getCrtDt());
		System.out.println("수정일: " + boardVO.getMdfyDt());
		System.out.println("FileName: " + boardVO.getFileName());
		System.out.println("OriginFileName: " + boardVO.getOriginFileName());
		
		ModelAndView modelAndView = new ModelAndView();
		
		//Validation 체크한 것 중 실패한 것이 있다면,
		if(bindingResult.hasErrors()) {
			//화면을 보여준다.
			//게시글 등록은 하지 않는다.
			modelAndView.setViewName("board/boardwrite");
			modelAndView.addObject("boardVO", boardVO);
			return modelAndView;
		}
		
		boolean isSuccess = boardService.createNewBoard(boardVO, file);
		if(isSuccess) { //게시글 등록 결과가 성공이라면
			//"/board/list" URL로 이동.
			modelAndView.setViewName("redirect:/board/list");
			return modelAndView;
		}else { //실패라면
			//게시글 등록(작성)화면으로 데이터를 보내줌.
			//게시글 등록(작성)화면에서 boardVO값으로 등록 값을 설정해야 함.
			modelAndView.setViewName("board/boardwrite");
			modelAndView.addObject("boardVO", boardVO);
			return modelAndView;
		}
	}
	
	//@RequestParam 파라미터를 받아오는 방법 중 하나
	//  => 전달되는 파라미터가 1개 내지는 2개 정도 일때 유용(파라미터의 이름과 변수명이 동일해야함(id))
	//@ModelAttribute 파라미터를 받아오는 방법 중 하나
	//  => Command Object: 전달되는 파라미터들이 여러개 일 때 유용.(파라미터의 이름과 VO클래스 멤버변수들의 이름을 동일하게 부여)
	@GetMapping("/board/view")// http://localhost:8080/board/view ?id=1
	public ModelAndView viewOneBoard(@RequestParam int id) {
		BoardVO boardVO = boardService.getOneBoard(id, true);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("board/boardview");
		modelAndView.addObject("boardVO", boardVO);
		return modelAndView;
	}
	
	@GetMapping("/board/file/download/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable int id) {
		//파일 정보를 얻어오기 위해 게시글을 조회함.
		BoardVO boardVO = boardService.getOneBoard(id, false);
		if(boardVO == null) {
			throw new IllegalArgumentException("잘못된 접근입니다.");
		}
		//서버에 등록되어있는 파일을 가져온다.
		File storedFile = fileHandler.getStoredFile(boardVO.getFileName());
		
		return fileHandler.getResponseEntity(storedFile, boardVO.getOriginFileName());
	}
	
	@GetMapping("/board/modify/{id}")// http://localhost:8080/board/modify/2
	public ModelAndView viewBoardModifyPage(@PathVariable int id) {
		BoardVO boardVO = boardService.getOneBoard(id, false);
		//게시글 수정을 위해 게시글의 내용을 조회
		//게시글 조회와 동일한 코드 호출
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("board/boardmodify");
		modelAndView.addObject("boardVO", boardVO);
		return modelAndView;
	}
	
	@PostMapping("/board/modify")
	public ModelAndView doBoardUpdate(@Valid @ModelAttribute BoardVO boardVO
									, BindingResult bindingResult
									, Model model
									, @RequestParam MultipartFile file) {
		System.out.println("ID: " + boardVO.getId());
		System.out.println("제목: " + boardVO.getSubject());
		System.out.println("이메일: " + boardVO.getEmail());
		System.out.println("내용: " + boardVO.getContent());
		System.out.println("등록일: " + boardVO.getCrtDt());
		System.out.println("수정일: " + boardVO.getMdfyDt());
		System.out.println("FileName: " + boardVO.getFileName());
		System.out.println("OriginFileName: " + boardVO.getOriginFileName());
		
		ModelAndView modelAndView = new ModelAndView();
		
		if(bindingResult.hasErrors()) {
			modelAndView.setViewName("board/boardmodify");
			modelAndView.addObject("boardVO", boardVO);
			return modelAndView;
		}
		
		//게시글 수정
		boolean isSuccess = boardService.updateOneBoard(boardVO, file);
		if(isSuccess) {
			//게시글 수정 결과가 성공이라면 /board/view/id?=1 URL로 이동.
			modelAndView.setViewName("redirect:/board/view?id=" + boardVO.getId());
			return modelAndView;
		}else {
			//게시글 수정 결과가 실패라면 게시글 수정 화면으로 데이터를 보내줌.
			modelAndView.setViewName("board/boardmodify");
			modelAndView.addObject("boardVO", boardVO);
			return modelAndView;
		}
	}
	
	@GetMapping("/board/delete/{id}")
	public String doBoardDelete(@PathVariable int id) {
		boolean isSuccess = boardService.deleteOneBoard(id);
		if(isSuccess) {
			return "redirect:/board/list";
		} else {
			return "redirect:/board/view?id=" + id;
		}
	}

}
