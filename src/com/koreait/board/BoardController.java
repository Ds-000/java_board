package com.koreait.board;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.koreait.board.common.SecurityUtils;
import com.koreait.board.common.Utils;
import com.koreait.board.db.BoardDAO;
import com.koreait.board.db.SQLInterUpdate;
import com.koreait.board.model.BoardPARAM;
import com.koreait.board.model.BoardSEL;

public class BoardController {

	public void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int typ = Utils.getIntParam(request, "typ", 1);
		BoardPARAM param = new BoardPARAM();
		param.setTyp(typ);
		
		request.setAttribute("jsList", new String[] {"board"});
		request.setAttribute("list", BoardDAO.selBoardList(param));
		Utils.forwardTemp("리스트", "temp/basic_temp", "board/bList", request, response);
	}
	
	public void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int i_board = Utils.getIntParam(request, "i_board");
		BoardPARAM param = new BoardPARAM();
		param.setI_board(i_board);
		param.setI_user(SecurityUtils.getLoingUserPk(request));
	
		
		//ip주소로 구분하여 application 객체 이용하여 새로고침해서 조회수 안 올라가도록 조치!!
		
		//1. 아이피주소값 얻어야 함
		//2. application에서 "특정키값 만들어야 함" 즉, 어떤 글인지 구분이 가능해야한다.
		//3. application에서 특정키값으로 값이 있는지 체크
		//3-1 : 없으면 그 특정키값으로 나의 ip주소를 set한다. (조회수 올림)
		//3-2 : 있으면 application에 저장된 ip주소가 나랑 같은지 확인
		//3-2-1 : 같으면 무시
		//3-2-2 : 다르면 (조회수 올림) > 그 특정키값으로 나의 ip주소를 set한다.
		
		//--------------------------------------------------- [조회수 처리 start] --/
		final String KEY = "b_" + i_board;
		String myIpAddr = request.getRemoteAddr();
		ServletContext application = request.getServletContext();
		
		String savedIpAddr = (String)application.getAttribute(KEY);
		if(savedIpAddr == null || !savedIpAddr.equals(myIpAddr)) {
			application.setAttribute(KEY, myIpAddr);
			
			String sql = "UPDATE t_board SET hits = hits + 1"
					+ " WHERE i_board = ?";
			
			BoardDAO.executeUpdate(sql, new SQLInterUpdate() {
				@Override
				public void proc(PreparedStatement ps) throws SQLException {
					ps.setInt(1, i_board);
				}
			});			
		} 
		//--------------------------------------------------- [조회수 처리 end] --/
		
		BoardSEL data = BoardDAO.selBoard(param);
		request.setAttribute("data", data);		
		request.setAttribute("jsList", new String[] {"board", "axios.min"});
		Utils.forwardTemp(data.getTitle(), "temp/basic_temp", "board/bDetail", request, response);
	}
	
	public void reg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Utils.forwardTemp("글등록", "temp/basic_temp", "board/bRegmod", request, response);
	}
	
	public void mod(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Utils.forwardTemp("글수정", "temp/basic_temp", "board/bRegmod", request, response);
	}
	
	public void regProc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int typ = Utils.getIntParam(request, "typ");
		String title = request.getParameter("title");
		String ctnt = request.getParameter("ctnt");
		int writerI_user = SecurityUtils.getLoingUserPk(request);		
		String sql = " INSERT INTO t_board" + 
				" (typ, seq, title, ctnt, i_user)" + 
				" SELECT ?, IFNULL(MAX(seq), 0) + 1, ?, ?, ? FROM t_board" + 
				" WHERE typ = ? ";		
		int result = BoardDAO.executeUpdate(sql, new SQLInterUpdate() {
			@Override
			public void proc(PreparedStatement ps) throws SQLException {
				ps.setInt(1, typ);
				ps.setNString(2, title);
				ps.setNString(3, ctnt);
				ps.setInt(4, writerI_user);
				ps.setInt(5, typ);
			}
		});		
		response.sendRedirect("/board/list.korea?typ=" + typ);
		
	}
	
	public void modProc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	
	public void ajaxFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		int state = Utils.getIntParam(request, "state");
		int i_board = Utils.getIntParam(request, "i_board");
		
		System.out.println("state : " + state);
		System.out.println("i_board : " + i_board);

		String sql = "INSERT INTO t_board_favorite (i_board, i_user) VALUES (?, ?)";
		if(state == 0) { 
			sql = "DELETE FROM t_board_favorite WHERE i_board = ? AND i_user = ?";
		}
		
		int result = BoardDAO.executeUpdate(sql, new SQLInterUpdate() {
			@Override
			public void proc(PreparedStatement ps) throws SQLException {
				ps.setInt(1, i_board);
				ps.setInt(2, SecurityUtils.getLoingUserPk(request));
			}
		});
		response.setContentType("application/json");
		response.getWriter().print(String.format("{\"result\":%d}", result));
	}
}
