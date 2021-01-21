package com.koreait.board;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.koreait.board.common.SecurityUtils;
import com.koreait.board.common.Utils;
import com.koreait.board.db.SQLInterUpdate;
import com.koreait.board.db.UserDAO;
import com.koreait.board.model.UserModel;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

public class UserController {
	//로그인 페이지 띄우기(get)-
		public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			Utils.forwardTemp("로그인", "temp/basic_temp", "user/login", request, response);
	 	}
		
		//로그인 처리(post)
		public void loginProc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			String login_id = request.getParameter("user_id");
			String login_pw = request.getParameter("user_pw");
			
			UserModel param = new UserModel();
			param.setUser_id(login_id);
			//이상없음: 1 아이디 없음 :2, 비밀번호 틀림 : 3
			UserModel loginUser = UserDAO.selUser(param);
			
			if(loginUser == null) { //아이디 없음
				request.setAttribute("msg", "아이디를 확인해 주세요");
				login(request, response);
				return;
			}		
			String userDbPw = loginUser.getUser_pw();
			String userDbSalt = loginUser.getSalt();
			String encryLoginPw = SecurityUtils.getSecurePassword(login_pw, userDbSalt);

			if(userDbPw.equals(encryLoginPw)) { //성공
				loginUser.setSalt(null);
				loginUser.setUser_pw(null);
				loginUser.setR_dt(null);
				loginUser.setPh(null);
				loginUser.setProfile_img(null);
				loginUser.setUser_id(null);
				HttpSession session = request.getSession();
				session.setAttribute("loginUser", loginUser);			
				response.sendRedirect("/board/list.korea?typ=1");			
			} else { //비밀번호 틀림
				request.setAttribute("msg", "비밀번호를 확인해 주세요");
				login(request, response);
			}
		}
		
		//회원가입 페이지 띄우기(get)
		public void join(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			Utils.forwardTemp("회원가입", "temp/basic_temp", "user/join", request, response);
	 	}
		
		public void joinProc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			String user_id = request.getParameter("user_id");
			String user_pw = request.getParameter("user_pw");
			String nm = request.getParameter("nm");
			String strGender = request.getParameter("gender");
			String ph = request.getParameter("ph");
			String salt = SecurityUtils.getSalt();
			String encryPw = SecurityUtils.getSecurePassword(user_pw, salt);
			
			String sql = "INSERT INTO t_user"
					+ "(user_id, user_pw, salt, nm, gender, ph)"
					+ "VALUES"
					+ "(?, ?, ?, ?, ?, ?)";
			
			int result = UserDAO.executeUpdate(sql, new SQLInterUpdate() {
				@Override
				public void proc(PreparedStatement ps) throws SQLException {
					ps.setString(1, user_id);
					ps.setString(2, encryPw);
					ps.setString(3, salt);
					ps.setNString(4, nm);
					ps.setInt(5, Integer.parseInt(strGender));
					ps.setString(6, ph);
				}
			});
			//회원가입 오류가 발생되면 (아이디가 엄청 길면 등등) 다시 회원가입 페이지로 가면 된다.
			if(result == 0) {
				request.setAttribute("msg", "회원가입에 실패하였습니다.");
				join(request, response);
				return;
			}
			//회원가입 완료되면 로그인 화면으로 이동
			response.sendRedirect("/user/login.korea");
		}
		
		public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			HttpSession hs = request.getSession();
			hs.invalidate();
			response.sendRedirect("/user/login.korea");
		}
		
		//프로필 화면
		public void profile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			UserModel param = new UserModel();
			param.setI_user(SecurityUtils.getLoingUserPk(request));
			request.setAttribute("data", UserDAO.selUser(param));
			request.setAttribute("jsList", new String[] {"axios.min", "user"});
			Utils.forwardTemp("프로필", "temp/basic_temp", "user/profile", request, response);
		}
		
		//이미지 업로드 proc
		public void profileUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			int i_user = SecurityUtils.getLoingUserPk(request);
			String savePath = request.getServletContext().getRealPath("/res/img/" + i_user);
			System.out.println("savePath : " + savePath);
			File folder = new File(savePath);
			/*
			//파일을 삭제한다면....
			File imgFile = new File(savePath + "/파일명.jpg");
			if(imgFile.exists()) {
				imgFile.delete();
			}
			*/
			if(folder.exists()) { //기존 이미지가 있었다면 삭제처리
				File[] folder_list = folder.listFiles(); 
				for(File file : folder_list) {
					if(file.isFile()) {
						file.delete();
					}
				}
				folder.delete();
			}		
			folder.mkdirs();
			
			int sizeLimit = 104_857_600; //100mb제한	
			MultipartRequest multi = new MultipartRequest(request, savePath, sizeLimit, "utf-8", new DefaultFileRenamePolicy());
			
			Enumeration files = multi.getFileNames();
			if(files.hasMoreElements()) {
				String eleName = (String)files.nextElement();			
								
				String fileNm = multi.getFilesystemName(eleName);
				System.out.println("fileNm : " + fileNm);	
				
				String sql = "UPDATE t_user SET profile_img = ?"
						+ " WHERE i_user = ?";
				
				UserDAO.executeUpdate(sql, new SQLInterUpdate() {
					@Override
					public void proc(PreparedStatement ps) throws SQLException {
						ps.setString(1, fileNm);
						ps.setInt(2, i_user);
					}					
				});
			}
			
			response.sendRedirect("/user/profile.korea");
		}
		
		
		public void delProfileImg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			int i_user = SecurityUtils.getLoingUserPk(request);
			String savePath = request.getServletContext().getRealPath("/res/img/" + i_user);
			
			File folder = new File(savePath);		
			if(folder.exists()) { //기존 이미지 삭제처리
				File[] folder_list = folder.listFiles(); 
				for(File file : folder_list) {
					if(file.isFile()) {
						file.delete();
					}
				}
				folder.delete();
			}
			String sql = "UPDATE t_user SET profile_img = null "
					+ " WHERE i_user = ?";
			
			UserDAO.executeUpdate(sql, new SQLInterUpdate() {
				@Override
				public void proc(PreparedStatement ps) throws SQLException {				
					ps.setInt(1, i_user);
				}					
			});		
			String result = "{\"result\":1}";
			response.setContentType("application/json");
			response.getWriter().print(result);
		}
}
