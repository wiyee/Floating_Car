package pojo;

import java.util.List;

import dbTable.pojo.Group_POJO;

public class GroupList {
	public List<Double> gclGroup;
	public List<Double> speedGroup;
	public List<Double> inDataGroup;
	public List<Double> outDataGroup;
	public GroupList(Group_POJO group, int i) {
		gclGroup=group.getGCL(i);
		speedGroup=group.getSpeed(i);
		inDataGroup=group.getInData(i);
		outDataGroup=group.getOutData(i);
	}
}
