package reserve.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;

public class TimeSearchDAO {
	
	//해당 풋살장의 아이디를 구하는 쿼리 메서드
	public int getPlaceId(Connection con, String placeName) throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement prst=null;
		ResultSet rs=null;
		int id=0;
		String sql="select place_id from place where  PLACE_NAME=?";
		
		try
		{
			prst=con.prepareStatement(sql);
			prst.setString(1, placeName);
			rs=prst.executeQuery();
			
			if(rs.next())
			{
				id=rs.getInt("place_id");
			}
			
			return id;
		}finally
		{
			JdbcUtil.close(rs);
			JdbcUtil.close(prst);
		}
		
	}

	public List<String> getTimeList(Connection con, int place_id) throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement prst=null;
		ResultSet rs=null;
		List<String> list=null;
		String sql="select OPERATION_TIME from operation where PLACE_ID=?";
		
		try
		{
			prst=con.prepareStatement(sql);
			prst.setInt(1, place_id);
			rs=prst.executeQuery();
			list=new ArrayList<String>();
			while(rs.next())
			{
				list.add(rs.getString("OPERATION_TIME"));
			}
			return list;
		}finally
		{
			JdbcUtil.close(rs);
			JdbcUtil.close(prst);
		}
	}

}
