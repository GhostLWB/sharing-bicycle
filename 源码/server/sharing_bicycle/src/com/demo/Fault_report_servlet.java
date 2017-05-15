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

import net.sf.json.JSONObject;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import com.connection.Class_con;
import com.sun.org.apache.bcel.internal.generic.NEW;
/*
 * @宋羽珩
 * 2017/5/14
 * 用户提供故障申报功能 客户端
 * 
 */
public class Fault_report_servlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		Map<String,Object> result_map;
		boolean in_use=true;
		boolean in_preorder=true;
		Boolean flag=false;
		String  reason="";
		int res_bike=0;
		int res_user=0;
		JSONObject json = new JSONObject();
		//设置编码格式
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		Connection connection =Class_con.getConnect();
		QueryRunner queryRunner = new QueryRunner();
		//参数获取
		String bike_id =req.getParameter("bike_id");
		String description =req.getParameter("description");
		String user_id = req.getParameter("user_id");
		if((bike_id==null)||(description==null)||(user_id==null)){
			return;
		}
		String sql ="select in_use,in_order from bike where bike_id=?";
		String sql2="UPDATE bike set break_down=true,description=? WHERE bike_id=?";
		String sql3="UPDATE user_info set total_credit=total_credit+30 WHERE id=?";
		try {
			result_map=queryRunner.query(connection, sql, new MapHandler(), bike_id);
			if(!((result_map==null)||(result_map.size()<1))){
				in_use=(Boolean)result_map.get("in_use");
				in_preorder=(Boolean)result_map.get("in_order");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag=false;
			reason="数据异常";
			json.put("flag", flag);
			json.put("reason", reason);
			out.println(json.toString());
			return;
		}
		if(in_use==true){
			flag=false;
			reason="车辆在使用，报修失败";
		}
		else if (in_preorder==true) {
			flag=false;
			reason="车辆被预约，报修失败";
		}
		else {
			try {
				connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
				connection.setAutoCommit(false);
				res_bike=queryRunner.update(connection, sql2, description,bike_id);
				res_user=queryRunner.update(connection, sql3, user_id);
				connection.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				try {
					connection.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				flag=false;
				reason="数据异常";
				json.put("flag", flag);
				json.put("reason", reason);
				out.println(json.toString());
				return;
			}
			flag=true;
			

		}
		
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		json.put("flag", flag);
		json.put("reason", reason);
		out.println(json.toString());
		
		
	}

}
