package com.utime.burrowNest.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.utime.burrowNest.storage.typeHandler.EAccessTypeTypeHandler;
import com.utime.burrowNest.storage.typeHandler.EArchiveTypeTypeHandler;
import com.utime.burrowNest.storage.typeHandler.EOrientationTypeHandler;

/**
 * DB 환경 정보
 */
@Configuration
public class DbConfig {

	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setVfs(SpringBootVFS.class);
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:**/mapper/*Mapper.xml"));
		sessionFactory.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        sessionFactory.getObject().getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
        
        sessionFactory.setTypeHandlers( new TypeHandler[]{
                new EArchiveTypeTypeHandler(),
                new EOrientationTypeHandler(),
                new EAccessTypeTypeHandler()
            });
        
		return sessionFactory.getObject();
	}
	
	@Bean(destroyMethod = "clearCache", name = { "sqlSession" })
	public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
//	@Bean
//    public ConfigurationCustomizer configurationCustomizer() {
//		return configuration -> {
//	        configuration.getTypeHandlerRegistry()
//	            .register(EArchiveType.class, EArchiveTypeTypeHandler.class);
//	        configuration.getTypeHandlerRegistry()
//	            .register(EOrientation.class, EOrientationTypeHandler.class);
//	        configuration.getTypeHandlerRegistry()
//            .register(EAccessType.class, EAccessTypeTypeHandler.class);
//	    };
//    }
	
//	@Bean
//	public ConfigurationCustomizer configurationCustomizer() {
//	    return new ConfigurationCustomizer() {
//	    	
//			@Override
//			public void customize(org.apache.ibatis.session.Configuration configuration) {
//				configuration.getTypeHandlerRegistry()
//					.register(EArchiveType.class, EArchiveTypeTypeHandler.class);
//				configuration.getTypeHandlerRegistry()
//					.register(EOrientation.class, EOrientationTypeHandler.class);
//				configuration.getTypeHandlerRegistry()
//					.register(EAccessType.class, EAccessTypeTypeHandler.class);
//				
//			}
//	    };
//	}

}