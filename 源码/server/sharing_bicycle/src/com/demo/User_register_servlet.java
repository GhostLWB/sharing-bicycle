package com.demo;
import com.connection.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
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

public class User_register_servlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//设置返回格式
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		boolean flag=false;
		boolean hav_register=true;
		PrintWriter out = resp.getWriter();
		Connection connection=Class_con.getConnect();
		QueryRunner queryRunner = new QueryRunner();
		String sql="insert into user_passwd values (?,?)";
		String username=req.getParameter("id");
		String password=req.getParameter("password");
		int res=0;
		String sql1="select * from user_passwd where id = ?";
		//初始化wallet和credit
		String sql_userinfo ="INSERT into user_info(id,balance,total_credit,in_use,in_preorder) VALUES (?,?,?,?,?)";
		try {

			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			connection.setAutoCommit(false);
			List<Map<String, Object>> result = queryRunner.query(connection, sql1, new MapListHandler(),username);
			if(result.size()==0)
			{
				hav_register=false;
				res=queryRunner.update(connection, sql, username,password);
				//wallet 初始化 设置值为100
				queryRunner.update(connection,sql_userinfo,username,100,100,false,false);
				
			}
			connection.commit();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			try {
				connection.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			e1.printStackTrace();
		}

		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(res==0)
		{
			flag=false;
		}
		else {
			flag=true;
		}
		Map<String, Boolean> map =new HashMap<String, Boolean>();
		map.put("flag", flag);
		map.put("hav_register", hav_register);
		JSONObject jsonObject =new JSONObject(map);
		out.write(jsonObject.toString());
		out.flush();
		out.close();

	}

}
