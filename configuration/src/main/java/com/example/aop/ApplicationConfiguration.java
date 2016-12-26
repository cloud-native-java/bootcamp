package com.example.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ApplicationConfiguration {

	@Bean
	DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.setName("customers")
				.build();
	}

	@Bean
	JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	AroundExampleAspect aroundExampleAspect() {
		return new AroundExampleAspect();
	}

	@Aspect
	public static class AroundExampleAspect {

		private Log log = LogFactory.getLog(getClass());

		@Around("execution(* com.example.aop.CustomerService.*(..))")
		public Object customerServiceAspect(ProceedingJoinPoint joinPoint) throws Throwable {
			LocalDateTime start = LocalDateTime.now();

			log.info("starting @ " + start.toString());
			Throwable toThrow = null;
			Object returnValue = null;

			// <1>
			try {
				returnValue = joinPoint.proceed();
			} catch (Throwable t) {
				toThrow = t;
			}
			LocalDateTime stop = LocalDateTime.now();

			log.info("finishing @ " + stop.toString() + " with duration " +
					stop.minusNanos(start.getNano()).getNano());

			if (null != toThrow) throw toThrow;

			return returnValue;
		}
	}

}
