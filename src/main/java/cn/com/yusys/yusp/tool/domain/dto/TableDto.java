package cn.com.yusys.yusp.tool.domain.dto;

import java.util.Map;

public class TableDto {
	private String schem;
	private String name;
	private String remarks;
	
	private Map<String,ColumnDto> cols;

	public String getSchem() {
		return schem;
	}

	public void setSchem(String schem) {
		this.schem = schem;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Map<String, ColumnDto> getCols() {
		return cols;
	}

	public void setCols(Map<String, ColumnDto> cols) {
		this.cols = cols;
	}
	
}
