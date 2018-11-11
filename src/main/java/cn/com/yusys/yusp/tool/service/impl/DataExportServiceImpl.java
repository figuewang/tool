package cn.com.yusys.yusp.tool.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;

import cn.com.yusys.yusp.tool.core.db.DBService;
import cn.com.yusys.yusp.tool.domain.dto.ColumnDto;
import cn.com.yusys.yusp.tool.domain.dto.TableDto;
import cn.com.yusys.yusp.tool.repository.mapper.DataExportMapper;
import cn.com.yusys.yusp.tool.service.DataBaseService;
import cn.com.yusys.yusp.tool.service.DataExportService;
import cn.com.yusys.yusp.tool.util.FileUtil;
@Service
public class DataExportServiceImpl implements DataExportService {
	private final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private DataExportMapper dataExportMapper;
	
	@Autowired
	private DBService dBService;
	
	@Autowired
	private DataBaseService dataBaseService;
	
	//@Autowired
	//private SimpMessagingTemplate messageSend;
	
	@Override	
	@Async
	@Transactional(readOnly=true)
	public void exportTableData(String taskId,String tableName,TableDto data) throws Exception {
		exportTableDataByPage(taskId,tableName, data);
	}
	
	public void exportTableDataByPage(String taskId, String tableName, TableDto tableDto) throws IOException {

		int total = dataExportMapper.selectTotal(tableName);
		log.debug("总数：" + total);
		//messageSend.convertAndSend("/topic/ip",tableName+",开始导出。。。");
		FileUtil.writeNewContentToFile(System.getProperty("user.dir")+File.separator+taskId+".sql", "--开始导出");
		int totalPage = total / 10 + 1;
		for (int i = 0; i < totalPage; i++) {
			PageHelper.startPage(i + 1, 10);
			List<Map<String, Object>> datas = dataExportMapper.selectAllData(tableName);
			Map<String, ColumnDto> cols = tableDto.getCols();
			for (Map<String, Object> data : datas) {
				StringBuilder colsStrB = new StringBuilder();
				StringBuilder valueStrB = new StringBuilder();
				for (String colName : cols.keySet()) {
					colsStrB = colsStrB.append(colName).append(",");
					Object value = data.get(colName);
					if (null != value) {
						value = "'" + value + "'";
					}
					valueStrB = valueStrB.append(value).append(",");
				}

				String colsStr = colsStrB.toString();
				String valueStr = valueStrB.toString();

				String sql = "INSERT INTO " + tableName.toUpperCase() + " ("
						+ colsStr.substring(0, colsStr.length() - 1) + ") VALUES ("
						+ valueStr.substring(0, valueStr.length() - 1) + ");";
				
				System.out.println(sql);
				FileUtil.writeNewContentToFile(System.getProperty("user.dir")+File.separator+taskId+".sql", sql);				
			}
			//messageSend.convertAndSend("/topic/ip",tableName+"导出中:"+totalPage+"/"+(i + 1));
			System.out.println("-------------------------------------------");
			PageHelper.clearPage();
		}
		FileUtil.writeNewContentToFile(System.getProperty("user.dir")+File.separator+taskId+".sql", "--导出完成 共"+total+"条");
		//messageSend.convertAndSend("/topic/ip","done"+System.getProperty("user.dir")+File.separator+taskId+".sql");		
		//messageSend.convertAndSend("/topic/ip",tableName+"导出完成,"+System.getProperty("user.dir")+File.separator+taskId+".sql");		
	}

}
