package article.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import article.model.Article;
import article.model.Writer;
import jdbc.JdbcUtil;

public class ArticleDao {
	public Article insert(Connection conn, Article article) throws SQLException{
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement("insert into article values (num_seq.nextval,?,?,?,?,?,?,0)");
			pstmt.setString(1, article.getWriter().getId());
			pstmt.setString(2, article.getWriter().getName());
			pstmt.setString(3, article.getTitle());
			pstmt.setTimestamp(4, toTimestamp(article.getRegDate()));                                    
			pstmt.setTimestamp(5, toTimestamp(article.getModifiedDate()));
			pstmt.setString(6, article.getContent());
			
			int insertedCount = pstmt.executeUpdate();
			
			if(insertedCount >0) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("select max(article_no) from article");
				if(rs.next()) {
					Integer newNum = rs.getInt(1);
					return new Article(newNum, article.getWriter(), article.getTitle(),
							article.getRegDate(), article.getModifiedDate(), article.getContent(), 0);
				}
			}
			return null;
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
			JdbcUtil.close(pstmt);
		}
	}

	private Timestamp toTimestamp(Date date) {
		return new Timestamp(date.getTime());
	}
	
	public int selectCount(Connection conn) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(*) from article");
			if(rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
		}
	}

	public List<Article> select(Connection conn, int startRow, int size) throws SQLException{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("select * from (select rownum as rnum, article_no, member_id, member_name, title, regdate, moddate, content, read_cnt "
					+"from (select * from article order by article_no desc) where rownum <= ?) where rnum >= ?");

			pstmt.setInt(1, startRow+size);
			pstmt.setInt(2, startRow+1);
			rs = pstmt.executeQuery();
			List<Article> result = new ArrayList<>();
			while(rs.next()) {
				result.add(convertArticle(rs));
			}
			return result;
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	private Article convertArticle(ResultSet rs) throws SQLException{
		return new Article(rs.getInt("article_no"),
				new Writer(
						rs.getString("member_id"),
						rs.getString("member_name")),
				rs.getString("title"),
				toDate(rs.getTimestamp("regdate")),
				toDate(rs.getTimestamp("moddate")),
				rs.getString("content"),
				rs.getInt("read_cnt")
				);
	}

	private Date toDate(Timestamp timestamp) {
		return new Date(timestamp.getTime());
	}
	
	public Article selectById(Connection conn, int no) throws SQLException{
		PreparedStatement pstmt=null;
		ResultSet rs= null;
		try {
			pstmt = conn.prepareStatement("select * from article where article_no = ?");
			pstmt.setInt(1, no);
			rs = pstmt.executeQuery(); //쿼리수행
			Article article = null;
			if(rs.next()) {
				article = convertArticle(rs);
			}
			return article;
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	public void increaseReadCount(Connection conn, int no) throws SQLException	{
		try(PreparedStatement pstmt =conn.prepareStatement("update article set read_cnt=read_cnt+1 where article_no = ?")){
			pstmt.setInt(1, no);
			pstmt.executeUpdate();
		}
	}
	
	public int update(Connection conn, int no, String title, String content) throws SQLException{
		try(PreparedStatement pstmt = conn.prepareStatement("update article set title = ?, moddate =systimestamp, content= ? where article_no = ?")){
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setInt(3, no);
			return pstmt.executeUpdate();
		}
	}
	
	public void delete(Connection conn, int no) throws SQLException{
		try(PreparedStatement pstmt = conn.prepareStatement("delete from article where article_no = ?")){
			pstmt.setInt(1, no);
			pstmt.executeUpdate();
		}catch(SQLException e) {
			throw e;
		}
	}
}
