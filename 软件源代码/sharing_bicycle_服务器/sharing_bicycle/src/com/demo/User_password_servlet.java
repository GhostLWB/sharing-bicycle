package com.demo;
import  com.connection.*;
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.json.JSONObject;


public class User_password_servlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out =resp.getWriter();
		Connection connection= Class_con.getConnect();
		String id=req.getParameter("id");
		String passwd = req.getParameter("password");
		QueryRunner queryRunner =new QueryRunner();
		String sql = "select * from user_passwd where id = ? and password = ? ";
		boolean flag=false;
		try {
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			connection.setAutoCommit(false);
			List<Map<String, Object>> res = queryRunner.query(connection, sql, new MapListHandler(), id,passwd);
			connection.commit();
			if(res.size()!=0)
			{
				flag=true;
			}
			else {
				flag=false;
			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			e.printStackTrace();
		}finally
		{
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Map<String, Boolean> map =new HashMap<String, Boolean>();
		map.put("flag", flag);
		JSONObject jsonObject =new JSONObject(map);
		out.write(jsonObject.toString());
		out.flush();
		out.close();
		

	}

}
