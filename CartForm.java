package jp.co.internous.eagle.model.form;

import java.io.Serializable;

public class CartForm implements Serializable{
	private static final long serialVersionUID = 225104075792455226L;
	
	// 商品IDと個数の属性
	private int productId;
	private int productCount;
	
	// 各属性のゲッターとセッター
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

}
