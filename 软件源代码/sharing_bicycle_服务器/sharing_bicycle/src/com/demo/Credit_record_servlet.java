package com.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.connection.Class_con;
/*
 * @宋羽珩
 * 2017/5/15
 * 用于返回用户的积分记录
 */
public class Credit_record_servlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		boolean flag=false;
		List<Map<String, Object>> list;
		//设置编码格式
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		Connection connection =Class_con.getConnect();
		QueryRunner queryRunner = new QueryRunner();
		//获取参数
		String user_id = req.getParameter("user_id");
		if(user_id==null)
		{
			return;
		}
		//定义sql语句
		String sql ="select amount,description,date_time from credit_record WHERE id=? ORDER BY date_time DESC";
		try {
			list=queryRunner.query(connection, sql, new MapListHandler(), user_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return;
		}
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray json = new JSONArray();
		for(int i = 0 ;i<list.size();i++)
		{
			Map<String, Object> map=list.get(i);
			Timestamp ts = (Timestamp)map.get("date_time");
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			map.remove("date_time");
			map.put("date_time", sdf.format(ts));
			json.add(map);
			
			
			
		}
		out.print(json.toString());

	}

}
