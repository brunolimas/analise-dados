package br.com.limas.analisedados.model;

public class Report {

	private Integer customersSize;
	
	private Integer sellersSize;
	
	private Long mostExpensiveSaleId;
	
	private String worstSellerName;

	public Integer getCustomersSize() {
		return customersSize;
	}

	public void setCustomersSize(Integer customersSize) {
		this.customersSize = customersSize;
	}

	public Integer getSellersSize() {
		return sellersSize;
	}

	public void setSellersSize(Integer sellersSize) {
		this.sellersSize = sellersSize;
	}

	public Long getMostExpensiveSaleId() {
		return mostExpensiveSaleId;
	}

	public void setMostExpensiveSaleId(Long mostExpensiveSaleId) {
		this.mostExpensiveSaleId = mostExpensiveSaleId;
	}

	public String getWorstSellerName() {
		return worstSellerName;
	}

	public void setWorstSellerName(String worstSellerName) {
		this.worstSellerName = worstSellerName;
	}
		
}