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
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.connection.Class_con;
import com.sun.org.apache.bcel.internal.generic.NEW;
/*
 * @������
 * 2017/5/13
 * ���ڴ����ó�����  ���ͻ��ˣ�
 */
public class Use_bike_servlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		boolean flag=false;
		Map<String, Object> user_result;
		Map<String, Object> bike_result;
		boolean user_in_use=true;
		boolean user_in_preorder=true;
		Integer bike_id_query=null;
		int res_user_info;
		int res_bike;
		String reason="";
		//���������ʽ
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		Connection connection =Class_con.getConnect();
		QueryRunner queryRunner = new QueryRunner();
		
		//��ȡ����
		String bike_id = req.getParameter("bike_id");
		String user_id = req.getParameter("user_id");
		if((bike_id ==null)||(user_id==null))
		{
			return;
		}
		//����sql���
		String sql ="select in_use,in_preorder,bike_id FROM user_info where id=?";
		String sql0="select in_use,in_order,break_down from bike where bike_id=?";
		String sql1="UPDATE user_info SET in_use=TRUE where id =?";
		String sql2="UPDATE bike SET in_use=TRUE,in_lock=FALSE WHERE bike_id=?";
		String sql3="UPDATE user_info set in_preorder=FALSE,bike_id=NULL,in_use=TRUE WHERE id=?";
		String sql4="UPDATE bike set in_order=FALSE,in_use=True,in_lock=FALSE,id_preorder=? WHERE bike_id=?";
		//��ѯ�û�״̬
		try {
			user_result = queryRunner.query(connection, sql, new MapHandler(),user_id);
			if(!((user_result==null)||(user_result.size()<1)))
			{
				user_in_use=(Boolean)user_result.get("in_use");
				user_in_preorder=(Boolean)user_result.get("in_preorder");
				bike_id_query=(Integer)user_result.get("bike_id");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if((user_in_use==false)&&(user_in_preorder==false))
		{
			//�û���ʹ�ã���ԤԼ���
			try {
				bike_result=queryRunner.query(connection, sql0, new MapHandler(), bike_id);
				//�жϳ�������
				if(!((bike_result==null)||(bike_result.size()<1))){
					if((Boolean)bike_result.get("in_order")==true)
					{
						flag=false;
						reason="������ԤԼ";
					}
					else if((Boolean)bike_result.get("in_use")==true){
						flag=false;
						reason="������ʹ��";
					}
					else if ((Boolean)bike_result.get("break_down")==true) {
						flag=false;
						reason="��������";
					}
					else {
						try {
							connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
							connection.setAutoCommit(false);
							res_user_info=queryRunner.update(connection, sql1, user_id);
							res_bike=queryRunner.update(connection, sql2, bike_id);
							connection.commit();
							flag=true;
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							try {
								connection.rollback();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							flag=false;
							reason="���ݸ����쳣";
						}
					}
				}
				else {
					flag=false;
					reason="��ѯ�쳣";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		else if((user_in_use==false)&&(user_in_preorder==true))
		{
			//�û���ԤԼ���
			if(bike_id.equals(bike_id_query.toString()))
			{
				try {
					connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
					connection.setAutoCommit(false);
					res_user_info=queryRunner.update(connection, sql3, user_id);
					res_bike=queryRunner.update(connection, sql4, "",bike_id);
					connection.commit();
					flag=true;
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					try {
						connection.rollback();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					flag=false;
					reason="���ݸ����쳣";
				}

			}
			else {
				flag=false;
				reason="����ԤԼ�����������ó�";
			}
		}
		else {
			flag=false;
			if(user_in_use==true){
				reason="������ʹ�ó������������ó�";
			}
			else {
				reason="����ԤԼ�����������ó�";
			}
		}
		
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject json = new JSONObject();
		json.put("flag", flag);	
		json.put("reason", reason);
		out.println(json.toString());

		
	}

}
