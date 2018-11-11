package cn.com.yusys.yusp.tool.config;


import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import cn.com.yusys.yusp.tool.core.db.DBService;

@Configuration
@EnableTransactionManagement
@MapperScan("cn.com.yusys.yusp.tool.repository.mapper")
public class DatabaseConfiguration {

	private final Log log = LogFactory.getLog(this.getClass());

    private final Environment env;
    
    @Value("${spring.datasource.username}")
    private String user = null;
    @Value("${spring.datasource.password}")
    private String password = null;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClass = null;
    @Value("${spring.datasource.url}")
    private String url = null;
    
    @Value("${spring.datasource.schema}")
    private String schema = null;
    
    
    public DatabaseConfiguration(Environment env) {
        this.env = env;
    }

    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("MySQL", "mysql");
        databaseIdProvider.setProperties(properties);
        log.debug("数据源配置完成...");
        return databaseIdProvider;
    }
    
    //特殊的提供元数据获取
    @Bean
    public DBService getDBService(){
    	
    	DBService dBService = new DBService();
    	Properties props =new Properties();
		props.setProperty("user", user);		
		props.setProperty("password", password);		
		props.setProperty("driverClass", driverClass);		
		props.setProperty("url", url);
		props.setProperty("schema",schema);
		props.setProperty("remarks", "true");
		props.setProperty("useInformationSchema", "true");
		
		dBService.setProps(props);
		return dBService;
    }

}
