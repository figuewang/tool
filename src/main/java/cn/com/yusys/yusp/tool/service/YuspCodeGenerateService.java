package cn.com.yusys.yusp.tool.service;

public interface YuspCodeGenerateService {
	void codeGenerate(String tableName, String schema, String jdbcUser, String jdbcPassword, String jdbcUrl,
			String jdbcDriver, String folder, String packageName,String pluginClass) throws Exception;
}
