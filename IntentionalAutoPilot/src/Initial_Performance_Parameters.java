

//Singleton
public class Initial_Performance_Parameters {
	int initial_altitude;
	int initial_speed;
	double initial_DTD;
	double initial_fpa;
	int initial_vs;
	static Initial_Performance_Parameters m_unique_ipp_obj;
	
	private Initial_Performance_Parameters() {
		/*initial_altitude = 10000;
		initial_speed = 250; //in knots
		initial_DTD = 10;
		initial_vs = 0;
		initial_fpa = 0;*/
		
		//ASIANA AIR 214 initial parameters
		initial_altitude = 2000;
		initial_speed = 190; //in knots
		initial_DTD = 6.2;
		initial_vs = -1000;
		initial_fpa = 0;
	}
	
	public static Initial_Performance_Parameters getInstance(){
		if(m_unique_ipp_obj == null){
			m_unique_ipp_obj = new Initial_Performance_Parameters();
		}
		return m_unique_ipp_obj;
	}
	
	public int get_initial_altitude(){
		return initial_altitude;
	}
	
	//public int get_initial_speed(){
		//return initial_speed;
	//}
	
	public double get_initial_DTD(){
		return initial_DTD;
	}
	
	public int get_initial_vs(){
		return initial_vs;
	}
}

