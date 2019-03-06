public class Node{
	
	int g;
	int h;
	int f;
	int search;
	int x;
	int y;
	String psn;
	char c;
	boolean blocked;
	Node parent;
	Node next;
	
	Node(int x, int y){
		this.g = -1;
		this.h = 0;
		this.f = 0;
		this.search = 0;
		this.x = x;
		this.y = y;
		this.psn = new String( x + "x" + y);
		this.c = '0';
		this.blocked = false;
		this.parent = null;
		this.next = null;
	}
	
	Node(int x, int y, String psn){		//really only used for priority queue testing
		this.g = -1;
		this.h = 0;
		this.f = 0;
		this.search = 0;
		this.x = x;
		this.y = y;
		this.psn = new String(psn);
		this.blocked = false;
		this.parent = null;
		this.next = null;
	}
	
	public void makeWall() {
		blocked = true;
		c = 'X';
	}
	
	public boolean isBlocked() {
		return blocked;
	}
	
	@Override
	public String toString() {
		return psn;
	}
	
	@Override
	public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node obj = (Node) o;
        if(this.psn.equals(obj.psn)) {
        	return true;
        }
        else
        	return false;
}
	
	public void resest() {
		this.g = -1;
		//this.h = 0;
		this.f = 0;
		this.parent = null;
		this.next = null;
	}
	
	public void reset() {
		this.g = -1;
		//this.h = 0;
		this.f = 0;
		//this.parent = null;
	}

}
