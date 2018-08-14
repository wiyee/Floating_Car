package pojo;

import java.util.List;

public class Class_Number{
	List<Double> dataList=null;
	List<Integer> valueList=null;
	public Class_Number(List<Double> dataList, List<Integer> valueList) {
		super();
		this.dataList = dataList;
		this.valueList = valueList;
	}
	public List<Double> getDataList() {
		return dataList;
	}
	public List<Integer> getValueList() {
		return valueList;
	}
}