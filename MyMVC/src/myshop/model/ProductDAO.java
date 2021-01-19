package myshop.model;

import java.sql.*;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import member.model.memberVO;

public class ProductDAO implements InterProductDAO {

	private DataSource ds;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	public ProductDAO() {

		Context initContext;
		try {
			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/mymvc_oracle");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<ImageVO> ImageSelectAll() throws SQLException {

		List<ImageVO> imageList = new ArrayList<ImageVO>();

		try {
			conn = ds.getConnection();

			String sql = "select imgno, imgfilename\n" + "from tbl_main_image\n" + "order by imgno asc";

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ImageVO imgvo = new ImageVO();
				imgvo.setImgno(rs.getInt(1));
				imgvo.setImgfilename(rs.getString(2));

				imageList.add(imgvo);
			}
		} finally {
			close();
		}

		return imageList;
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

	// AJAX를 사용하여 상품목록을 "더보기" 방식으로 페이징 처리를 하기 위해 스펙별로 제품의 전체개수 알아오기.
	@Override
	public int totalPspecCount(String fk_snum) throws SQLException {
		int totalCount = 0;
		try {
			conn = ds.getConnection();

			String sql = "select count(*)\n" + "from tbl_product\n" + "where fk_snum = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, fk_snum);

			rs = pstmt.executeQuery();
			rs.next();
			totalCount = rs.getInt(1);

		} finally {
			close();
		}

		return totalCount;
	}

	@Override
	public ArrayList<ProductVO> selectBySpecName(Map<String, String> paraMap) throws SQLException {
		ArrayList<ProductVO> pdlist = new ArrayList<>();
		try {
			conn = ds.getConnection();

			String sql = "select pnum, pname, code, pcompany, pimage1, pimage2, pqty, price, saleprice, sname, pcontent, point, pinputdate\n"
					+ "from \n" + "(\n" + " select row_number() over(order by pnum desc) AS RNO \n"
					+ "      , pnum, pname, C.code, pcompany, pimage1, pimage2, pqty, price, saleprice, S.sname, pcontent, point  \n"
					+ "      , to_char(pinputdate, 'yyyy-mm-dd') as pinputdate \n" + " from tbl_product P \n"
					+ " JOIN tbl_category C \n" + " ON P.fk_cnum = C.cnum \n" + " JOIN tbl_spec S \n"
					+ " ON P.fk_snum = S.snum\n" + " where S.sname = ? \n" + " ) V\n" + "where RNO between ? and ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("sname"));
			pstmt.setString(2, paraMap.get("start"));
			pstmt.setString(3, paraMap.get("end"));

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ProductVO pvo = new ProductVO();

				pvo.setPnum(rs.getInt(1));
				pvo.setPname(rs.getString(2));

				CategoryVO ct = new CategoryVO();
				ct.setCode(rs.getString(3));
				pvo.setCategvo(ct);
				pvo.setPcompany(rs.getString(4));
				pvo.setPimage1(rs.getString(5));
				pvo.setPimage2(rs.getString(6));
				pvo.setPqty(rs.getInt(7));
				pvo.setPrice(rs.getInt(8));
				pvo.setSaleprice(rs.getInt(9));

				SpecVO sp = new SpecVO();
				sp.setSname(rs.getString(10));
				pvo.setSvo(sp);
				pvo.setPcompany(rs.getString(11));
				pvo.setPoint(rs.getInt(12));
				pvo.setPinputdate(rs.getString(13));

				pdlist.add(pvo);
			}

		} finally {
			close();
		}
		return pdlist;
	}

	@Override
	public ArrayList<HashMap<String, String>> getCategoryList() throws SQLException {

		ArrayList<HashMap<String, String>> categoryList = new ArrayList<>();

		try {
			conn = ds.getConnection();

			String sql = " select cnum, code, cname " + " from tbl_category " + " order by cnum asc ";

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				HashMap<String, String> map = new HashMap<>();
				map.put("cnum", rs.getString(1));
				map.put("code", rs.getString(2));
				map.put("cname", rs.getString(3));

				categoryList.add(map);
			} // end of while(rs.next())----------------------------------

		} finally {
			close();
		}

		return categoryList;
	}

	@Override
	public ArrayList<SpecVO> selectSpecList() throws SQLException {
		ArrayList<SpecVO> speclist = new ArrayList<>();

		try {
			conn = ds.getConnection();

			String sql = "select snum, sname\n" + "from tbl_spec\n" + "order by snum asc";

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				SpecVO svo = new SpecVO();
				svo.setSnum(rs.getInt(1));
				svo.setSname(rs.getString(2));

				speclist.add(svo);
			} // end of while(rs.next())----------------------------------

		} finally {
			close();
		}

		return speclist;
	}

	// 제품번호 채번 해오기
	@Override
	public int getPnumOfProduct() throws SQLException {

		int pnum = 0;

		try {
			conn = ds.getConnection();

			String sql = " select seq_tbl_product_pnum.nextval AS PNUM " + " from dual ";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			rs.next();
			pnum = rs.getInt(1);

		} finally {
			close();
		}

		return pnum;
	}

	// tbl_product_imagefile 테이블에 추가이미지 파일명 insert 해주기
	@Override
	public int product_imagefile_Insert(int pnum, String attachFileName) throws SQLException {

		int result = 0;

		try {
			conn = ds.getConnection();

			String sql = " insert into tbl_product_imagefile(imgfileno, fk_pnum, imgfilename) "
					+ " values(seqImgfileno.nextval, ?, ?) ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, pnum);
			pstmt.setString(2, attachFileName);

			result = pstmt.executeUpdate();

		} finally {
			close();
		}

		return result;

	}

	// tbl_product 테이블에 insert 하기
	@Override
	public int productInsert(ProductVO pvo) throws SQLException {

		int result = 0;

		try {
			conn = ds.getConnection();

			String sql = " insert into tbl_product(pnum, pname, fk_cnum, pcompany, pimage1, pimage2, pqty, price, saleprice, fk_snum, pcontent, point) "
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?)";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, pvo.getPnum());
			pstmt.setString(2, pvo.getPname());
			pstmt.setInt(3, pvo.getFk_cnum());
			pstmt.setString(4, pvo.getPcompany());
			pstmt.setString(5, pvo.getPimage1());
			pstmt.setString(6, pvo.getPimage2());
			pstmt.setInt(7, pvo.getPqty());
			pstmt.setInt(8, pvo.getPrice());
			pstmt.setInt(9, pvo.getSaleprice());
			pstmt.setInt(10, pvo.getFk_snum());
			pstmt.setString(11, pvo.getPcontent());
			pstmt.setInt(12, pvo.getPoint());

			result = pstmt.executeUpdate();

		} finally {
			close();
		}

		return result;
	}

	// 제품번호를 가지고서 해당 제품의 정보를 조회해오기
	@Override
	public ProductVO selectOneProductByPnum(String pnum) throws SQLException {

		ProductVO pvo = null;

		try {
			conn = ds.getConnection();

			String sql = " select S.sname, pnum, pname, pcompany, price, saleprice, point, pqty, pcontent, pimage1, pimage2 "
					+ " from " + " ( "
					+ "  select fk_snum, pnum, pname, pcompany, price, saleprice, point, pqty, pcontent, pimage1, pimage2 "
					+ "  from tbl_product " + "  where pnum = ? " + " ) P JOIN tbl_spec S " + " ON P.fk_snum = S.snum ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pnum);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				String sname = rs.getString(1); // "HIT", "NEW", "BEST" 값을 가짐
				int npnum = rs.getInt(2); // 제품번호
				String pname = rs.getString(3); // 제품명
				String pcompany = rs.getString(4); // 제조회사명
				int price = rs.getInt(5); // 제품 정가
				int saleprice = rs.getInt(6); // 제품 판매가
				int point = rs.getInt(7); // 포인트 점수
				int pqty = rs.getInt(8); // 제품 재고량
				String pcontent = rs.getString(9); // 제품설명
				String pimage1 = rs.getString(10); // 제품이미지1
				String pimage2 = rs.getString(11); // 제품이미지2

				pvo = new ProductVO();

				SpecVO spvo = new SpecVO();
				spvo.setSname(sname);

				pvo.setSvo(spvo);
				pvo.setPnum(npnum);
				pvo.setPname(pname);
				pvo.setPcompany(pcompany);
				pvo.setPrice(price);
				pvo.setSaleprice(saleprice);
				pvo.setPoint(point);
				pvo.setPqty(pqty);
				pvo.setPcontent(pcontent);
				pvo.setPimage1(pimage1);
				pvo.setPimage2(pimage2);

			} // end of if-----------------------------

		} finally {
			close();
		}

		return pvo;
	}

	// 제품번호를 가지고서 해당 제품의 추가된 이미지 정보를 조회해오기
	@Override
	public ArrayList<String> getImagesByPnum(String pnum) throws SQLException {

		ArrayList<String> imgList = new ArrayList<>();

		try {
			conn = ds.getConnection();

			String sql = " select imgfilename " + " from tbl_product_imagefile " + " where fk_pnum = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pnum);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				String imgfilename = rs.getString(1); // 이미지파일명
				imgList.add(imgfilename);
			}

		} finally {
			close();
		}

		return imgList;
	}

	// Ajax 를 이용한 특정 제품의 상품후기를 입력(insert)하기
	@Override
	public int addComment(PurchaseReviewsVO previewvo) throws SQLException {

		int n = 0;

		try {
			conn = ds.getConnection();

			String sql = " insert into tbl_purchase_reviews(review_seq, fk_userid, fk_pnum, contents, writeDate) "
					+ " values(seq_purchase_reviews.nextval, ?, ?, ?, default) ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, previewvo.getFk_userid());
			pstmt.setInt(2, previewvo.getFk_pnum());
			pstmt.setString(3, previewvo.getContents());

			n = pstmt.executeUpdate();

		} finally {
			close();
		}

		return n;
	}

	@Override
	public ArrayList<PurchaseReviewsVO> commentList(String fk_pnum) throws SQLException {

		ArrayList<PurchaseReviewsVO> CommentList = new ArrayList<PurchaseReviewsVO>();

		try {
			conn = ds.getConnection();

			String sql = " select review_seq, name, fk_pnum, contents, to_char(writeDate, 'yyyy-mm-dd hh24:mi:ss') AS writeDate "
					+ " from tbl_purchase_reviews R join tbl_member M " + " on R.fk_userid = M.userid  "
					+ " where R.fk_pnum = ? " + " order by review_seq desc ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(fk_pnum));

			rs = pstmt.executeQuery();

			while (rs.next()) {
				String contents = rs.getString("contents");
				String name = rs.getString("name");
				String writeDate = rs.getString("writeDate");

				PurchaseReviewsVO previewvo = new PurchaseReviewsVO();
				previewvo.setContents(contents);

				memberVO mvo = new memberVO();
				mvo.setName(name);

				previewvo.setMvo(mvo);
				previewvo.setWriteDate(writeDate);

				CommentList.add(previewvo);
			}

		} finally {
			close();
		}

		return CommentList;
	}

	@Override
	public int likeAdd(Map<String, String> paraMap) throws SQLException {
		int n = 0;

		try {
			conn = ds.getConnection();
			
			conn.setAutoCommit(false);	// 수동커밋으로 전환
		
			String sql = "delete from tbl_pro_dislike\n"+
					"where fk_userid = ? and fk_pnum = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("pnum"));
			
			n = pstmt.executeUpdate();
			
			sql = "insert into tbl_product_like(fk_userid, fk_pnum) values(? , ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("pnum"));
			
			n = pstmt.executeUpdate();
			
			if(n == 1){
				conn.commit();
			}

		} catch(SQLIntegrityConstraintViolationException e) {
			conn.rollback();
		}
		finally {
			close();
		}

		return n;
	}

	@Override
	public int dislikeAdd(Map<String, String> paraMap) throws SQLException {
		int n = 0;

		try {
			conn = ds.getConnection();
			
			conn.setAutoCommit(false);	// 수동커밋으로 전환
		
			String sql = "delete from tbl_product_like\n"+
					"where fk_userid = ? and fk_pnum = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("pnum"));
			
			n = pstmt.executeUpdate();
			
			sql = "insert into tbl_pro_dislike(fk_userid, fk_pnum) values(? , ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("pnum"));
			
			n = pstmt.executeUpdate();
			
			if(n == 1){
				conn.commit();
			}

		} catch(SQLIntegrityConstraintViolationException e) {
			conn.rollback();
		}
		finally {
			close();
		}

		return n;
	}

	@Override
	public Map<String, Integer> getLikeDislikeCnt(String pnum) throws SQLException {
		Map<String, Integer> paraMap = new HashMap<String, Integer>();

		try {
			conn = ds.getConnection();

			String sql = "select\n"+
					"    (select count(*)\n"+
					"     from tbl_product_like\n"+
					"     where fk_pnum = ?) as likecnt\n"+
					"   ,( select count(*)\n"+
					"      from tbl_pro_dislike\n"+
					"      where fk_pnum = ?) as dislikecnt\n"+
					"from dual";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pnum);
			pstmt.setString(2, pnum);

			rs = pstmt.executeQuery();

			rs.next();
			
			paraMap.put("likecnt", rs.getInt(1));
			paraMap.put("dislikecnt", rs.getInt(2));

		} finally {
			close();
		}

		return paraMap;
	}

	@Override
	public ArrayList<ProductVO> selectProductByCategory(Map<String, String> paraMap) throws SQLException{
		
		ArrayList<ProductVO> prodList = new ArrayList<>();
		
		try{
			conn = ds.getConnection();
			
			String sql = "select cname, sname, pnum, pname, pcompany, pimage1, pimage2, pqty, price, saleprice, pcontent, point, pinputdate \n"+
					"from \n"+
					"(\n"+
					"    select rownum AS RNO, cname, sname, pnum, pname, pcompany, pimage1, pimage2, pqty, price, saleprice, pcontent, point, pinputdate \n"+
					"    from \n"+
					"    (\n"+
					"        select C.cname, S.sname, pnum, pname, pcompany, pimage1, pimage2, pqty, price, saleprice, pcontent, point, pinputdate \n"+
					"        from \n"+
					"            (select pnum, pname, pcompany, pimage1, pimage2, pqty, price, saleprice, pcontent, point  \n"+
					"                  , to_char(pinputdate, 'yyyy-mm-dd') as pinputdate, fk_cnum, fk_snum   \n"+
					"             from tbl_product  \n"+
					"             where fk_cnum = ? \n"+
					"             order by pnum desc\n"+
					"        ) P \n"+
					"        JOIN tbl_category C \n"+
					"        ON P.fk_cnum = C.cnum \n"+
					"        JOIN tbl_spec S \n"+
					"        ON P.fk_snum = S.snum \n"+
					"    ) V \n"+
					") T \n"+
					"where T.RNO between ? and ?";
			pstmt = conn.prepareStatement(sql);
			
			int currentShowPageNo = Integer.parseInt(paraMap.get("currentShowPageNo"));
			int sizePerPage = 10;
			
			pstmt.setString(1, paraMap.get("cnum"));
			pstmt.setInt(2, (currentShowPageNo * sizePerPage) - (sizePerPage -1));	// 공식
			pstmt.setInt(3, (currentShowPageNo * sizePerPage));
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				ProductVO pvo = new ProductVO();
				
				pvo.setPnum(rs.getInt("pnum"));
				pvo.setPname(rs.getString("pname"));
				
				CategoryVO categvo = new CategoryVO();
				categvo.setCname(rs.getString("cname"));
				
				pvo.setCategvo(categvo);
				pvo.setPcompany(rs.getString("pcompany"));
				pvo.setPimage1(rs.getString("pimage1"));
				pvo.setPimage2(rs.getString("pimage2"));
				pvo.setPqty(rs.getInt("pqty"));
				pvo.setPrice(rs.getInt("price"));
				pvo.setSaleprice(rs.getInt("saleprice"));
				
				SpecVO spvo = new SpecVO();
				spvo.setSname(rs.getString("sname"));
				
				pvo.setSvo(spvo);
				
				pvo.setPcontent(rs.getString("pcontent"));
				pvo.setPoint(rs.getInt("point"));
				pvo.setPinputdate(rs.getString("pinputdate"));
				
				prodList.add(pvo);
				
			}
		}finally {
			close();
		}	
		return prodList;
	}

	@Override
	public int getTotalPage(String cnum) throws SQLException{
		
		int totalPage = 0;
		try {
			conn = ds.getConnection();
			
			String sql = " select ceil( count(*)/10 ) "
						+ " from tbl_product "
						+ " where fk_cnum = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cnum);
			rs = pstmt.executeQuery();
			rs.next();
			totalPage = rs.getInt(1);
		}
		finally {
			close();
		}
		return totalPage;
	}

	@Override
	public List<HashMap<String, String>> selectStoreMap()  throws SQLException{
		List<HashMap<String, String>> storeList = new ArrayList<>();
	      
	      try {
	         conn = ds.getConnection();
	         
	         String sql = " select storeID, storeName, storeUrl, storeImg, storeAddress, lat, lng, zindex " + 
	                    " from tbl_map " + 
	                    " order by zindex asc ";
	         
	         pstmt = conn.prepareStatement(sql);
	         
	         rs = pstmt.executeQuery();
	         
	         while(rs.next()) {
	            HashMap<String, String> map = new HashMap<>();
	            map.put("STOREID", rs.getString("STOREID"));
	            map.put("STORENAME", rs.getString("STORENAME"));
	            map.put("STOREURL", rs.getString("STOREURL"));
	            map.put("STOREIMG", rs.getString("STOREIMG"));
	            map.put("STOREADDRESS", rs.getString("STOREADDRESS"));
	            map.put("LAT", rs.getString("LAT"));
	            map.put("LNG", rs.getString("LNG"));
	            map.put("ZINDEX", rs.getString("ZINDEX"));
	                        
	            storeList.add(map); 
	         }
	         
	      } finally {
	         close();
	      }
	      
	      return storeList;      

	}
}

