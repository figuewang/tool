package cn.com.yusys.yusp.tool.web.dto;

public class ResultDto {
	private int code = 0;
	private String message;
	private int total;
	
	public ResultDto(){}
	
	public ResultDto(Object data){
		this.data =data;
	}
	
	private Object data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	
}
