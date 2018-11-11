package cn.com.yusys.yusp.tool.service;

import cn.com.yusys.yusp.tool.domain.dto.TableDto;

public interface DataExportService {
	void exportTableData(String taskId,String tableName,TableDto data) throws Exception;
}
