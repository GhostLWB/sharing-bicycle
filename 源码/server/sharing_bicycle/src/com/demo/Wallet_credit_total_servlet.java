package com.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.connection.Class_con;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class Wallet_credit_total_servlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//设置返回格式
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		String id = req.getParameter("id");
		PrintWriter out = resp.getWriter();
		String credit ="";
		String balance ="";
		//获取连接
		Connection connection = Class_con.getConnect();
		//要使用的SQL 语句  wallet credit
		String sql1 = "select total_credit from credit where id = ?";
		String sql2 = "select balance from wallet where id = ?";
		QueryRunner queryRunner =new QueryRunner();
		try {
			Map<String, Object> creditMap = queryRunner.query(connection, sql1, new MapHandler(), id);
			Map<String, Object>	balanceMap = queryRunner.query(connection, sql2, new MapHandler(), id);
			if(creditMap ==null || creditMap.size()<1)
			{
				credit = "fail";
			}
			else
			{
				credit = creditMap.get("total_credit").toString();
			}
			if(balanceMap ==null || balanceMap.size()<1)
			{
				balance = "fail";
			}
			else {

				
				balance = balanceMap.get("balance").toString();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println(credit);
		out.println(balance);
		
	}

}
