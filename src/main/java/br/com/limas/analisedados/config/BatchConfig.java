package br.com.limas.analisedados.config;

import java.beans.PropertyEditorSupport;
import java.util.Collections;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import br.com.limas.analisedados.model.Customer;
import br.com.limas.analisedados.model.Item;
import br.com.limas.analisedados.model.Report;
import br.com.limas.analisedados.model.Sale;
import br.com.limas.analisedados.model.Seller;
import br.com.limas.analisedados.tasklet.FileTasklet;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	private JobBuilderFactory jobs;
 
	@Autowired
	private StepBuilderFactory steps;
	
	@Bean
	public Job fileJob(@Qualifier("fileStep") Step fileStep) {
		return jobs.get("fileJob")
				.incrementer(new RunIdIncrementer())
				.start(fileStep)
				.build();
	}
	
	@Bean
    protected Step fileStep(FileTasklet fileTasklet) {
        return steps.get("fileStep")
        		.tasklet(fileTasklet)
        		.build();
    }
	
	@Bean
    protected FileTasklet fileTasklet() {
		final FileTasklet tasklet = new FileTasklet();
		tasklet.setSellerLineMapper(this.createSellerLineMapper());
		tasklet.setCustomerLineMapper(this.createCustomerLineMapper());
		tasklet.setSaleLineMapper(this.createSaleLineMapper());
		tasklet.setReportLineAggregator(this.createReportLineAggregator());
		return tasklet;
	}

	private LineMapper<Seller> createSellerLineMapper() {
		final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter("ç");
        lineTokenizer.setNames(new String[]{"cpf", "name", "salary"});
        lineTokenizer.setIncludedFields(new int[]{1,2,3});
        
        final BeanWrapperFieldSetMapper<Seller> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Seller.class);
        
        final DefaultLineMapper<Seller> studentLineMapper = new DefaultLineMapper<>();
        studentLineMapper.setLineTokenizer(lineTokenizer);
        studentLineMapper.setFieldSetMapper(fieldSetMapper);
        return studentLineMapper;
	}
	
	
	private LineMapper<Customer> createCustomerLineMapper() {
		final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter("ç");
        lineTokenizer.setNames(new String[]{"cnpj", "name", "businessArea"});
        lineTokenizer.setIncludedFields(new int[]{1,2,3});
       
        final BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);
        
        final DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
	}
	

	private LineMapper<Sale> createSaleLineMapper() {
		final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter("ç");
        lineTokenizer.setNames(new String[]{"id", "items", "sellerName"});
        lineTokenizer.setIncludedFields(new int[]{1,2,3});
        
        final BeanWrapperFieldSetMapper<Sale> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setCustomEditors(Collections.singletonMap(Item[].class, new ItemArrayPropertyEditor()));
        fieldSetMapper.setTargetType(Sale.class);
        
        final DefaultLineMapper<Sale> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
	}
	
	private LineMapper<Item> createItemLineMapper() {
		final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter("-");
        lineTokenizer.setNames(new String[]{"id", "quantity", "price"});
        
        final BeanWrapperFieldSetMapper<Item> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Item.class);
        
        final DefaultLineMapper<Item> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
	}
	
	private LineAggregator<Report> createReportLineAggregator() {
		final BeanWrapperFieldExtractor<Report> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[]{"customersSize", "sellersSize", "mostExpensiveSaleId", "worstSellerName"});
        
		final DelimitedLineAggregator<Report> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setDelimiter("ç");
		lineAggregator.setFieldExtractor(fieldExtractor);
        return lineAggregator;
	}
	
	
	private class ItemArrayPropertyEditor extends PropertyEditorSupport {

		private LineMapper<Item> itemMapper = createItemLineMapper();
		
	    @Override
		public void setAsText(String text) throws IllegalArgumentException {
	    	String[] strs = StringUtils.commaDelimitedListToStringArray(text.substring(1, text.length() - 1));
	    	Item[] value = new Item[strs.length];

	    	for (int i = 0; i < value.length; i++) {
				try {
					value[i] = itemMapper.mapLine(strs[i], i+1);
				}catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
			
			setValue(value);
		}
		
	}
	
}
