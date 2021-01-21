package com.koreait.board.db;

import java.sql.*;
import java.util.*;
import com.koreait.board.model.*;

public class BoardDAO extends CommonDAO{
	public static BoardSEL selBoard(BoardPARAM param) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = " SELECT A.i_board, A.typ, A.seq, A.title, A.ctnt, A.r_dt, A.hits"
				+ " , B.i_user, B.nm AS writer_nm"
				+ " , ifnull(C.favorite_cnt, 0) AS favorite_cnt"
				+ " , CASE WHEN D.i_board IS NULL THEN 0 ELSE 1 END AS is_favorite"
				+ " FROM t_board A"
				+ " LEFT JOIN t_user B"
				+ " ON A.i_user = B.i_user"
				+ " LEFT JOIN ("
				+ " SELECT i_board, COUNT(i_board) AS favorite_cnt"
				+ "	FROM t_board_favorite"
				+ " GROUP BY i_board"
				+ " ) C ON A.i_board = C.i_board"
				
				+ " LEFT JOIN t_board_favorite D"
				+ " ON A.i_board = D.i_board"
				+ " AND D.i_user = ?"
				+ " WHERE A.i_board = ?";
		
		try {
			con = DbUtils.getCon();
			ps = con.prepareStatement(sql);
			ps.setInt(1, param.getI_user()); //�α��� ���� PK
			ps.setInt(2, param.getI_board());			
			rs = ps.executeQuery();
			if(rs.next()) {
				BoardSEL vo = new BoardSEL();		
				vo.setI_board(rs.getInt("i_board"));
				vo.setTyp(rs.getInt("typ"));
				vo.setSeq(rs.getInt("seq"));
				vo.setTitle(rs.getNString("title"));
				vo.setCtnt(rs.getNString("ctnt"));
				vo.setR_dt(rs.getString("r_dt"));
				vo.setHits(rs.getInt("hits"));
				vo.setI_user(rs.getInt("i_user"));
				vo.setWriter_nm(rs.getString("writer_nm"));
				vo.setFavorite_cnt(rs.getInt("favorite_cnt"));
				vo.setIs_favorite(rs.getInt("is_favorite"));
				return vo;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.close(con, ps, rs);
		}
		

		return null;
	}
	
	public static List<BoardSEL> selBoardList(BoardPARAM param) {
		List<BoardSEL> list = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = " SELECT A.i_board, A.seq, A.title, A.r_dt, A.hits"
				+ " , B.i_user, B.nm AS writer_nm"
				+ " , ifnull(C.favorite_cnt, 0) AS favorite_cnt"
				+ " FROM t_board A"
				+ " LEFT JOIN t_user B"
				+ " ON A.i_user = B.i_user"
				+ " LEFT JOIN ("
				+ " SELECT i_board, COUNT(i_board) AS favorite_cnt"
				+ "	FROM t_board_favorite"
				+ " GROUP BY i_board"
				+ " ) C ON A.i_board = C.i_board"
				+ " WHERE A.typ = ?"
				+ " ORDER BY seq DESC";
		
		try {
			con = DbUtils.getCon();
			ps = con.prepareStatement(sql);
			ps.setInt(1, param.getTyp());
			rs = ps.executeQuery();
			while(rs.next()) {
				BoardSEL vo = new BoardSEL();
				list.add(vo);
				
				vo.setI_board(rs.getInt("i_board"));
				vo.setSeq(rs.getInt("seq"));
				vo.setTitle(rs.getString("title"));
				vo.setR_dt(rs.getString("r_dt"));
				vo.setHits(rs.getInt("hits"));
				vo.setI_user(rs.getInt("i_user"));
				vo.setWriter_nm(rs.getString("writer_nm"));
				vo.setFavorite_cnt(rs.getInt("favorite_cnt"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.close(con, ps, rs);
		}
		
		return list;
	}
}
