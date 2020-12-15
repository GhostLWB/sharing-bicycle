package com.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.connection.Class_con;
import com.sun.org.apache.bcel.internal.generic.NEW;
/*
 * @宋羽珩
 * 2017/5/10
 * 用于返回所有车辆信息  客户端
 */

public class Bike_full_data_servlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		Connection connection =Class_con.getConnect();
		QueryRunner queryRunner = new QueryRunner();
		List<Map<String, Object>> re=new ArrayList<Map<String,Object>>();
		String sql ="SELECT bike_id,in_use,break_down,in_lock,in_order,GPS,id_preorder FROM bike";
		try {
			 re = queryRunner.query(connection, sql, new MapListHandler());
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
		JSONArray json = new JSONArray();
		if(re!=null){
			if(re.size()!=0){
			for(int i = 0 ;i<re.size();i++)
			{
				json.add(re.get(i));
			}
			out.print(json.toString());
			}
		}

	}
	

}
