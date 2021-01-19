package myshop.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface InterProductDAO {
	
	// 메인페이지에 보여지는 상품이미지파일명을 모두 조회(select)하는 메소드
	// DTO(Data Transfer Object) == VO(Value Object)
	List<ImageVO> ImageSelectAll() throws SQLException;
	
	// AJAX를 사용하여 상품목록을 "더보기" 방식으로 페이징 처리를 하기 위해 스펙별로 제품의 전체개수 알아오기.
	int totalPspecCount(String fk_snum) throws SQLException;

	ArrayList<ProductVO> selectBySpecName(Map<String, String> paraMap) throws SQLException;

	ArrayList<HashMap<String, String>> getCategoryList() throws SQLException;

	ArrayList<SpecVO> selectSpecList() throws SQLException;

	// 제품번호 채번 해오기
	int getPnumOfProduct() throws SQLException;

	int product_imagefile_Insert(int pnum, String attachFileName) throws SQLException;

	int productInsert(ProductVO pvo) throws SQLException;

	ProductVO selectOneProductByPnum(String pnum) throws SQLException;

	ArrayList<String> getImagesByPnum(String pnum) throws SQLException;

	int addComment(PurchaseReviewsVO reviewsVO) throws SQLException;

	ArrayList<PurchaseReviewsVO> commentList(String fk_pnum) throws SQLException;

	// 특정 회원이 특정 제품에 대해 좋아요에 투표하기(insert)
	int likeAdd(Map<String, String> paraMap) throws SQLException;

	// 특정 회원이 특정 제품에 대해 싫어요에 투표하기(insert)
	int dislikeAdd(Map<String, String> paraMap) throws SQLException;

	// 특정 제품에 대해 좋아요, 싫어요의 투표 결과(select)
	Map<String, Integer> getLikeDislikeCnt(String pnum) throws SQLException;

	// 특정 카테고리에 속하는 제품들을 일반적인 페이징 처리하여 조회(select)해오기
	ArrayList<ProductVO> selectProductByCategory(Map<String, String> paraMap) throws SQLException;

	// 페이지바를 만들기 위해서 특정카테고리의 제품개수에 대한 총페이지수를 알아오기(select)
	int getTotalPage(String cnum) throws SQLException;

	List<HashMap<String, String>> selectStoreMap() throws SQLException;

}
