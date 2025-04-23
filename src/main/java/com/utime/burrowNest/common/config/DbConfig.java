package com.utime.burrowNest.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.utime.burrowNest.storage.typeHandler.EArchiveTypeTypeHandler;
import com.utime.burrowNest.storage.typeHandler.EOrientationTypeHandler;
import com.utime.burrowNest.storage.vo.EArchiveType;
import com.utime.burrowNest.storage.vo.EOrientation;

/**
 * DB 환경 정보
 */
@Configuration
public class DbConfig {

	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
		SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
		sqlSessionFactoryBean.setDataSource(dataSource);
		sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:**/mapper/*Mapper.xml"));
		sqlSessionFactoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        sqlSessionFactoryBean.getObject().getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
        
		return sqlSessionFactoryBean.getObject();
	}
	
	@Bean(destroyMethod = "clearCache", name = { "sqlSession" })
	public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
	@Bean
    public ConfigurationCustomizer configurationCustomizer() {
		return configuration -> {
	        configuration.getTypeHandlerRegistry()
	            .register(EArchiveType.class, EArchiveTypeTypeHandler.class);
	        configuration.getTypeHandlerRegistry()
	            .register(EOrientation.class, EOrientationTypeHandler.class);
	    };
    }

}