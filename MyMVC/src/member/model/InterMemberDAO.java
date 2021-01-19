package member.model;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public interface InterMemberDAO {
	
	// 회원가입을 해주는 메소드 (tbl_member 테이블에 insert)
	int registerMember(memberVO member) throws SQLException;
	
	// userid가 존재하면 true 리턴 존재하지 않으면 false 리턴
	boolean idDuplicateCheck(String userid) throws SQLException;
	boolean emailDuplicateCheck(String email) throws SQLException;
	
	// 입력받은 paraMap을 가지고 한명의 회원정보를 리턴시켜주는 메소드이다.(로그인 처리)
	memberVO selectOneMember(Map<String, String> paraMap) throws SQLException, NoSuchAlgorithmException;
	
	// 아이디 찾기
	String findUserid(Map<String, String> paraMap) throws SQLException;

	// 비빌전호 찾기(아이디, 이메일을 입력받아서 해당 사용자가 존재하는지 유무를 알려준다.)
	boolean isUserExist(Map<String, String> paraMap) throws SQLException;

	// 암호 변경하기
	int pwdUpdate(Map<String, String> paraMap) throws SQLException;

	// 회원의 coin 변경하
	int coinUpdate(Map<String, String> paraMap) throws SQLException;

	// 회원의 개인정보 변경하기
	int updateMember(memberVO member) throws SQLException;

	// 페이징 처리를 안한 모든 회원 또는 검색한 회원 목록 보여주기
	ArrayList<memberVO> selectMember(Map<String, String> paraMap) throws SQLException;

	// 페이징 처리를 한 모든 회원 또는 검색한 회원 목록 보여주기
	ArrayList<memberVO> selectPagingMember(Map<String, String> paraMap) throws SQLException;

	// 페이징 처리를 위해서 전체회원에 대한 총페이지 개수 알아오기(select)
	int getTotalPage(Map<String, String> paraMap) throws SQLException;

	// userid 값을 입력받아서 회원1명에 대한 상세정보를 알아오기(select)
	memberVO memberOneDetail(String userid) throws SQLException;



}
