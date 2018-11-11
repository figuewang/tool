package cn.com.yusys.yusp.tool.service;

public interface CodeGenerateService {
	/**
	 * 
	 * @param tableName 表名
	 * @param schema
	 * @param jdbcUser 
	 * @param jdbcPassword
	 * @param jdbcUrl
	 * @param jdbcDriver
	 * @param folder  文件生成路径
	 * @param packageName 包名称
	 * @return
	 * @throws Exception
	 */
	void codeGenerate(String tableName,String schema,String jdbcUser,String jdbcPassword,String jdbcUrl,String jdbcDriver,String folder,String packageName) throws Exception;
}
