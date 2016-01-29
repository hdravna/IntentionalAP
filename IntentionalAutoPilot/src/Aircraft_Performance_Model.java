/*
Author: Anvardh Nanduri
Organisation: CATSR, GMU
Supervisor: Lance Sherry
Date: September 2014
Code for Intentional Auto Pilot Prototype. No part of this software code can be reused in any form without prior approval of the author.
email: anvardh@gmail.com
*/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;

//Singleton
public class Aircraft_Performance_Model extends Observable implements ActionListener{

	private int m_current_altitude;
	private double m_current_speed;
	private double m_current_DTD;
	private double m_current_fpa;
	private double m_acceleration;
	private int m_current_vs;
	private int m_target_altitude;
	private int m_target_speed;
	private int m_target_vs;
	private double m_pitch;
	private double m_dist_in_current_time_interval;
	private double m_fpa; //flight path angle (gamma)
	private double m_angle_of_attack; //AoA (alpha)
	private String m_action;
	private boolean m_is_glidepath_captured_b = false;
	private int m_throttles_value;
	
	//faf distance from Destination RW
	private double m_faf_from_RW;
	private ArrayList<Integer> m_glide_slope_alt_targets;
	
	//MyPanel m_panel_obj;
	MCPParameters m_mcpParameters;
	static int counter = 15; //increment-button's timer 15 secs hard-coded (TODO MAKE THIS PRIVATE)
	static int i =1;
	static Aircraft_Performance_Model m_unique_apm_obj;
	private boolean m_is_ac_within_capture_region_b;
	private boolean m_is_ac_in_FAF_b;
	private boolean m_is_ap_engaged_b;
	private boolean m_is_at_engaged_b;
	private boolean m_is_at_in_hold_b;
	private boolean m_is_at_in_retard_b;
	private boolean m_is_vnav_engaged_b;
	private int m_altitude_diff;
	
	private ArrayList<Integer> m_cas_targets_vector;
	private ArrayList<Integer> m_altitude_targets_vector;
	private ArrayList<Integer> m_vs_targets_vector;
	private ArrayList<Integer> m_at_engagement_status_vector;
	private ArrayList<Integer> m_ap_engagement_status_vector;
	private ArrayList<Integer> m_at_hold_status_vector;
	private ArrayList<Integer> m_at_retard_status_vector;
	private ArrayList<Integer> m_throttle_values_vector;
	
	
	private int m_ac_position_wrt_gs_target; // 0 if current alt above GS tgt
	// 1 if equal
	// 2 if current alt below GS tgt
	private double m_cas_to_tas_conversion_table[] = {1,
													0.9854,
													0.971,
													0.9566,
													0.9424,
													0.9283,
													0.9143,
													0.9004,
													0.8866,
													0.8729,
													0.8593,
													0.8459,
													0.8326,
													0.8193,
													0.8062,
													0.7932,
													0.7804,
													0.7676,
													0.7549,
													0.7424,
													0.7299,
													0.73,
													0.726,
													0.715,
													0.7048,
													0.694,
													0.683,
													0.673,
													0.6628,
													0.6617,
													0.6402,
													0.6290,
													0.6179,
													0.6071,
													0.5961,
};

	private Aircraft_Performance_Model(Initial_Performance_Parameters ipp_obj, MyPanel mypanel){
		m_mcpParameters = MCPParameters.getInstance();
		
		/*m_current_altitude = ipp_obj.initial_altitude;
		m_current_speed = ipp_obj.initial_speed;
		m_current_DTD = ipp_obj.initial_DTD;
		m_current_vs = ipp_obj.initial_vs;*/
		
		m_current_altitude = m_mcpParameters.getInitialAlt();
		m_current_speed = m_mcpParameters.getInitialSpeed();
		m_current_DTD = m_mcpParameters.getInitialDTD();
		m_current_vs = m_mcpParameters.getInitialVS();
				
		m_target_altitude = m_mcpParameters.get_target_mcpaltitude();
		m_target_speed = m_mcpParameters.get_target_CAS();
		m_faf_from_RW = m_mcpParameters.get_faf_distance_from_rw();
		m_glide_slope_alt_targets = new ArrayList<Integer>();
		//compute_glideslope_alt_targets();
		
		set_cas_targets_vector(new ArrayList<Integer>());
		set_altitude_targets_vector(new ArrayList<Integer>());
		set_at_engagement_status_vector(new ArrayList<Integer>());
		set_ap_engagement_status_vector(new ArrayList<Integer>());
		set_at_hold_status_vector(new ArrayList<Integer>());
		set_at_retard_status_vector(new ArrayList<Integer>());
		set_throttle_values_vector(new ArrayList<Integer>());
		set_vs_targets_vector(new ArrayList<Integer>());
	}
	
	public static Aircraft_Performance_Model getInstance(Initial_Performance_Parameters ipp_obj, MyPanel mypanel){
		if(m_unique_apm_obj == null)
		{
			m_unique_apm_obj = new Aircraft_Performance_Model(ipp_obj, mypanel);
		}
		return m_unique_apm_obj;
	}
	
	public ArrayList<Integer> get_glideslope_alt_target()
	{
		return m_glide_slope_alt_targets;
	}
	public void refresh_parameters(){
		compute_glideslope_alt_targets();
		if(!m_is_ac_in_FAF_b || !m_is_vnav_engaged_b)
		{
			m_target_altitude = m_mcpParameters.get_target_mcpaltitude();
		}
		m_target_speed = m_mcpParameters.get_target_CAS();
		m_target_vs = m_mcpParameters.get_target_VS();
		refresh_target_vs(); //fpm 
	}
	
	private void refresh_target_vs(){

		if(!is_ac_in_FAF() || (is_ac_in_FAF() && !m_mcpParameters.getIsVNAVEngaged()))
		{
		if(m_target_altitude - m_current_altitude > 0)
		{
			m_target_vs = 2000;
		}
		if(m_target_altitude - m_current_altitude < 0)
		{
			m_target_vs = -2000;
		}
		}
	}
	
	public void compute_performance_parameters(){
		//refresh_parameters();
		compute_dtd();
		compute_altitude();
		compute_speed_in_cas();
		compute_vs();
		compute_pitch();
		assess_ac_position_for_gs();
	}

	private int get_glideslope_alt_tgt_for_current_dtd(int index)
	{
		return m_glide_slope_alt_targets.get(index); 
	}
	private void assess_ac_position_for_gs()
	{
		int index = (int)(m_current_DTD*100);
		int glide_slope_target = get_glideslope_alt_tgt_for_current_dtd(index);
		int glide_slope_tolerance = 10; //feet
		
		
		if(m_current_altitude > glide_slope_target+glide_slope_tolerance)
		{
			m_ac_position_wrt_gs_target = 0;
			m_is_glidepath_captured_b = false;
		}
		else if(m_current_altitude < glide_slope_target-glide_slope_tolerance)
		{
			m_ac_position_wrt_gs_target = 2;
			m_is_glidepath_captured_b = false;
		}
		else if(m_current_altitude <= glide_slope_target + glide_slope_tolerance && m_current_altitude >= glide_slope_target - glide_slope_tolerance)
		{
			m_ac_position_wrt_gs_target = 1;
			m_is_glidepath_captured_b = true;
		}
	}
	
	public int get_ac_position_wrt_gs_tgt()
	{
		return m_ac_position_wrt_gs_target;
	}
	private void compute_glideslope_alt_targets(){
		int size = (int) (m_faf_from_RW*100);
		double dist_in_nm = 0.0; //dtd in nm
		double dist_in_ft = 0.0; //dtd in ft
		int x= 0;
		double tan_3 = Math.tan(Math.toRadians(3.0)); //in degrees
		
		for (int i = size; i > 0; i--)
		{
			dist_in_nm = (m_faf_from_RW - (0.01*i)); //in nm
			//1 nm = 6076.12 feet
			dist_in_ft = dist_in_nm*(6076.12);
			x = (int) (tan_3 * dist_in_ft);
			m_glide_slope_alt_targets.add(x);
		}	
	}
	private void compute_pitch(){
		if(m_is_ap_engaged_b || (!m_is_ap_engaged_b && m_is_at_in_hold_b))
		{
			double vs_in_fps = m_current_vs/60;
			double cas_in_fps = 1.68 * m_current_speed;
			//double tas_in_fps = 
			double fpa_in_rad = Math.asin(vs_in_fps/cas_in_fps);
			m_angle_of_attack = 0;
			m_fpa = Math.toDegrees(fpa_in_rad); //gamma- flight path angle
			m_pitch = m_fpa + m_angle_of_attack; //pitch angle (theta) = Angle of attack (alpha) + flight path angle
		}		
	}

	private int update_and_get_current_vs(){

		if(m_mcpParameters.get_is_scenario_specific())
		{
			m_current_vs = m_mcpParameters.get_current_VS();
		}
		else
		{
			m_altitude_diff = m_target_altitude - m_current_altitude;

			if(m_is_ap_engaged_b || !m_mcpParameters.get_is_scenario_specific())
			{
				if(!m_is_ac_in_FAF_b || (m_is_ac_in_FAF_b && !m_mcpParameters.getIsVNAVEngaged()))
				{
					if(Math.abs(m_altitude_diff) != 0)
					{
						if(!is_in_capture_region())
						{
							if((m_target_vs - m_current_vs >= 0 && m_target_vs - m_current_vs <= 200) ||
									(m_current_vs - m_target_vs >= 0 && m_current_vs - m_target_vs <= 200))
							{
								m_current_vs = m_target_vs;
							}
							else if(m_target_vs - m_current_vs > 200)
							{
								m_current_vs = m_current_vs + 200;
							}

							else if(m_current_vs - m_target_vs > 200)
							{
								m_current_vs = m_current_vs - 200;
							}
						}

						else //is in capture region
						{
							if(m_altitude_diff > 0)
							{
								if(m_altitude_diff > 400)
								{
									m_current_vs = 1600;
								}
								else if(m_altitude_diff > 60 && m_altitude_diff <= 400)
								{
									m_current_vs = 800;
								}

								else if(m_altitude_diff > 10 && m_altitude_diff <= 60)
								{
									m_current_vs = 200;
								}

								else if(m_altitude_diff > 0 && m_altitude_diff <= 10)
								{
									m_current_vs = 60;
								}
							}

							else if (m_altitude_diff < 0)
							{
								if(m_altitude_diff < -400)
								{
									m_current_vs = -1600;
								}
								else if(m_altitude_diff < -60 && m_altitude_diff >= -400)
								{
									m_current_vs = -800;
								}

								else if(m_altitude_diff < -10 && m_altitude_diff >= -60)
								{
									m_current_vs = -200;
								}

								else if(m_altitude_diff < 0 && m_altitude_diff >= -10)
								{
									m_current_vs = -60;
								}
							}
						}
					}
				}

				else if(m_is_ac_in_FAF_b && m_is_vnav_engaged_b)
				{
					if(Math.abs(m_altitude_diff) >= 40)
					{
						if(m_altitude_diff < 0)
						{
							m_target_vs = - (40 * 60);					
						}
						else if (m_altitude_diff > 0)
						{
							m_target_vs = 40*60;
						}
						m_current_vs = m_target_vs;
					}
					else if(Math.abs(m_altitude_diff) < 40)
					{
						m_target_vs = m_altitude_diff * 60;					
						m_current_vs = m_target_vs;
					}
				}
			}

			else //if ap_not_engaged && not flying scenario specific
			{
				double current_speed_in_fpm = (1.6878)*(m_current_speed) * 60;
				double pitch_in_rad = Math.toRadians(m_pitch);
				double sine_pitch = Math.sin(pitch_in_rad);
				m_current_vs = (int) (current_speed_in_fpm * sine_pitch);
			}
		}


		return m_current_vs;
	}

	private void compute_altitude(){

		if(m_is_ac_in_FAF_b && m_mcpParameters.getIsVNAVEngaged())
		{
			int index = (int)(m_current_DTD*100);
			m_target_altitude = get_glideslope_alt_tgt_for_current_dtd(index);
		}
		if(m_current_altitude - m_target_altitude == 0)
		{
			m_current_altitude = m_target_altitude;
			update_and_get_current_vs();
		}
		else
		{
			int vs = 0;
			vs = update_and_get_current_vs();
			int rate_of_climb = vs/60;
			m_current_altitude = m_current_altitude + rate_of_climb;
		}
	}
	public void set_is_ap_engaged(boolean is_ap_set_b)
	{
		m_is_ap_engaged_b = is_ap_set_b;
		m_mcpParameters.setIsAPEngaged(m_is_ap_engaged_b);
	}
	
	public void set_is_at_engaged(boolean is_at_set_b)
	{
		m_is_at_engaged_b = is_at_set_b;
		m_mcpParameters.set_is_at_engaged_b(m_is_at_engaged_b);
	}
	
	public void set_is_vnav_engaged(boolean is_vnav_engaged_b)
	{
		m_is_vnav_engaged_b = is_vnav_engaged_b;
		m_mcpParameters.setIsVNAVEngaged(m_is_vnav_engaged_b);
	}
	
	public void set_pitch(int pitch){
		m_pitch = pitch;
	}
	
	public double get_pitch(){
		return m_pitch;
	}
	
	public boolean is_in_capture_region(){
		//capture altitude is altitude remaining to the assigned altitude or vertical path
		double capture_altitude = 0.0;
		double error_in_vs = m_target_vs/60.0;//m_target_vs - m_current_vs;
		final double g = 32.2; //units (ft/sec*sec)
		final double C = 0.05;
		double He = m_target_altitude - m_current_altitude;
		capture_altitude = ((error_in_vs) * Math.abs(error_in_vs))/(2*C*g); 
		if((He > 0) && (He < capture_altitude)||
				((He < 0) && (He > capture_altitude)))
		{
			m_is_ac_within_capture_region_b = true;
		}
		else
		{
			m_is_ac_within_capture_region_b = false;
		}
		return m_is_ac_within_capture_region_b;
	}
	private double update_and_get_current_speed(){
	
		if(m_mcpParameters.get_is_at_engaged_b() && !(m_mcpParameters.get_is_at_in_hold_mode() || m_mcpParameters.get_is_at_in_retard_mode()))
		{
			if((m_target_speed - m_current_speed >= 0 && m_target_speed - m_current_speed <= 2) ||
					(m_current_speed - m_target_speed >= 0 && m_current_speed - m_target_speed <= 2))
			{
				m_current_speed = m_target_speed;
				i = 1;
			}

			else if(m_target_speed - m_current_speed > 2 )
			{
				if(m_target_speed - m_current_speed >= 100)
				{
					//accelerate to tgt @ 0.05g's speed (0.05 g is ~ 1 knot/sec) and keep decreasing dtd
					m_current_speed = m_current_speed + (0.05 * i);
				}

				else if(m_target_speed - m_current_speed >= 20 && m_target_speed - m_current_speed < 100)
				{
					m_current_speed = m_current_speed + (0.04 * i);
				}

				else if(m_target_speed - m_current_speed >= 10 && m_target_speed - m_current_speed < 20)
				{
					m_current_speed = m_current_speed + (0.04 * i);
				}

				else if(m_target_speed - m_current_speed > 2 && m_target_speed - m_current_speed < 10)
				{
					m_current_speed = m_current_speed + (0.03 * i);
				}
			}

			else if(m_current_speed - m_target_speed > 2 ) //Decelerate to tgt @ 0.05g's speed and keep decreasing dtd
			{
				if(m_current_speed - m_target_speed >= 100)
				{
					//accelerate to tgt @ 0.05g's speed and keep decreasing dtd
					m_current_speed = (m_current_speed - (0.05 * i));
				}
				else if(m_current_speed - m_target_speed  >= 20 && m_current_speed - m_target_speed  < 100)
				{
					m_current_speed = (m_current_speed - (0.04 * i));
				}
				else if(m_current_speed - m_target_speed  >= 10 && m_current_speed - m_target_speed  < 20)
				{
					m_current_speed = (m_current_speed - (0.04 * i));
				}
				else if(m_current_speed - m_target_speed  > 2 && m_current_speed - m_target_speed < 10)
				{
					m_current_speed = (m_current_speed - (0.03 * i));
				}
			}
			i++;
		}

		else //determined by throttles
		{
			if(m_current_speed < 110 || 
					(m_mcpParameters.get_is_scenario_specific() && 
								(m_mcpParameters.get_is_at_in_hold_mode() || m_mcpParameters.get_is_at_in_retard_mode())))
			{	
				/*if(m_current_speed < 100)
				{
					m_current_speed = m_current_speed - 5;
				}*/
				
				//reduce 1 kt per 2 seconds
				if(m_current_speed >= 145)
				{
					if(i % 2 == 0)
					{
						m_current_speed = m_current_speed - 1;
					}
				}
				//reduce 1 knot per second
				else
				{
					m_current_speed = m_current_speed - 1;
				}
					i = i+1;
			}				 
		}
		return m_current_speed;
	}
	private void compute_dtd(){
		double current_speed = update_and_get_current_speed();
		m_dist_in_current_time_interval = current_speed/3600;
		m_current_DTD = (double)(m_current_DTD - m_dist_in_current_time_interval);
		m_current_DTD = Math.round(m_current_DTD*100.0)/100.0;
		
		if(m_current_DTD <= m_faf_from_RW)
		{
			m_is_ac_in_FAF_b = true;
		}
		else
		{
			m_is_ac_in_FAF_b = false;
		}
	}

	public double get_distance_covered_in_time_slice()
	{
		return m_dist_in_current_time_interval;
	}
	
	public boolean is_ac_in_FAF()
	{
		return m_is_ac_in_FAF_b;
	}
	
	private void compute_speed_in_cas(){
		
	}
	
	/*private void compute_altitude_in_ft(){
		
	}*/
	
	private void compute_vs(){
		
	}

	public ArrayList<Integer> get_cas_targets_vector() {
		return m_cas_targets_vector;
	}

	public void set_cas_targets_vector(ArrayList<Integer> m_cas_targets_vector) {
		this.m_cas_targets_vector = m_cas_targets_vector;
	}

	public ArrayList<Integer> get_altitude_targets_vector() {
		return m_altitude_targets_vector;
	}

	public void set_altitude_targets_vector(ArrayList<Integer> m_altitude_targets_vector) {
		this.m_altitude_targets_vector = m_altitude_targets_vector;
	}

	public ArrayList<Integer> get_vs_targets_vector() {
		return m_vs_targets_vector;
	}

	public void set_vs_targets_vector(ArrayList<Integer> m_vs_targets_vector) {
		this.m_vs_targets_vector = m_vs_targets_vector;
	}

	public ArrayList<Integer> get_at_engagement_status_vector() {
		return m_at_engagement_status_vector;
	}

	public void set_at_engagement_status_vector(
			ArrayList<Integer> m_at_engagement_status_vector) {
		this.m_at_engagement_status_vector = m_at_engagement_status_vector;
	}

	public ArrayList<Integer> get_ap_engagement_status_vector() {
		return m_ap_engagement_status_vector;
	}

	public void set_ap_engagement_status_vector(
			ArrayList<Integer> m_ap_engagement_status_vector) {
		this.m_ap_engagement_status_vector = m_ap_engagement_status_vector;
	}

	public ArrayList<Integer> get_at_hold_status_vector() {
		return m_at_hold_status_vector;
	}

	public void set_at_hold_status_vector(ArrayList<Integer> m_at_hold_status_vector) {
		this.m_at_hold_status_vector = m_at_hold_status_vector;
	}
	
	public ArrayList<Integer> get_at_retard_status_vector() {
		return m_at_retard_status_vector;
	}

	public void set_at_retard_status_vector(ArrayList<Integer> m_at_retard_status_vector) {
		this.m_at_retard_status_vector = m_at_retard_status_vector;
	}

	public ArrayList<Integer> get_throttle_values_vector() {
		return m_throttle_values_vector;
	}

	public void set_throttle_values_vector(ArrayList<Integer> m_throttle_values_vector) {
		this.m_throttle_values_vector = m_throttle_values_vector;
	}
	
	public void stopTimer(){
		setChanged();
		notifyObservers();
		clearChanged(); 
	}
	
		public void publish_performance_parameters(){
		m_mcpParameters.set_current_CAS(m_current_speed);
		m_mcpParameters.set_current_Altitude(m_current_altitude);
		m_mcpParameters.set_alt_diff(m_altitude_diff);
		m_mcpParameters.set_current_DTD(m_current_DTD);
		m_mcpParameters.set_current_VS(m_current_vs);
		m_mcpParameters.setIsInCaptureRegion(m_is_ac_within_capture_region_b);
		m_mcpParameters.setIsInFAF(m_is_ac_in_FAF_b);
		m_mcpParameters.set_ac_position_wrt_gs_tgt(m_ac_position_wrt_gs_target);
		m_mcpParameters.setPitchValue(m_pitch);
		m_mcpParameters.set_distance_covered_in_time_slice(m_dist_in_current_time_interval);
		m_mcpParameters.notify_all_observers();		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		refresh_parameters();
		compute_performance_parameters();		
		publish_performance_parameters();
		if(m_mcpParameters.get_is_scenario_specific())
		{
			m_mcpParameters.override_with_scenario_specific_targets(m_current_DTD);
		}
		if(ParamRefreshTimer.getIsLimitedTime()){
			counter--;
			if(counter == 0)
			{
				stopTimer();
			}
		}
	}
}
