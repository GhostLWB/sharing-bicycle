package com.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;

import com.connection.Class_con;
import com.sun.org.apache.xml.internal.serialize.Printer;
/*
 * @author 宋羽珩
 * 2017/5/4
 * 用于接受BIKE端请求的服务端
 * 修改数据库数据
 */
public class Bike_modify_servlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//设置请求格式
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		Connection connection =Class_con.getConnect();
		QueryRunner queryRunner = new QueryRunner();
		//获取参数
		String bike_id = req.getParameter("bike_id");
		String gps_lon = req.getParameter("gps_lon");
		String gps_lat = req.getParameter("gps_lat");
		//
		String sql = "update bike set GPS = ? WHERE bike_id = ?";
		String gps =gps_lon+","+gps_lat;
		String result = "";
		int res = 0 ; //定义返回结果
		//进行bike信息修改
		try {
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			connection.setAutoCommit(false);
			res = queryRunner.update(connection, sql, gps,bike_id);
			connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(res==1)
		{
			result="success";
		}
		else {
			result="fail";
		}
		out.print(result);
	}
	
	
}
