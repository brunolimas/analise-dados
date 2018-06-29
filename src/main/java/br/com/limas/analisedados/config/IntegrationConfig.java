package br.com.limas.analisedados.config;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.batch.integration.launch.JobLaunchingMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AbstractDirectoryAwareFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;

@Configuration
public class IntegrationConfig {

	@Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    private DirectChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow sampleFlow() {
        return IntegrationFlows
                .from("fileInputChannel")
                .channel(inputChannel())
                .transform(this::toRequest)
                .handle(jobLaunchingMessageHandler())
                .handle(jobExecution -> System.out.println(jobExecution.getPayload()))
                .get();
    }

    @Bean
    @InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedDelay = "1000"))
    public MessageSource<File> fileReadingMessageSource() {
        CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
        filters.addFilter(new FileFilter());

        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(System.getenv("HOMEPATH") + File.separator + "data" + File.separator + "in" ));
        source.setFilter(filters);
        source.setAutoCreateDirectory(true);
        return source;
    }

    @Transformer
    public JobLaunchRequest toRequest(File input) {
    	File output = new File(System.getenv("HOMEPATH") + File.separator + "data" + File.separator + "out", input.getName().replaceFirst("\\.dat", ".done.dat"));
    	
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("input.file.name", input.getAbsolutePath());
        jobParametersBuilder.addString("output.file.name", output.getAbsolutePath());
        
        jobParametersBuilder.addLong("runTimeMillis", System.currentTimeMillis());
        return new JobLaunchRequest(job, jobParametersBuilder.toJobParameters());
    }

    @Bean
    JobLaunchingMessageHandler jobLaunchingMessageHandler() {
        return new JobLaunchingMessageHandler(jobLauncher);
    }
    
    private static class FileFilter extends AbstractDirectoryAwareFileListFilter<File> {
        
    	
        @Override
        public boolean accept(File input) {
       	
        	if(input.getName().endsWith(".done.dat")) {
        		return false;
        	}
        	
        	if(input.getName().endsWith(".dat") == false) {
        		return false;
        	}
        	
        	File output = new File(System.getenv("HOMEPATH") + File.separator + "data" + File.separator + "out", input.getName().replaceFirst("\\.dat", ".done.dat"));
            return output.exists() == false;
        }


		@Override
		protected boolean isDirectory(File file) {
			return file.isDirectory();
		}
        
    }
}