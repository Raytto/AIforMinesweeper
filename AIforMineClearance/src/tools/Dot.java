package tools;

public class Dot {

	public int x,y;
	public Dot(int x,int y) {
		// TODO Auto-generated constructor stub
		this.x=x;
		this.y=y;
	}
	
	public boolean equals(Dot dot) {
		// TODO Auto-generated method stub
		if(this.x==dot.x&&this.y==dot.y)
			return true;
		return false;
	}
	
	public String toString(){
		return "("+x+","+y+")";
	}

}
