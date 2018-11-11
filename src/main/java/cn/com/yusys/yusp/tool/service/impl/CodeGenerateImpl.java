package cn.com.yusys.yusp.tool.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.stereotype.Service;

import cn.com.yusys.yusp.tool.service.CodeGenerateService;
@Service
public class CodeGenerateImpl implements CodeGenerateService {
	
	private final Log log = LogFactory.getLog(this.getClass());

	/*public static void main(String[] args) throws Exception {

		String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
		String jdbcUrl = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
		String jdbcUser = "cmis_plat_1";
		String jdbcPassword = "cmis_plat_1";
		String schema = "cmis_plat_1";

		String folder = "F:\\data";
		String packageName = "cn.com.yusys.yusp";
		
		String tableName = "s_usr";
		
		CodeGenerateImpl CodeGenerateImpl = new CodeGenerateImpl();
		CodeGenerateImpl.codeGenerate(tableName, schema, jdbcUser, jdbcPassword, jdbcUrl, jdbcDriver, folder, packageName);
	}
*/
	@Override
	public void codeGenerate(String tableName, String schema, String jdbcUser, String jdbcPassword, String jdbcUrl,
			String jdbcDriver, String folder, String packageName) throws Exception {
		
		File temp = new File(folder);
		if(!temp.exists()){
			temp.mkdirs();
		}
		
		String xmlPackage = packageName+".mapper";
		String beanPackage = packageName+".domain";
		String daoPackage = packageName+".dao";
		
		Configuration configuration = new Configuration();
		Context context = new Context(ModelType.getModelType("flat"));//生成对象的样式
		configuration.addContext(context);
		context.setId("MysqlTables");
		context.setIntrospectedColumnImpl(null);
		context.setTargetRuntime("MyBatis3");
		
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
		log.debug("文件生成成功："+folder);
	}
}
