package br.com.limas.analisedados.model;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class Seller {

	private String cpf;
	
	private String name;
	
	private BigDecimal salary;

	private Sale[] sales;
	
	public BigDecimal getTotalValueSales() {
		return Stream.of(sales).map(Sale::getTotalValue).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}
	
	public void setSales(Sale[] sales) {
		this.sales = sales;
	}
	
	public Sale[] getSales() {
		return sales;
	}

}