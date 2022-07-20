package com.codingworld.springbatch.config;


import com.codingworld.springbatch.tasks.MyTaskOne;
import com.codingworld.springbatch.tasks.MyTaskTwo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

  @Autowired
  private JobBuilderFactory jobs;

  @Autowired
  private StepBuilderFactory steps;

  @Bean
  public Step stepOne(){
    return steps.get("stepOne")
        .tasklet(new MyTaskOne())
        .build();
  }

  @Bean
  public Step stepTwo(){
    return steps.get("stepTwo")
        .tasklet(new MyTaskTwo())
        .build();
  }

  @Bean(name="demoJobOne")
  public Job demoJobOne(){
    return jobs.get("demoJobOne")
        .start(stepOne())
        .next(stepTwo())
        .build();
  }

  @Bean(name="demoJobTwo")
  public Job demoJobTwo(){
    return jobs.get("demoJobTwo")
        .flow(stepOne())
        .build()
        .build();
  }
}
