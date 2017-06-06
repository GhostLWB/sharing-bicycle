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

import net.sf.json.JSONObject;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.connection.Class_con;
import com.sun.org.apache.bcel.internal.generic.NEW;
/*
 * @author ������
 * 2017/5/5
 * ���ڷ���ָ��������Ϣbike��
 * 
 */
public class Get_single_bike_servlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//���ñ����ʽ����ȡ���ݿ�����
		boolean result_code = false;
		JSONObject json = new JSONObject();
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out =resp.getWriter();
		Connection connection= Class_con.getConnect();
		
		//��ȡ������ִ��sql���
		String bike_id = req.getParameter("bike_id");
		String sql="select in_use,break_down,description,in_order,in_lock from bike where bike_id=?";
		QueryRunner queryRunner = new QueryRunner();
		try {
			List<Map<String, Object>> re = queryRunner.query(connection, sql, new MapListHandler(), bike_id);
			//���ؽ������
			if(re.size()==1){
				result_code = true;
				json.putAll(re.get(0));
				json.put("code", result_code);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result_code=false;
			json.put("code", result_code);
		}
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//���ؽ��
		out.print(json.toString());
	}

}
