package cn.com.yusys.yusp.tool.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.stereotype.Service;

import cn.com.yusys.yusp.tool.service.YuspCodeGenerateService;
@Service
public class YuspCodeGenerateServiceImpl implements YuspCodeGenerateService {
	/*public static void main(String[] args) throws Exception {
		YuspCodeGenerateServiceImpl hh = new YuspCodeGenerateServiceImpl();
		String tableName = "example_demo";
		String schema = "yusp";
		String jdbcUser = "yusp";
		String jdbcPassword = "yusp";
		String jdbcUrl = "jdbc:oracle:thin:@192.168.251.158:1521:ycorcl";
		String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
		//String folder = "F:\\workspacecmis4cmismaven4.6-springboot-yusp0820\\yusp-app-framework\\yusp-app-common\\yusp-app-common-micro-starter\\src\\main\\java";
		String folder ="F:\\data";
		String packageName = "cn.com.yusys.yusp.admin";
		hh.codeGenerate(tableName, schema, jdbcUser, jdbcPassword, jdbcUrl, jdbcDriver, folder, packageName,"cn.com.yusys.yusp.tool.core.code.MapperWithWorkflowPlugin");
	}*/
	
	public void codeGenerate(String tableName, String schema, String jdbcUser, String jdbcPassword, String jdbcUrl,
			String jdbcDriver, String folder, String packageName,String pluginClass) throws Exception {
		
		File temp = new File(folder);
		if(!temp.exists()){
			temp.mkdirs();
		}
		
		String xmlPackage = packageName+".repository.mapper";
		String beanPackage = packageName+".domain";
		String daoPackage = packageName+".repository.mapper";
		
		Configuration configuration = new Configuration();
		Context context = new Context(ModelType.getModelType("flat"));//生成对象的样式
		configuration.addContext(context);
		context.setId("MysqlTables");
		context.setIntrospectedColumnImpl(null);
		context.setTargetRuntime("MyBatis3");
		
		PluginConfiguration plugin1 = new PluginConfiguration();
		plugin1.setConfigurationType("org.mybatis.generator.plugins.EqualsHashCodePlugin");
		PluginConfiguration plugin2 = new PluginConfiguration();
		
		if(null==pluginClass||"".equals(pluginClass)){
			plugin2.setConfigurationType("cn.com.yusys.yusp.tool.core.code.MapperPlugin");
		}else{
			plugin2.setConfigurationType(pluginClass);
		}
		
		
		PluginConfiguration plugin3 = new PluginConfiguration();
		plugin3.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
		PluginConfiguration plugin4 = new PluginConfiguration();
		plugin4.setConfigurationType("org.mybatis.generator.plugins.ToStringPlugin");
		
		context.addPluginConfiguration(plugin1);
		context.addPluginConfiguration(plugin2);
		context.addPluginConfiguration(plugin3);
		context.addPluginConfiguration(plugin4);
		
		
		CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();		
		commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
		commentGeneratorConfiguration.addProperty("suppressDate", "true");
		context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
		
		JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
		jdbcConnectionConfiguration.setDriverClass(jdbcDriver);
		jdbcConnectionConfiguration.setConnectionURL(jdbcUrl);
		jdbcConnectionConfiguration.setUserId(jdbcUser);
		jdbcConnectionConfiguration.setPassword(jdbcPassword);
		jdbcConnectionConfiguration.addProperty("remarksReporting", "true");//运行jdbc获取表注释
		context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
		
		JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
		context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
		javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");//数字类型是否统一转换成bigdecimal
		
		JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
		context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);
		javaModelGeneratorConfiguration.setTargetPackage(beanPackage);
		javaModelGeneratorConfiguration.setTargetProject(folder);
		javaModelGeneratorConfiguration.addProperty("enableSubPackages", "false");
		javaModelGeneratorConfiguration.addProperty("trimStrings", "true");
		javaModelGeneratorConfiguration.addProperty("rootClass", "cn.com.yusys.yusp.commons.mapper.domain.BaseDomain");
		
		SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
		context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);
		sqlMapGeneratorConfiguration.setTargetPackage(xmlPackage);
		sqlMapGeneratorConfiguration.setTargetProject(folder);
		sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "false");
		
		JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
		context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
		javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
		javaClientGeneratorConfiguration.setTargetPackage(daoPackage);
		javaClientGeneratorConfiguration.setTargetProject(folder);
		javaClientGeneratorConfiguration.addProperty("enableSubPackages", "false");
		javaClientGeneratorConfiguration.addProperty("methodNameCalculator", "extended");
		
		TableConfiguration tc = new TableConfiguration(context);
		context.addTableConfiguration(tc);
		tc.setTableName(tableName);
		tc.setSchema(schema);
		tc.setConfiguredModelType("flat");
		tc.setSelectByExampleQueryId("false");
		tc.setUpdateByExampleStatementEnabled(false);
		tc.setDeleteByExampleStatementEnabled(false);
		tc.setCountByExampleStatementEnabled(false);
		tc.setSelectByExampleStatementEnabled(false);
		tc.addProperty("constructorBased", "false");
		tc.addProperty("ignoreQualifiersAtRuntime", "true");
		tc.addProperty("selectAllOrderByClause", "id");
							
		List<String> warnings = new ArrayList<String>();					
		DefaultShellCallback callback = new DefaultShellCallback(true);	
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, callback, warnings);
		myBatisGenerator.generate(null);
		
	}
}
