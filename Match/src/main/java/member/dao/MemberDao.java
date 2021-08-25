package member.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import jdbc.JdbcUtil;
import member.model.Member;

public class MemberDao {

	public Member selectById(Connection con,String id) throws SQLException
	{
		PreparedStatement prst=null;
		ResultSet rs=null;
		String sql="select * from member where id=?";
		
		try
		{
			prst=con.prepareStatement(sql);
			prst.setString(1, id);
			rs=	prst.executeQuery();
			Member member=null;
			if(rs.next())
			{
				member=new Member(rs.getString("id"), rs.getString("name"),rs.getString("password"),rs.getString("tel"));
			}
			return member;
		}finally
		{
			JdbcUtil.close(rs);
			JdbcUtil.close(prst);
		}
		
	}

	public void insert(Connection con, Member member) throws SQLException {
		String sql="insert into member values(?,?,?,?)";
		
		try(PreparedStatement prst=con.prepareStatement(sql))
		{
			prst.setString(1,member.getId());
			prst.setString(2, member.getName());
			prst.setString(3, member.getPassword());
			prst.setString(4, member.getTel());
			prst.executeUpdate();
		}
	}

	public void update(Connection con, Member member) throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement prst=null;
		String sql="update member set password=? where id=?";
		
		try
		{
			prst=con.prepareStatement(sql);
			prst.setString(1, member.getPassword());
			prst.setString(2, member.getId());
			prst.executeUpdate();
		}finally
		{
			JdbcUtil.close(prst);
		}
	}

	public void updateTel(Connection con, Member member) throws SQLException {
		PreparedStatement prst=null;
		String sql="update member set tel=? where id=?";
		
		try
		{
			prst=con.prepareStatement(sql);
			prst.setString(1, member.getTel());
			prst.setString(2, member.getId());
			prst.executeUpdate();
		}finally
		{
			JdbcUtil.close(prst);
		}
		
	}	
}
