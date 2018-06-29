package br.com.limas.analisedados.model;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class Sale {

	private Long id;

	private Item[] items;

	private String sellerName;

	public BigDecimal getTotalValue() {
		return Stream.of(items).map(Item::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Item[]  getItems() {
		return items;
	}

	public void setItems(Item[] items) {
		this.items = items;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

}