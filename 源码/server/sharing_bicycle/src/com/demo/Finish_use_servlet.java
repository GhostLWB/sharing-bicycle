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

public class Finish_use_servlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		Float balance=null;
		Integer credit=null;
		Map<String, Object> user_re;
		String flag="";
		//设置请求格式
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		Connection connection =Class_con.getConnect();
		QueryRunner queryRunner = new QueryRunner();
		//获取参数
		String bike_id = req.getParameter("bike_id");
		String user_id = req.getParameter("user_id");
		String credit_change=req.getParameter("credit");
		String balance_change=req.getParameter("balance");
		String riding_time =req.getParameter("riding_time");
		if((bike_id==null)||(user_id==null)||(credit_change==null)||(balance_change==null)||(riding_time==null))
		{
			return;
		}
		//SQL语句
		String sql = "UPDATE bike set in_lock=TRUE,in_use=FALSE WHERE bike_id=?";
		String sql1="select balance,total_credit FROM user_info WHERE id=?";
		String sql2="UPDATE user_info set in_use=FALSE,balance=?,total_credit=? WHERE id =?";
		String sql3="INSERT into cycling_record(id,bike_id,date_time,riding_time) VALUES(?,?,NOW(),?)";
		String sql4="insert into wallect_record(id,date_time,amount) VALUES (?,NOW(),?)";
		String sql5="INSERT into credit_record(id,amount,description,date_time) VALUES (?,?,?,NOW())";
		try {
			user_re=queryRunner.query(connection, sql1, new MapHandler(), user_id);
			if(!((user_re==null)||user_re.size()<1))
			{
				balance=(Float)user_re.get("balance");
				credit=(Integer)user_re.get("total_credit");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			out.print("fail");
			
			return;
		}
		balance=balance+(Float.parseFloat(balance_change));
		credit=credit+Integer.parseInt(credit_change);
		try {
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			connection.setAutoCommit(false);
			queryRunner.update(connection, sql, bike_id);
			queryRunner.update(connection, sql2, balance,credit,user_id);
			queryRunner.update(connection, sql3, user_id,bike_id,riding_time);
			queryRunner.update(connection, sql4, user_id,balance_change);
			queryRunner.update(connection,sql5,user_id,credit_change,"骑行奖励");
			connection.commit();
		} catch (SQLException e) {
			System.out.println(e.toString());
			// TODO Auto-generated catch block
			try {
				connection.rollback();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			flag="fail";
			out.print(flag);

			return;
		}
		
		flag="success";
		out.print(flag);

		
		
	}

}
