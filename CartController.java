package jp.co.internous.eagle.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

import jp.co.internous.eagle.model.domain.TblCart;
import jp.co.internous.eagle.model.domain.dto.CartDto;
import jp.co.internous.eagle.model.form.CartForm;
import jp.co.internous.eagle.model.mapper.TblCartMapper;
import jp.co.internous.eagle.model.session.LoginSession;

@Controller
@RequestMapping("/eagle/cart")
public class CartController {
	
	@Autowired
	private TblCartMapper cartMapper;
	
	@Autowired
	private LoginSession loginSession;
	
	private Gson gson = new Gson();
	
	// カート画面の初期表示メソッド（【出力内容】をhtmlに渡す）
	@RequestMapping("/")
	public String index(Model m) {
		
		// ログイン時はユーザーIDを、未ログイン時は仮ユーザーIDをuserIdに代入する
		int userId = loginSession.getUserId();
		if(userId == 0) {
			userId = loginSession.getTemporaryUserId();
		}
		
		// CartDto型のListである変数cartListにカート情報を代入する
		List<CartDto> cartList = cartMapper.findByUserId(userId);
		
		// Modelにカート情報を格納し、遷移先に渡す
		m.addAttribute("cartList", cartList);
		m.addAttribute("userId", userId);
		m.addAttribute("loginSession", loginSession);
		
		return "cart";
	}
	
	// カート画面でチェックされたカート情報を削除するメソッド
	@SuppressWarnings("unchecked")
	@PostMapping("/delete")
	public String deleteCart(@RequestBody String deleteCartId) {
		
		// ViewからajaxメソッドでPOST送信されたJSONをデコードし、変数に代入する
		Map<String, List<String>> map = gson.fromJson(deleteCartId, Map.class);
		List<String> checkedIds = map.get("deleteCartId");
		
		// カートIDでDBを検索し、カート情報の削除処理をする
		cartMapper.deleteByCartId(checkedIds);
		
		// 初期表示メソッドに処理を渡す
		return "forward:/eagle/cart/";
	}
	
	// 商品をカートに追加するメソッド
	// トップページ、商品詳細画面から遷移したときにこのメソッドを呼び出す
	@RequestMapping("/add")
	public String addCart(CartForm form, Model m) {
		
		// TblCartクラスのメソッドが使いたいのでインスタンス化
		TblCart c = new TblCart();
		
		// TblCartインスタンスにloginSessionとCartFormの値を格納する
		c.setUserId(loginSession.getUserId());
		c.setProductId(form.getProductId());
		c.setProductCount(form.getProductCount());
		
		// 可読性を考慮して変数に直す（このときログイン判定も行う）
		int userId = c.getUserId();
		if(userId == 0) {
			c.setUserId(loginSession.getTemporaryUserId());
			userId = c.getUserId();
		}
		int productId = c.getProductId();
		
		// ユーザーIDと商品IDで重複をチェックする
		// 重複があればUPDATE文を実行し、なければINSERT文を実行する
		int duplicate =  cartMapper.findDuplicateInCart(productId, userId);
		if(duplicate != 0) {
			cartMapper.update(c);
		} else {
			cartMapper.insert(c);
		}
		
		// DBの更新の後に再びSELECT文を実行する
		List<CartDto> cartList = cartMapper.findByUserId(userId);
		
		// Modelに変数cartListを格納し、htmlに渡す
		m.addAttribute("cartList", cartList);
		m.addAttribute("userId", userId);
		m.addAttribute("loginSession", loginSession);
		
		return "cart";
	}
}
