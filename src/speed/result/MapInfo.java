package speed.result;

public class MapInfo {
	public int roadId;
	public String roadName;
	public double firstJd;
	public double firstWd;
	public double centerJd;
	public double centerWd;
	public double endJd;
	public double endWd;
	@Override
	public String toString() {
		return "Map [roadId=" + roadId + ", roadName=" + roadName + ", firstJd=" + firstJd + ", firstWd=" + firstWd
				+ ", centerJd=" + centerJd + ", centerWd=" + centerWd + ", endJd=" + endJd + ", endWd=" + endWd + "]";
	}
	public int getRoadId() {
		return roadId;
	}
	public void setRoadId(int roadId) {
		this.roadId = roadId;
	}
	public String getRoadName() {
		return roadName;
	}
	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}
	public double getFirstJd() {
		return firstJd;
	}
	public void setFirstJd(double firstJd) {
		this.firstJd = firstJd;
	}
	public double getFirstWd() {
		return firstWd;
	}
	public void setFirstWd(double firstWd) {
		this.firstWd = firstWd;
	}
	public double getCenterJd() {
		return centerJd;
	}
	public void setCenterJd(double centerJd) {
		this.centerJd = centerJd;
	}
	public double getCenterWd() {
		return centerWd;
	}
	public void setCenterWd(double centerWd) {
		this.centerWd = centerWd;
	}
	public double getEndJd() {
		return endJd;
	}
	public void setEndJd(double endJd) {
		this.endJd = endJd;
	}
	public double getEndWd() {
		return endWd;
	}
	public void setEndWd(double endWd) {
		this.endWd = endWd;
	}
	
}
