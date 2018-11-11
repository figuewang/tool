package cn.com.yusys.yusp.tool.web.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.com.yusys.yusp.tool.core.db.DBService;
import cn.com.yusys.yusp.tool.domain.dto.TableDto;
import cn.com.yusys.yusp.tool.service.CodeGenerateService;
import cn.com.yusys.yusp.tool.service.DataBaseService;
import cn.com.yusys.yusp.tool.service.DataExportService;
import cn.com.yusys.yusp.tool.service.YuspCodeGenerateService;
import cn.com.yusys.yusp.tool.util.FileUtil;
import cn.com.yusys.yusp.tool.util.ZipCompress;
import cn.com.yusys.yusp.tool.web.dto.ResultDto;

@RestController
public class ToolIndexResource {

	private final Environment env;

	public ToolIndexResource(Environment env) {
		this.env = env;
	}
	
	@Autowired 
	DataBaseService dataBaseService;
    
	@Autowired
    private DBService dBService;
	
	@Autowired
	private DataExportService dataExportService;
	
	@Autowired
	private CodeGenerateService codeGenerateService;
	
	@Autowired
	private YuspCodeGenerateService yuspCodeGenerateService;
	
	@GetMapping("/")
    public ModelAndView home(Map<String, Object> map) {
		//map.put("name", System.currentTimeMillis());
        ModelAndView res = new ModelAndView("hello");
        return res;
    }
    
	/**
     * 获取表基本信息
     * @param tableName
     * @return
     */
	@GetMapping("/api/tool/tableInfo")
    public ResultDto tableInfo(String tableName)  {        
		ResultDto result = new ResultDto();
		
		String schema = dBService.getProps().getProperty("schema");
		if(null==schema||"".equals(schema)){
			schema = dBService.getProps().getProperty("user");
		}
		
		Object data = null;
		Connection conn = null;
		try {
			conn = dBService.getConnection();
			data = dataBaseService.getOneTableInfo(schema, tableName, conn);
			result.setData(data);
		} catch (SQLException e) {
			result.setCode(1);
			result.setMessage(e.getMessage());
			e.printStackTrace();
		}finally{
			dBService.closeConnection(conn);
		}
				
		return result;
    }
	
	/**
	 * 获取所有表
	 * @return
	 */
	@GetMapping("/api/tool/allTable")
    public ResultDto allTable(int page,int size,String tableName) {    
		ResultDto result = new ResultDto();
		Connection conn = dBService.getConnection();		
		List<TableDto> data = null;
		try {
			String schema = dBService.getProps().getProperty("schema");
			if(null==schema||"".equals(schema)){
				schema = dBService.getProps().getProperty("user");
			}
			data = dataBaseService.getAllTables(conn,schema);
			result.setData(data);
		} catch (Exception e) {
			result.setCode(1);
			result.setMessage(e.getMessage());
			e.printStackTrace();
		}finally{
			dBService.closeConnection(conn);
		}
		
		List<TableDto> dataT = new ArrayList<TableDto>();
		if(tableName!=null&&!"".equals(tableName)){
			for(TableDto tabel:data){
				if(tabel.getName().toUpperCase().contains(tableName.toUpperCase())){
					dataT.add(tabel);
				}
			}
		}else{
			dataT = data;
		}
		
		result.setData(getPagedList( page,  size, dataT));
		result.setTotal(dataT.size());
		return result;
    }
	
	private List<TableDto> getPagedList(int page, int size, List<TableDto> data) {
		
		if(null==data||data.isEmpty()){
			return Collections.emptyList();// 空数组
		}
		
		if (page == 0) {
			page = 1;
		}
		int fromIndex = (page - 1) * size;
		if (fromIndex >= data.size()) {
			return Collections.emptyList();// 空数组
		}
		if (fromIndex < 0) {
			fromIndex = 0;// 空数组
		}
		int toIndex = page * size;
		if (toIndex >= data.size()) {
			toIndex = data.size();
		}
		return data.subList(fromIndex, toIndex);
	}
	
	/**
	 * 导出表中的所有数据，生成文件
	 * @param tableName
	 * @return 返回一个任务id，根据任务id下载
	 * @throws Exception
	 */
	@GetMapping("/api/tool/export")
    public ResultDto export(String tableName) throws Exception {   
		
		String taskId = UUID.randomUUID().toString().replaceAll("-", "");
		Connection conn = dBService.getConnection();
		TableDto data = dataBaseService.getOneTableInfo(dBService.getProps().getProperty("user"), tableName, conn);
		dBService.closeConnection(conn);
		
		dataExportService.exportTableData(taskId,tableName,data);
		ResultDto result = new ResultDto();
		result.setData(taskId);
		return result;
    }
	
	/**
	 * 普通的mybatis-generate代码生成
	 * @param tableName
	 * @param packageName
	 * @return 返回生成路径
	 * @throws Exception
	 */
	@GetMapping("/code")
    public void code(String tableName,String packageName,HttpServletResponse response) throws Exception {  		
		String schema = dBService.getProps().getProperty("schema");
		if(null==schema||"".equals(schema)){
			schema = dBService.getProps().getProperty("user");
		}
		String jdbcUser = dBService.getProps().getProperty("user");
		String jdbcPassword = dBService.getProps().getProperty("password");
		String jdbcUrl = dBService.getProps().getProperty("url");
		String jdbcDriver = dBService.getProps().getProperty("driverClass");
		
		String subFolderName = UUID.randomUUID().toString().replaceAll("-", "");
		String folder = System.getProperty("user.dir")+File.separator+subFolderName;
		codeGenerateService.codeGenerate(tableName, schema, jdbcUser, jdbcPassword, jdbcUrl, jdbcDriver, folder, packageName);
		
		String zipName = System.getProperty("user.dir")+File.separator+subFolderName+".zip";
		ZipCompress.zip(folder,zipName);
		FileUtil.deleteDir(new File(folder));
		
		File zipFile = new File(zipName);
		FileInputStream fileInputStream = new FileInputStream(zipFile);
		byte[] fileByte = fileToByte(fileInputStream, zipFile.length());
		fileInputStream.close();
		
		writeFile( subFolderName+".zip","application/zip",fileByte, response);
		FileUtil.deleteDir(zipFile);
    }
	
	@GetMapping("/yuspcode")
    public void yuspcode(String tableName,String packageName,HttpServletResponse response) throws Exception {  		
		String schema = dBService.getProps().getProperty("schema");
		if(null==schema||"".equals(schema)){
			schema = dBService.getProps().getProperty("user");
		}
		String jdbcUser = dBService.getProps().getProperty("user");
		String jdbcPassword = dBService.getProps().getProperty("password");
		String jdbcUrl = dBService.getProps().getProperty("url");
		String jdbcDriver = dBService.getProps().getProperty("driverClass");
		
		String subFolderName = UUID.randomUUID().toString().replaceAll("-", "");
		String folder = System.getProperty("user.dir")+File.separator+subFolderName;
		yuspCodeGenerateService.codeGenerate(tableName, schema, jdbcUser, jdbcPassword, jdbcUrl, jdbcDriver, folder, packageName,null);
		
		String zipName = System.getProperty("user.dir")+File.separator+subFolderName+".zip";
		ZipCompress.zip(folder,zipName);
		FileUtil.deleteDir(new File(folder));
		
		File zipFile = new File(zipName);
		FileInputStream fileInputStream = new FileInputStream(zipFile);
		byte[] fileByte = fileToByte(fileInputStream, zipFile.length());
		fileInputStream.close();
		
		writeFile( subFolderName+".zip","application/zip",fileByte, response);
		
		FileUtil.deleteDir(zipFile);
    }
	
	@GetMapping("/yuspworkflowcode")
    public void yuspworkflowcode(String tableName,String packageName,HttpServletResponse response) throws Exception {  		
		String schema = dBService.getProps().getProperty("schema");
		if(null==schema||"".equals(schema)){
			schema = dBService.getProps().getProperty("user");
		}
		String jdbcUser = dBService.getProps().getProperty("user");
		String jdbcPassword = dBService.getProps().getProperty("password");
		String jdbcUrl = dBService.getProps().getProperty("url");
		String jdbcDriver = dBService.getProps().getProperty("driverClass");
		
		String subFolderName = UUID.randomUUID().toString().replaceAll("-", "");
		String folder = System.getProperty("user.dir")+File.separator+subFolderName;
		yuspCodeGenerateService.codeGenerate(tableName, schema, jdbcUser, jdbcPassword, jdbcUrl, jdbcDriver, folder, packageName,"cn.com.yusys.yusp.tool.core.code.MapperWithWorkflowPlugin");
		
		String zipName = System.getProperty("user.dir")+File.separator+subFolderName+".zip";
		ZipCompress.zip(folder,zipName);
		FileUtil.deleteDir(new File(folder));
		
		File zipFile = new File(zipName);
		FileInputStream fileInputStream = new FileInputStream(zipFile);
		byte[] fileByte = fileToByte(fileInputStream, zipFile.length());
		fileInputStream.close();
		
		writeFile( subFolderName+".zip","application/zip",fileByte, response);
		
		FileUtil.deleteDir(zipFile);
    }
	
	@GetMapping("/downloadSqlFile")
    public void downloadFile(String path,HttpServletResponse response) throws Exception {  		
		File zipFile = new File(path);
		FileInputStream fileInputStream = new FileInputStream(zipFile);
		byte[] fileByte = fileToByte(fileInputStream, zipFile.length());
		fileInputStream.close();		
		writeFile("xxx.sql","application/html",fileByte, response);		
		FileUtil.deleteDir(zipFile);
    }
	
	private void writeFile(String fileName,String mimeType,byte[] fileByte,HttpServletResponse response) throws IOException{
   	 	response.reset();
        response.setContentType(mimeType + ";charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.getOutputStream().write(fileByte);
        response.getOutputStream().flush();
   }
	
	/**
	 * 文件流转数组
	 * @param inStream
	 * @param fileLength
	 * @return
	 * @throws IOException
	 */
	private byte[] fileToByte(InputStream inStream, long fileLength) throws IOException {

		byte[] buffer = new byte[256 * 1024];
		byte[] fileBuffer = new byte[(int) fileLength];

		int count = 0;
		int length = 0;

		while ((length = inStream.read(buffer)) != -1) {
			for (int i = 0; i < length; ++i) {
				fileBuffer[count + i] = buffer[i];
			}
			count += length;
		}
		return fileBuffer;
	}
		
}