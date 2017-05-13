package com.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.StyledEditorKit.BoldAction;

import net.sf.json.JSONObject;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.connection.Class_con;
import com.sun.org.apache.bcel.internal.generic.NEW;

/*
 * @author 宋羽珩
 * 2017/5/5
 * 用于预约用车
 * 2017/5/13更改
 * 加入usr_info 判断的保护
 * 
 */
public class Preorder_bike_servlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		boolean bike_id_code=false;
		boolean flag;
		JSONObject json = new JSONObject();
		Map<String, Object> map=null;
		boolean in_use ;
		boolean in_order;
		boolean break_down;
		int res=0;
		int res_userinfo=0;
		int res_bike=0;
		boolean user_preorder=true;
		boolean user_in_use=true;
		
		//设置请求格式
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		Connection connection =Class_con.getConnect();
		QueryRunner queryRunner = new QueryRunner();
		//获取参数
		String bike_id = req.getParameter("bike_id");
		String user_id = req.getParameter("user_id");
		if((bike_id ==null)||(user_id==null))
		{
			return;
		}
		//
		String sql3="select in_use,in_preorder from user_info where id=?";
		String sql = "select in_use,break_down,in_order from bike where bike_id = ?";
		String sql1 = "update bike set in_order=true,id_preorder = ? where bike_id = ?";
		String sql2 = "UPDATE user_info SET bike_id=?,in_preorder=? where id =?";
		//bike_id_code 标识bike_id 是否正确
		try {
			List<Map<String, Object>> re = queryRunner.query(connection, sql, new MapListHandler(), bike_id);
			if(re.size()==1)
			{
				bike_id_code=true;
				json.put("bike_id_code", bike_id_code);
				map=re.get(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			bike_id_code=false;
			json.put("bike_id_code", bike_id_code);
		}
		//获取车辆属性值
		in_use=(Boolean)map.get("in_use");
		break_down=(Boolean)map.get("break_down");
		in_order=(Boolean)map.get("in_order");
		
		//查询当前用户是否有用车或者预约
		try {
			Map<String, Object> user_result = queryRunner.query(connection, sql3, new MapHandler(), user_id);
			user_in_use=(Boolean)user_result.get("in_use");
			user_preorder=(Boolean)user_result.get("in_preorder");
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//修改user_info bike 表 
		//flag 标识成功或失败
		if((in_use==false)&&(break_down==false)&&(in_order==false))
		{
			if((user_preorder==false)&&(user_in_use==false)){
				try {
					connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
					connection.setAutoCommit(false);
					res_bike=queryRunner.update(connection, sql1,user_id,bike_id);
					res_userinfo=queryRunner.update(connection,sql2,bike_id,true,user_id);
					connection.commit();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					try {
						connection.rollback();
						flag=false;
						json.put("flag", flag);
						json.putAll(map);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			else{
				flag=false;
				json.put("flag", flag);
				json.putAll(map);
				json.put("user_in_preorder", user_preorder);
				json.put("user_in_use", user_in_use);
			}

			
		}
		else {
			flag=false;
			json.put("flag", flag);
			json.putAll(map);
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
			json.put("flag", flag);
		}
		out.print(json.toString());
		
		
	}

}
