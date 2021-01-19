package member.model;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

import javax.naming.*;
import javax.sql.DataSource;

import util.security.*;

public class MemberDAO implements InterMemberDAO {

	private DataSource ds;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private AES256 aes;

	public MemberDAO() {

		Context initContext;
		try {
			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/mymvc_oracle");

			aes = new AES256(SecretMyKey.KEY); // SecretMyKey.KEY 은 우리가 만든
												// 비밀키이다.
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	// 자원 반납 메소드
	private void close() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 회원가입을 해주는 메소드(tbl_member 테이블에 insert)
	@Override
	public int registerMember(memberVO member) throws SQLException {
		int result = 0;
		try {
			conn = ds.getConnection();
			String sql = "insert into tbl_member(userid, pwd, name, email, mobile, postcode, address, detailaddress, extraaddress, gender, birthday) "
				 	+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, member.getUserid());
			pstmt.setString(2, Sha256.encrypt(member.getPwd())); // 암호를 SHA256
																	// 알고리즘으로
																	// 단방향 암호화
																	// 시킨다.
			pstmt.setString(3, member.getName());
			pstmt.setString(4, aes.encrypt(member.getEmail()));
			pstmt.setString(5, aes.encrypt(member.getMobile())); // 이메일을 AES256
																	// 알고리즘을
																	// 이요하여 양방향
																	// 암호화 시킨다.
			pstmt.setString(6, member.getPostcode());
			pstmt.setString(7, member.getAddress());
			pstmt.setString(8, member.getDetailaddress());
			pstmt.setString(9, member.getExtraaddress());
			pstmt.setString(10, member.getGender());
			pstmt.setString(11, member.getBirthday());

			result = pstmt.executeUpdate();

		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return result;
	}

	// ID 중복검사 (tbl_member 테이블에서 userid가 존재하면 true를 리턴해주고, userid가 존재하지 않으면
	// false를 리턴한다.)
	@Override
	public boolean idDuplicateCheck(String userid) throws SQLException {

		boolean isExist = false;

		try {
			conn = ds.getConnection();

			String sql = " select userid " + " from tbl_member " + " where userid = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);

			rs = pstmt.executeQuery();

			isExist = rs.next(); // 행이 있으면 true, 없으면 false

		} finally {
			close();
		}
		return isExist;
	}

	// email 중복검사 (tbl_member 테이블에서 email이 존재하면 true를 리턴해주고, email이 존재하지 않으면
	// false를 리턴해준다.)
	@Override
	public boolean emailDuplicateCheck(String email) throws SQLException {
		boolean isExist = false;

		try {
			conn = ds.getConnection();

			String sql = " select email " + " from tbl_member " + " where email = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);

			rs = pstmt.executeQuery();

			isExist = rs.next(); // 행이 있으면 true, 없으면 false

		} finally {
			close();
		}
		return isExist;
	}

	// 입력받은 paraMap을 가지고 한명의 회원정보를 리턴시켜주는 메소드이다.(로그인 처리)
	@Override
	public memberVO selectOneMember(Map<String, String> paraMap) throws SQLException, NoSuchAlgorithmException {

		memberVO member = null;

		try {
			conn = ds.getConnection();
			String sql = "SELECT userid, name, email, mobile, postcode, address, detailaddress, extraaddress, gender, "
					+ "       birthyyyy, birthmm, birthdd, coin, point, registerday, pwdchangegap,  "
					+ "       nvl(lastlogingap, trunc( months_between(sysdate, registerday) ) ) AS lastlogingap "
					+ " FROM  " + " ( "
					+ " select userid, name, email, mobile, postcode, address, detailaddress, extraaddress, gender "
					+ "     , substr(birthday,1,4) AS birthyyyy, substr(birthday,6,2) AS birthmm, substr(birthday,9) AS birthdd "
					+ "     , coin, point, to_char(registerday, 'yyyy-mm-dd') AS registerday "
					+ "     , trunc( months_between(sysdate, lastpwdchangedate) ) AS pwdchangegap "
					+ "from tbl_member\n" + "where status = 1 and  userid = ? and pwd = ? " + ") M  " + "CROSS JOIN  "
					+ "(\n" + "select trunc( months_between(sysdate, max(logindate)) ) AS lastlogingap "
					+ "from tbl_loginhistory " + "where fk_userid = ? " + ") H";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, Sha256.encrypt(paraMap.get("pwd")));
			pstmt.setString(3, paraMap.get("fk_userid"));

			rs = pstmt.executeQuery();

			if (rs.next()) {
				member = new memberVO();
				member.setUserid(rs.getString(1));
				member.setName(rs.getString(2));
				member.setEmail(aes.decrypt(rs.getString(3)));
				member.setMobile(aes.decrypt(rs.getString(4)));
				member.setPostcode(rs.getString(5));
				member.setAddress(rs.getString(6));
				member.setDetailaddress(rs.getString(7));
				member.setExtraaddress(rs.getString(8));
				member.setGender(rs.getString(9));
				member.setBirthday(rs.getString(10) + rs.getString(11) + rs.getString(12));
				member.setCoin(rs.getInt(13));
				member.setPoint(rs.getInt(14));
				member.setRegisterday(rs.getString(15));

				if (rs.getInt(16) >= 3) {
					member.setRequirePwdChange(true); // 로그인 시, 암호를 변경하라는 alert를
														// 띄우도록 한다.
				}

				if (rs.getInt(17) >= 12) {
					// 마지막으로 로그인 한 날짜시간이 현재시각으로 부터 1년이 지났으면 휴먼으로 지정
					member.setIdle(1);

					// tbl_member 테이블의 idle 컬럼의 값을 1로 변경하기
					sql = " update tbl_member set idle = 1 " + " where userid = ? ";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, paraMap.get("userid"));

					pstmt.executeUpdate();
				}

				if (member.getIdle() != 1) {
					// tbl_loginhistory 테이블에 insert 하기
					sql = " insert into tbl_loginhistory(fk_userid, clientip) " + " values(?, ?) ";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, paraMap.get("userid"));
					pstmt.setString(2, paraMap.get("clientip"));

					pstmt.executeUpdate();
				}
			}
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return member;
	}

	// 아이디 찾기(성명, 이메일을 입력받아서 해당 사용자의 아이디를 알려준다.)
	@Override
	public String findUserid(Map<String, String> paraMap) throws SQLException {

		String userid = null;

		try {
			conn = ds.getConnection();

			String sql = " select userid " + " from tbl_member " + " where status = 1 and name = ? and email = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("name"));
			pstmt.setString(2, aes.encrypt(paraMap.get("email")));

			rs = pstmt.executeQuery();

			if (rs.next()) {
				userid = rs.getString(1);
			}

		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return userid;
	}

	// 비밀번호 찾기(아이디, 이메일을 입력받아서 해당 사용자가 존재하는지 유무를 알려준다.)
	@Override
	public boolean isUserExist(Map<String, String> paraMap) throws SQLException {

		boolean isUserExist = false;
		try {
			conn = ds.getConnection();

			String sql = " select userid " + " from tbl_member " + " where status = 1 and userid = ? and email = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, aes.encrypt(paraMap.get("email")));

			rs = pstmt.executeQuery();

			isUserExist = rs.next(); // 행이 있으면 true, 없으면 false

		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return isUserExist;
	}

	// 암호 변경하기
	@Override
	public int pwdUpdate(Map<String, String> paraMap) throws SQLException {

		int result = 0;
		try {
			conn = ds.getConnection();
			String sql = "update tbl_member set pwd = ? " + " where userid = ? ";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, Sha256.encrypt(paraMap.get("pwd")));
			pstmt.setString(2, paraMap.get("userid"));

			result = pstmt.executeUpdate();

		} finally {
			close();
		}
		return result;
	}

	// 회원의 coin 변경하기
	@Override
	public int coinUpdate(Map<String, String> paraMap) throws SQLException {
		int result = 0;
		try {
			conn = ds.getConnection();
			String sql = "update tbl_member set coin = coin + ? , point = point + ? " + "where userid = ? ";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, paraMap.get("coinmoney"));
			pstmt.setInt(2, (int) (Integer.parseInt(paraMap.get("coinmoney")) * 0.01));
			pstmt.setString(3, paraMap.get("userid"));

			result = pstmt.executeUpdate();

		} finally {
			close();
		}
		return result;
	}

	@Override
	public int updateMember(memberVO member) throws SQLException {

		int result = 0;
		try {
			conn = ds.getConnection();
			String sql = "update tbl_member set name = ? " + "                       , pwd = ? "
					+ "                       , email = ? " + "                       , mobile = ? "
					+ "                       , postcode = ? " + "                       , address = ? "
					+ "                       , detailaddress = ? " + "                       , extraaddress = ? "
					+ "where userid = ? ";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, member.getName());
			pstmt.setString(2, Sha256.encrypt(member.getPwd())); /* 단방향 암호화 */
			pstmt.setString(3, aes.encrypt(member.getEmail()));
			pstmt.setString(4, aes.encrypt(member.getMobile()));
			pstmt.setString(5, member.getPostcode());
			pstmt.setString(6, member.getAddress());
			pstmt.setString(7, member.getDetailaddress());
			pstmt.setString(8, member.getExtraaddress());
			pstmt.setString(9, member.getUserid());

			result = pstmt.executeUpdate();

		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}

	// 페이징 처리를 안한 회원목록 보여주기.
	@Override
	public ArrayList<memberVO> selectMember(Map<String, String> paraMap) throws SQLException {

		ArrayList<memberVO> memberList = new ArrayList<>();

		try {
			conn = ds.getConnection();

			String sql = " select userid, name, email, gender " + " from tbl_member " + " where userid != 'admin' ";

			String colname = paraMap.get("searchType");
			String searchWord = paraMap.get("searchWord");

			// 검색 대상이 이메일인 경우
			if ("email".equals(colname)) {
				searchWord = aes.encrypt(searchWord);
			}

			if (searchWord != null && !searchWord.trim().isEmpty()) {
				sql += " and " + colname + " like '%'|| ? ||'%' ";
			}
			sql += " order by registerday desc ";
			pstmt = conn.prepareStatement(sql);

			if (searchWord != null && !searchWord.trim().isEmpty()) {
				pstmt.setString(1, searchWord);
			}
			rs = pstmt.executeQuery();

			while (rs.next()) {

				memberVO mvo = new memberVO();
				mvo.setUserid(rs.getString(1));
				mvo.setName(rs.getString(2));
				mvo.setEmail(aes.decrypt(rs.getString(3))); // 복호화
				// mvo.setEmail("sample@naver.com"); // 복호화
				mvo.setGender(rs.getString(4));

				memberList.add(mvo);
			}

		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return memberList;
	}

	@Override
	public ArrayList<memberVO> selectPagingMember(Map<String, String> paraMap) throws SQLException {

		ArrayList<memberVO> memberList = new ArrayList<>();

		try {
			conn = ds.getConnection();

			String sql = " select userid, name, email, gender "+
					" from "+
					" ( "+
					" select rownum as rno, userid, name, email, gender "+
					"    from "+
					"    ( "+
					"    select userid, name, email, gender "+
					"    from tbl_member "+
					"    where userid != 'admin' ";

			String colname = paraMap.get("searchType");
			String searchWord = paraMap.get("searchWord");

			// 검색 대상이 이메일인 경우
			if ("email".equals(colname)) {
				searchWord = aes.encrypt(searchWord);
			}

			if (searchWord != null && !searchWord.trim().isEmpty()) {
				sql += " and "+colname+" like '%' || ? || '%' ";
			}
			
			sql += "    order by registerday desc "+
					"    )V "+
					")T "+
					" where T.rno between ? and ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			int currentShowPageNo = Integer.parseInt(paraMap.get("currentShowPageNo"));
			int sizePerPage = Integer.parseInt(paraMap.get("sizePerPage"));
			
			// 검색어가 있다면
			if (searchWord != null && !searchWord.trim().isEmpty()) {
				pstmt.setString(1, searchWord);
				pstmt.setInt(2, ( currentShowPageNo * sizePerPage ) - (sizePerPage -1) );
				pstmt.setInt(3, ( currentShowPageNo * sizePerPage ));
			}
			// 검색어가 없다면
			else {
				pstmt.setInt(1, ( currentShowPageNo * sizePerPage ) - (sizePerPage -1) );
				pstmt.setInt(2, ( currentShowPageNo * sizePerPage ));
			}
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {

				memberVO mvo = new memberVO();
				mvo.setUserid(rs.getString(1));
				mvo.setName(rs.getString(2));
				mvo.setEmail(aes.decrypt(rs.getString(3))); // 복호화
				// mvo.setEmail("sample@naver.com"); // 복호화
				mvo.setGender(rs.getString(4));

				memberList.add(mvo);
			}
			
			System.out.println(memberList.size());
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return memberList;

	}

	
	// 페이징 처리를 위해서 전체 회원에 대한 총 페이지 개수를  알아오기(select)===================================================================
	   @Override
	   public int getTotalPage(Map<String, String> paraMap) throws SQLException {
	      
	      int totalPage = 0;
	      
	      try {
	         
	         conn = ds.getConnection();
	         
	         String sql = " select ceil(count(*)/ ? ) "
	                  + " from tbl_member " 
	                  + " where userid != 'admin' ";
	         
	         String colname = paraMap.get("searchType"); // 맨 처음에는 null 값이 들어간다. 
	         String searchWord = paraMap.get("searchWord");
	               
	         if("email".equals(colname)) {
	            // 검색대상이 email인 경우, 암호화 시키자.
	            searchWord = aes.encrypt(searchWord); // 복호화
	            
	         }
	         
	         if(searchWord != null && !searchWord.trim().isEmpty()) {
	            // 검색어가 null이 아니어야 한다.(무엇인가 입력한 경우) 그리고 searchWord.trim() 공백 제거했을때 isEmpty가 아니어야 한다.(공백을 지웠는데도 아무것도 없으면 안된다.)
	            sql += " and "+ colname +" like '%'|| ? || '%'  ";
	         }
	         
	         pstmt = conn.prepareStatement(sql);
	         
	         pstmt.setString(1, paraMap.get("sizePerPage"));
	         
	         if(searchWord != null && !searchWord.trim().isEmpty()) {
	            // 검색어가 null이 아닌 경우 Mapping (검색어가 있는 경우)
	            pstmt.setString(2, searchWord); 
	         }
	         
	         rs = pstmt.executeQuery();
	            
	         rs.next();
	         
	         totalPage = rs.getInt(1); // 첫번째 컬럼인  결과 받아서 totalPage에 넘겨주자
	         
	      } catch(GeneralSecurityException | UnsupportedEncodingException e) {
	         e.printStackTrace();
	      
	      } finally {
	         close();
	      }
	      
	      return totalPage;
	   }

	@Override
	public memberVO memberOneDetail(String userid) throws SQLException {
		memberVO mvo = null;
		try {
			conn = ds.getConnection();

			String sql = " SELECT userid, name, email, mobile, postcode, address, detailaddress, extraaddress, gender, "
					+ "       substr(birthday,1,4) as birthyyyy, substr(birthday,6,2) as birthmm, substr(birthday,9) asbirthdd "
					+ "		, coin, point, to_char(registerday, 'yyyy-mm-dd') as registerday "
					+ " from tbl_member " 
					+ " where userid = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);

			rs = pstmt.executeQuery();

			if(rs.next()) {
				mvo = new memberVO();
				mvo.setUserid(rs.getString(1));
				mvo.setName(rs.getString(2));
				mvo.setEmail(aes.decrypt(rs.getString(3))); // 복호화
				mvo.setMobile(aes.decrypt(rs.getString(4)));
				mvo.setPostcode(rs.getString(5));
				mvo.setAddress(rs.getString(6));
				mvo.setDetailaddress(rs.getString(7));
				mvo.setExtraaddress(rs.getString(8));
				mvo.setGender(rs.getString(9));
				mvo.setBirthday(rs.getString(10) + rs.getString(11) + rs.getString(12));
				mvo.setCoin(rs.getInt(13));
				mvo.setPoint(rs.getInt(14));
				mvo.setRegisterday(rs.getString(15));
			}

		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return mvo;
	}
	   

}
