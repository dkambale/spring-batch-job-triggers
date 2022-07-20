package com.codingworld.springbatch.config;
import com.codingworld.springbatch.job.CustomQuartzJob;
import java.io.IOException;
import java.util.Properties;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;


@Configuration
public class QuartzConfig
{
  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private JobLocator jobLocator;

  @Bean
  public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
    jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
    return jobRegistryBeanPostProcessor;
  }


  @Bean
  public JobDetail jobOneDetail() {
    //Set Job data map
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("jobName", "demoJobOne");
    jobDataMap.put("jobLauncher", jobLauncher);
    jobDataMap.put("jobLocator", jobLocator);

    return JobBuilder.newJob(CustomQuartzJob.class)
        .withIdentity("demoJobOne")
        .setJobData(jobDataMap)
        .storeDurably()
        .build();
  }

  @Bean
  public JobDetail jobTwoDetail() {
    //Set Job data map
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("jobName", "demoJobTwo");
    jobDataMap.put("jobLauncher", jobLauncher);
    jobDataMap.put("jobLocator", jobLocator);

    return JobBuilder.newJob(CustomQuartzJob.class)
        .withIdentity("demoJobTwo")
        .setJobData(jobDataMap)
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger jobOneTrigger()
  {
    SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
        .simpleSchedule()
        .withIntervalInSeconds(10)
        .repeatForever();

    return TriggerBuilder
        .newTrigger()
        .forJob(jobOneDetail())
        .withIdentity("jobOneTrigger")
        .withSchedule(scheduleBuilder)
        .build();
  }

  @Bean
  public Trigger jobTwoTrigger()
  {
    SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
        .simpleSchedule()
        .withIntervalInSeconds(20)
        .repeatForever();

    return TriggerBuilder
        .newTrigger()
        .forJob(jobTwoDetail())
        .withIdentity("jobTwoTrigger")
        .withSchedule(scheduleBuilder)
        .build();
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() throws IOException
  {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    scheduler.setTriggers(jobOneTrigger(), jobTwoTrigger());
    scheduler.setQuartzProperties(quartzProperties());
    scheduler.setJobDetails(jobOneDetail(), jobTwoDetail());
    return scheduler;
  }

  @Bean
  public Properties quartzProperties() throws IOException
  {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
    propertiesFactoryBean.afterPropertiesSet();
    return propertiesFactoryBean.getObject();
  }
}
