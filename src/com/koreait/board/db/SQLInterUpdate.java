package com.koreait.board.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SQLInterUpdate {
	void proc(PreparedStatement ps) throws SQLException;
}