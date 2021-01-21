package com.koreait.board.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.koreait.board.model.ManageBoardModel;

public class CommonDAO {
	public static List<ManageBoardModel> selManageBoardList() {
		List<ManageBoardModel> list = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT typ, nm FROM t_manage_board order by orderby";
		
		try {
			con = DbUtils.getCon();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				ManageBoardModel vo = new ManageBoardModel();
				list.add(vo);
				
				vo.setTyp(rs.getInt("typ"));
				vo.setNm(rs.getString("nm"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.close(con, ps, rs);
		}
		
		return list;
	}
	
	//u, d, i
	public static int executeUpdate(String sql, SQLInterUpdate siu) {
		Connection con = null;
		PreparedStatement ps = null;		
		try {
			con = DbUtils.getCon();
			ps = con.prepareStatement(sql);
			siu.proc(ps);
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.close(con, ps);
		}	
		return 0;
	}
}
