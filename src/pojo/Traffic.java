package pojo;

public class Traffic{
	int id;
	int linkId;
	double speed;
	long fromMs;
	int gcl;
	double width;
	double length;
	double indata;
	double outdata;
	public Traffic(int id, int linkId, double speed, long fromMs, int gcl,
			double width, double length, double indata, double outdata) {
		super();
		this.id = id;
		this.linkId = linkId;
		this.speed = speed;
		this.fromMs = fromMs;
		this.gcl = gcl;
		this.width = width;
		this.length = length;
		this.indata = indata;
		this.outdata = outdata;
	}
	public int getId() {
		return id;
	}
	public int getLinkId() {
		return linkId;
	}
	public double getSpeed() {
		return speed;
	}
	public long getFromMs() {
		return fromMs;
	}
	public int getGcl() {
		return gcl;
	}
	public double getWidth() {
		return width;
	}
	public double getLength() {
		return length;
	}
	public double getIndata() {
		return indata;
	}
	public double getOutdata() {
		return outdata;
	}
	
}