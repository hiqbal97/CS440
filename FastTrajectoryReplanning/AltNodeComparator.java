import java.util.Comparator;

public class AltNodeComparator implements Comparator<Node>{

	@Override
	public int compare(Node n1, Node n2) {
		if(n1.f > n2.f)
			return 1;
		if(n1.f < n2.f)
			return -1;
		else {
			if(n1.g < n2.g)
				return -1;
			if(n1.g > n2.g)
				return 1;
			else
				return 0;
		}
	}
	
}