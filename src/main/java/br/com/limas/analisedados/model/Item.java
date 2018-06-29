package br.com.limas.analisedados.model;

import java.math.BigDecimal;


public class Item {

	private Long id;
	
	private Integer quantity;
	
	private BigDecimal price;
	
	
	public BigDecimal getTotalPrice() {
		return price.multiply(BigDecimal.valueOf(quantity));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
}