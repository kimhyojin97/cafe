package com.ktdsuniversity.edu.bbs.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ktdsuniversity.edu.bbs.service.BoardService;
import com.ktdsuniversity.edu.bbs.vo.BoardListVO;
import com.ktdsuniversity.edu.bbs.vo.BoardVO;
import com.ktdsuniversity.edu.bbs.vo.SearchBoardVO;
import com.ktdsuniversity.edu.beans.FileHandler;
import com.ktdsuniversity.edu.member.vo.MemberVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class BoardController {
	private Logger logger = LoggerFactory.getLogger(BoardController.class);
	
	@Autowired
	private FileHandler fileHandler;
	
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/board/list")
	public ModelAndView viewBoardList(@ModelAttribute SearchBoardVO searchBoardVO) {
		BoardListVO boardListVO = boardService.getAllBoard(searchBoardVO);
		searchBoardVO.setPageCount(boardListVO.getBoardCnt());
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("board/boardlist");
		modelAndView.addObject("boardList", boardListVO);
		modelAndView.addObject("searchBoardVO", searchBoardVO);
		return modelAndView;
	}
		
	@GetMapping("/board/write")
	public String viewBoardWritePage(HttpSession session) {
		MemberVO memberVO = (MemberVO) session.getAttribute("_LOGIN_USER_");
		if(memberVO == null) {
			return "redirect:/member/login";
		}
		return "board/boardwrite"; //jsp파일경로
	}
	
	@PostMapping("/board/write")
	public ModelAndView doBoardWrite(@Valid @ModelAttribute BoardVO boardVO
									//Validation 실패 결과를 갖고온다.
									//@Valid 바로 뒤에 와야함.
								   , BindingResult bindingResult
								   , @RequestParam MultipartFile file
								   , HttpServletRequest request
								   , @SessionAttribute("_LOGIN_USER_") MemberVO memberVO) {
		
		logger.debug("제목: " + boardVO.getSubject());
		logger.debug("이메일: " + boardVO.getEmail());
		logger.debug("내욜: " + boardVO.getContent());
		logger.debug("등록일: " + boardVO.getCrtDt());
		logger.debug("수정일: " + boardVO.getMdfyDt());
		logger.debug("FileName: " + boardVO.getFileName());
		logger.debug("OriginFileName: " + boardVO.getOriginFileName());
		
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
		boardVO.setEmail(memberVO.getEmail());
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
	public ModelAndView viewBoardModifyPage(@PathVariable int id
										, Model model
										, @SessionAttribute("_LOGIN_USER_") MemberVO memberVO) {
		BoardVO boardVO = boardService.getOneBoard(id, false);
		if(!boardVO.getEmail().equals(memberVO.getEmail())) {
			throw new IllegalArgumentException("잘못된 접근입니다.");
		}
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
									, @RequestParam MultipartFile file
									, @SessionAttribute("_LOGIN_USER_") MemberVO memberVO) {
		
		logger.debug("제목: " + boardVO.getSubject());
		logger.debug("이메일: " + boardVO.getEmail());
		logger.debug("내욜: " + boardVO.getContent());
		logger.debug("등록일: " + boardVO.getCrtDt());
		logger.debug("수정일: " + boardVO.getMdfyDt());
		logger.debug("FileName: " + boardVO.getFileName());
		logger.debug("OriginFileName: " + boardVO.getOriginFileName());
		
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
		BoardVO originBoardVO = boardService.getOneBoard(boardVO.getId(), false);
		if(!originBoardVO.getEmail().equals(memberVO.getEmail())) {
			throw new IllegalArgumentException("잘못된 접근입니다.");
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
	public String doBoardDelete(@PathVariable int id
							  , @SessionAttribute("_LOGIN_USER_") MemberVO memberVO) {
		BoardVO boardVO = boardService.getOneBoard(id, false);
		if(!boardVO.getEmail().equals(memberVO.getEmail())) {
			throw new IllegalArgumentException("잘못된 접근입니다.");
		}
		boolean isSuccess = boardService.deleteOneBoard(id);
		if(isSuccess) {
			return "redirect:/board/list";
		} else {
			return "redirect:/board/view?id=" + id;
		}
	}
	
	@GetMapping ("/board/excel/download")
	public ResponseEntity<Resource> downloadExcelFile() {
	     
	     // 엑셀로 만들 모든 게시글을 조회한다. 
		 BoardListVO boardListVO = boardService.getAllBoard(null);
		 
		 //xlsx 파일을 만든다.
		 Workbook workbook = new SXSSFWorkbook(-1);
		 
		 // 엑셀 시트를 만든다.
		 Sheet sheet = workbook.createSheet("게시글 목록");
		 
		 //엑셀 시트에 행(row)를 만든다.
		 Row row = sheet.createRow(0);
		 
		 // 행(row)에 열(column)을 추가해 타이틀을 만든다. 
		 Cell cell = row.createCell(0);
		 cell.setCellValue("번호");
		 cell = row.createCell(1);
		 cell.setCellValue("제목");
		 cell = row.createCell(2);
		 cell.setCellValue("첨부파일명");
		 cell = row.createCell(3);
		 cell.setCellValue("작성자이메일");
		 cell = row.createCell(4);
		 cell.setCellValue("조회수");
		 cell = row.createCell(5);
		 cell.setCellValue("등록일");
		 cell = row.createCell(6);
		 cell.setCellValue("수정일");
		 
		 // 게시글의 수 만큼 행(row)을 만들고 열(column)을 만들어 데이터를 추가한다.
		 List<BoardVO> boardList = boardListVO.getBoardList();   
		 
		 // 두번째 줄부터 데이터를 만든다. 
		 int rowIndex = 1;
		 for (BoardVO boardVO : boardList) {
		    row = sheet.createRow(rowIndex);
		    
		    cell = row.createCell(0);
		    cell.setCellValue ("" + boardVO.getId());
		
		cell = row.createCell(1);
		cell.setCellValue(boardVO.getSubject());
		cell = row.createCell(2);
		cell.setCellValue(boardVO.getOriginFileName());
		cell = row.createCell(3);
		cell.setCellValue(boardVO.getEmail());
		cell = row.createCell(4);
		cell.setCellValue("조회수");
		cell = row.createCell(5);
		cell.setCellValue("등록일");
		cell = row.createCell(6);
		cell.setCellValue("수정일");
		    
		    rowIndex +=1;
		 }
		 //작성한 문서를 파일로 만든다.
		 File excelFile = fileHandler.getStoredFile("게시글_목록.xlsx");
		 // outputStream: java에서 다른시스템으로 데이터를 내보내는 것
		 OutputStream os = null;
		 try {
		    os = new FileOutputStream(excelFile);
		    workbook.write(os);
		 } catch (IOException e) {
		    throw new IllegalArgumentException("엑셀파일을 만들 수 없습니다");
		 } finally { 
		    try { 
		       workbook.close();
		    } catch (IOException e) {}
		    if (os != null) {
		       try {os.flush();
		    } catch (IOException e) {}
		       try {os.close();
		    }catch (IOException e) {}
		    }
		 }
		 //엑셀파일을 다운로드 한다. 
		 // outputstreamd을 쓰고 닫는다
		 // 메모리에 저장하고 있는 output stream을 외부로 보낸다
		 
		 // 엑셀파일을 다운로드 한다.
		 
		 // 파일명 생성
		 // 다운로드할 파일명이한글일 때 urlencoder 라는 것을 사용한다.
		 String downloadFileName = URLEncoder.encode("게시글목록.xlsx",
			                        Charset.defaultCharset());
			         
         return fileHandler.getResponseEntity(excelFile, downloadFileName);
               
      }
	}
