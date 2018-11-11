package cn.com.yusys.yusp.tool.core.code.dto;

import java.util.List;

public class TableDto {
	private String domainname;
	private String pkColName;
	private List<ColumnDto> cols;
	public String getDomainname() {
		return domainname;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}
	public String getPkColName() {
		return pkColName;
	}
	public void setPkColName(String pkColName) {
		this.pkColName = pkColName;
	}
	public List<ColumnDto> getCols() {
		return cols;
	}
	public void setCols(List<ColumnDto> cols) {
		this.cols = cols;
	}
	
	
}
