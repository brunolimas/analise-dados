package br.com.limas.analisedados.tasklet;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;

import br.com.limas.analisedados.model.Customer;
import br.com.limas.analisedados.model.Report;
import br.com.limas.analisedados.model.Sale;
import br.com.limas.analisedados.model.Seller;

public class FileTasklet implements Tasklet {

	private LineMapper<Customer> customerLineMapper;
	
	private LineMapper<Seller> sellerLineMapper;
	
	private LineMapper<Sale> saleLineMapper;
	
	private LineAggregator<Report> reportLineAggregator;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
	
		final File file = new File((String) chunkContext.getStepContext().getJobParameters().get("input.file.name"));
		Assert.notNull(file, "file not found");
		
		List<Customer> customers = new ArrayList<>();
		List<Seller> sellers = new ArrayList<>();
		List<Sale> sales = new ArrayList<>();
		
		final LineIterator iterator = FileUtils.lineIterator(file, "iso-8859-1");
		
		while(iterator.hasNext()){
			final String line = iterator.next();
			contribution.incrementReadCount();
			
			if(line.startsWith("001")) {
				sellers.add(sellerLineMapper.mapLine(line, contribution.getReadCount()));
			
			}else if(line.startsWith("002")) {
				customers.add(customerLineMapper.mapLine(line, contribution.getReadCount()));
				
			}else if(line.startsWith("003")) {
				sales.add(saleLineMapper.mapLine(line, contribution.getReadCount()));
			}

		}
		
		sellers.forEach(seller -> { 
			seller.setSales(sales.stream().filter(sale -> sale.getSellerName().equals(seller.getName())).toArray(Sale[]::new));
		});
		
		Sale mostExpensiveSale = sales
			      .stream()
			      .max(Comparator.comparing(Sale::getTotalValue))
			      .orElse(null);
		
		Seller worstSeller = sellers
				.stream()
				 .min(Comparator.comparing(Seller::getTotalValueSales))
				 .orElse(null);
		
		final Report report = new Report();
		report.setCustomersSize(customers.size());
		report.setSellersSize(sellers.size());
		report.setMostExpensiveSaleId(mostExpensiveSale != null ? mostExpensiveSale.getId() : null);
		report.setWorstSellerName(worstSeller != null ? worstSeller.getName() : null);
		
		final File output = new File((String) chunkContext.getStepContext().getJobParameters().get("output.file.name"));
		FileUtils.write(output, reportLineAggregator.aggregate(report));
		return RepeatStatus.FINISHED;
	}
	
	public void setCustomerLineMapper(LineMapper<Customer> customerLineMapper) {
		this.customerLineMapper = customerLineMapper;
	}
	
	public void setSaleLineMapper(LineMapper<Sale> saleLineMapper) {
		this.saleLineMapper = saleLineMapper;
	}
	
	public void setSellerLineMapper(LineMapper<Seller> sellerLineMapper) {
		this.sellerLineMapper = sellerLineMapper;
	}
	
	public void setReportLineAggregator(LineAggregator<Report> reportLineAggregator) {
		this.reportLineAggregator = reportLineAggregator;
	}
	
}