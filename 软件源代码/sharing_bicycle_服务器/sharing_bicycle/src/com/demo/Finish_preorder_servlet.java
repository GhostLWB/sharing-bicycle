package com.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.json.JSONException;
import org.json.JSONObject;

import com.connection.Class_con;

/*
 * @宋羽珩
 * 2017/5/10
 * 用于结束 预约服务的请求
 * 修改user_info,bike 表
 * 必须是已经预约的用户
 */
public class Finish_preorder_servlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//设置编码格式
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		Connection connection =Class_con.getConnect();
		QueryRunner queryRunner = new QueryRunner();
		boolean flag=false;
		//定义结果标记
		int res_bike=0;
		int res_userinfo=0;
		//获取用户参数
		String bike_id =req.getParameter("bike_id");
		String user_id = req.getParameter("user_id");
		//判断非空
		if((bike_id ==null)||(user_id==null))
		{
			return;
		}
		//定义sql语句
		String sql1="UPDATE bike set in_order=FALSE,id_preorder=? WHERE bike_id = ?;";
		String sql2="UPDATE user_info set in_preorder=FALSE,bike_id=null where id=?";
		try {
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			connection.setAutoCommit(false);
			res_bike=queryRunner.update(connection, sql1, "",bike_id);
			res_userinfo=queryRunner.update(connection,sql2,user_id);
			connection.commit();
			
		} catch (SQLException e) {
			flag=false;
			// TODO Auto-generated catch block
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if((res_bike==1)&&(res_userinfo==1))
		{
			flag=true;
		}
		Map<String,Boolean> map =new HashMap<String, Boolean>();
		map.put("flag", flag);
		JSONObject jsonObject =new JSONObject(map);
		out.print(jsonObject.toString());
		
		
		
		
		
		
	}

}
