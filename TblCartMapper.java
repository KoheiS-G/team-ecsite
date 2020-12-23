package jp.co.internous.eagle.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import jp.co.internous.eagle.model.domain.TblCart;
import jp.co.internous.eagle.model.domain.dto.CartDto;

@Mapper
public interface TblCartMapper {
	
	// ユーザーIDからカート情報を検索する（戻り値はList）
	List<CartDto> findByUserId(
			@Param("userId") int userId
			);
	
	// ユーザーIDでカート情報を検索し、product_idカラムにCartFormで送信された値と同一の値があるかどうかを検索する（戻り値はboolean）
	int findDuplicateInCart(
			@Param("productId") int productId,
			@Param("userId") int userId
			);
	
	// ユーザーIDでカートテーブルを検索し何件のレコードがあるかを返す（戻り値はint）
	// 引数がuserId
	@Select("SELECT count(user_id) FROM tbl_cart WHERE user_id = #{userId}")
	int findCountByUserId(@Param("userId") int userId);
	
	// 仮ユーザーIDをユーザーIDに紐付けする（戻り値はint）
	@Update("UPDATE tbl_cart SET user_id = #{userId}, updated_at = now() WHERE user_id = #{tmpUserId}")
	int updateUserId(@Param("userId") int userId, @Param("tmpUserId") int tmpUserId);
	
	// カートIDでチェックされたカート情報を検索し該当レコードを削除する（戻り値はなし）
	void deleteByCartId(
			@Param("checkedIds") List<String> checkedIds
			);
	
	// ユーザーIDでカート内を検索し該当レコードを削除する（戻り値はint）
	@Delete("DELETE FROM tbl_cart WHERE user_id = #{userId}")
	int deleteByUserId(@Param("userId") int userId);
	
	
	// ユーザーIDと商品IDで検索し、該当のレコードの個数の値を更新する（戻り値はなし）
	@Update("UPDATE tbl_cart SET product_count = product_count + #{productCount}, updated_at = now()" +
			"WHERE user_id = #{userId} AND product_id = #{productId}")
	void update(TblCart cart);
	
	// 新規レコードを挿入する（戻り値はなし）
	@Insert("INSERT INTO tbl_cart ("
			+ "user_id, product_id, product_count "
			+ ") "
			+ "VALUES ("
			+ "#{userId}, #{productId}, #{productCount} "
			+ ")")
	void insert(TblCart cart);
	
}
