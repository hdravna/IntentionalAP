

import java.util.ArrayList;
import java.util.Observable;

//Singleton
public class MCPParameters extends Observable{
	private int m_target_altitude;
	private int m_target_cas;
	private int m_previous_target_cas;
	private int m_target_vs;
	private int m_selected_mcpCAS; //will become target if speed knob is pulled
	private int m_selected_mcpAltitude; //will become target if alt knob is pulled
	private int m_selected_mcpVS;
	private int m_current_altitude;
	private int m_altitude_difference;
	private int m_current_speed;
	private double m_current_DTD;
	//private double m_current_fpa;
	private int m_cruise_alt;
	private int m_current_vs;
	private double m_FAF_length_from_RW;
	private int m_ac_position_wrt_gs_tgt;
	private String m_active_procedure = "";
	private double m_distance_covered_in_time_slice;
	
	//initial values- initialized from initial values from scenarioSpecific data sheet
	int m_initial_altitude;
	int m_initial_speed;
	double m_initial_DTD;
	double m_initial_fpa;
	int m_initial_vs;
	
	double m_pitch;
	double m_fpa; //flight path angle (gamma)
	double m_angle_of_attack; //AoA (alpha)
	
	private boolean m_is_ac_within_capture_region_b;
	private boolean m_is_ac_within_faf_b;
	private boolean m_is_ap_engaged_b;
	private boolean m_is_vnav_engaged_b;
	private boolean m_is_at_engaged_b;
	private boolean m_is_at_in_hold_mode;
	private boolean m_is_at_in_retard_mode;
	private int m_thrust_value;
	
	static MCPParameters m_unique_instance;
	
	private boolean m_is_scenario_specific = false;
	private ArrayList<Integer> m_cas_targets_vector;
	private ArrayList<Integer> m_altitude_targets_vector;
	private ArrayList<Integer> m_vs_targets_vector;
	private ArrayList<Integer> m_at_engagement_status_vector;
	private ArrayList<Integer> m_ap_engagement_status_vector;
	private ArrayList<Integer> m_at_hold_status_vector;
	private ArrayList<Integer> m_throttle_values_vector;
	private ArrayList<Integer> m_at_retard_status_vector;
	private ArrayList<Integer> m_vnav_status_vector;
	
	public void setInitialAlt(int init_alt)
	{
		m_initial_altitude = init_alt;
	}
	
	public void setInitialSpeed(int init_speed)
	{
		m_initial_speed = init_speed;
	}
		
	public void setInitialVS(int init_vs)
	{
		m_initial_vs = init_vs;
	}
	
	public void setInitialDTD(double init_dtd)
	{
		m_initial_DTD = init_dtd;
	}
	
	public int getInitialAlt()
	{
		return m_initial_altitude;
	}
	
	public int getInitialSpeed()
	{
		return m_initial_speed;
	}
		
	public int getInitialVS()
	{
		return m_initial_vs;
	}
	
	public double getInitialDTD()
	{
		return m_initial_DTD;
	}
	
	private MCPParameters() {
		//m_target_cas = 250;
		//Asiana Air initial target CAS 
		m_target_cas = 170; //in kts
		
		m_target_altitude = 1800;
		m_selected_mcpAltitude = m_target_altitude; 
		//m_target_altitude = 10000;
		
		//m_target_vs = 0;
		m_target_vs = -1000;
		//m_selected_mcpAltitude = 10000;
		//m_selected_mcpCAS = 250;
		//m_selected_mcpVS = 0;
		
		m_selected_mcpCAS = m_target_cas;
		m_selected_mcpVS = m_target_vs;
		//m_current_altitude = 10000;
		m_current_altitude = 3460;
		//m_current_speed = 250;
		m_current_speed = 190;
		//m_current_vs = 0;
		m_current_vs = -1000;
		//m_previous_target_cas = 250;
		m_previous_target_cas = 170;
		
		//for glide slope logic
		//8.6 nm for SFO (Asiana Air incident)
		//m_FAF_length_from_RW = 8.6;
		
		m_cruise_alt = 10000;
		
		m_cas_targets_vector = new ArrayList<Integer>();
		m_altitude_targets_vector = new ArrayList<Integer>();
		m_at_engagement_status_vector = new ArrayList<Integer>();
		m_ap_engagement_status_vector = new ArrayList<Integer>();
		m_at_hold_status_vector = new ArrayList<Integer>();
		m_throttle_values_vector = new ArrayList<Integer>();
		m_vs_targets_vector = new ArrayList<Integer>();
		m_vnav_status_vector = new ArrayList<Integer>();
		m_at_retard_status_vector = new ArrayList<Integer>();
	}
	
	public static MCPParameters getInstance(){
		if(m_unique_instance == null){
			m_unique_instance = new MCPParameters();
		}
		return m_unique_instance;
	}
	
	public void override_with_scenario_specific_targets(double current_dtd)
	{
		int index = (int) (current_dtd*10);
		m_target_altitude = m_altitude_targets_vector.get(index);
		m_target_cas = m_cas_targets_vector.get(index);
		m_thrust_value = m_throttle_values_vector.get(index);
		m_current_vs = m_vs_targets_vector.get(index);
		
		if(m_ap_engagement_status_vector.get(index) == 1)
		{
			m_is_ap_engaged_b = true;
		}
		else
		{
			m_is_ap_engaged_b = false;
		}
		
		if(m_at_engagement_status_vector.get(index) == 1)
		{
			m_is_at_engaged_b = true;
		}
		else
		{
			m_is_at_engaged_b = false;
		}
		
		if(m_at_hold_status_vector.get(index) == 1)
		{
			m_is_at_in_hold_mode = true;
		}
		else
		{
			m_is_at_in_hold_mode = false;
		}
		
		if(m_at_retard_status_vector.get(index) == 1)
		{
			m_is_at_in_retard_mode = true;
		}
		else
		{
			m_is_at_in_retard_mode = false;
		}		
		if(m_vnav_status_vector.get(index) == 1)
		{
			m_is_vnav_engaged_b = true;
		}
		else
		{
			m_is_vnav_engaged_b = false;
		}		
}
	
	public void set_ac_position_wrt_gs_tgt(int position)
	{
		m_ac_position_wrt_gs_tgt = position;
	}
	
	public int get_ac_position_wrt_gs_tgt()
	{
		return m_ac_position_wrt_gs_tgt;
	}
	public void notify_all_observers(){
		setChanged();
		notifyObservers(this);
		clearChanged(); 
	}
	
	public String get_active_procedure(){
		return m_active_procedure;
	}
	
	public void set_active_procedure(String act_proc_p){
		m_active_procedure = act_proc_p;
	}
	
	public void set_cruise_altitude(int cruise_alt)
	{
		m_cruise_alt = cruise_alt;
	}
	
	public int get_cruise_alt()
	{
		return m_cruise_alt;
	}
	public void set_target_altitude(int mcpAlt_p){
		m_target_altitude = mcpAlt_p;
	}
	
	public void set_target_CAS(int mcpspeed_p){
		m_previous_target_cas = m_target_cas;
		m_target_cas = mcpspeed_p;
	}
	
	public void set_selected_mcpCAS(int selected_mcp_cas_p){
		m_selected_mcpCAS = selected_mcp_cas_p;
	}
	
	public void set_selected_mcpAltitude(int selected_mcp_alt_p){
		m_selected_mcpAltitude = selected_mcp_alt_p;
	}
	
	public int get_selected_mcpAltitude(){
		return m_selected_mcpAltitude;
	}
	
	public int get_selected_mcpCAS(){
		return m_selected_mcpCAS;
	}
	
	public int get_previous_cas_target()
	{
		int previous_cas_tgt = m_previous_target_cas;
		m_previous_target_cas = m_target_cas;
		return previous_cas_tgt;
	}
	
	public void set_target_VS(int mcpVS_p){
		m_target_vs = mcpVS_p;
	}
	
	public int get_target_mcpaltitude(){
		return m_target_altitude;
	}
	
	public int get_target_CAS(){
		return m_target_cas;
	}
	
	public int get_target_VS(){
		return m_target_vs;
	}
	
	public int get_current_CAS(){
		return m_current_speed;
	}
	
	public int get_alt_diff(){
		return m_altitude_difference;
	}
	
	public void set_alt_diff(int alt_diff_p)
	{
		m_altitude_difference = alt_diff_p;
	}
	public int get_current_altitude(){
		return m_current_altitude;
	}
	
	public int get_current_VS(){
		return m_current_vs;
	}
	
	public double get_current_DTD(){
		return m_current_DTD;
	}
	
	public double getPitch(){
		return m_pitch;
	}
	
	public void set_current_CAS(double CAS_inp_p){
		m_current_speed = (int)CAS_inp_p;
	}
	
	public void set_current_Altitude(double altitude_inp_p){
		m_current_altitude = (int)altitude_inp_p;
	}
	
	public void set_current_VS(double VS_inp_p){
		m_current_vs = (int)VS_inp_p;
	}
	
	public void set_current_DTD(double dtd_inp_p){
		m_current_DTD = dtd_inp_p;
	}
	
	public void setIsInCaptureRegion(boolean p_is_in_capture_region_b){
		m_is_ac_within_capture_region_b = p_is_in_capture_region_b;
	}
	
	public boolean getIsInCaptureRegion(){
	
		return m_is_ac_within_capture_region_b;
	}
	
	
	public void setPitchValue(double pitch_p)
	{
		m_pitch = pitch_p;
	}

	
	public int get_selected_mcpVS() {
		return m_selected_mcpVS;
	}

	public void set_selected_mcpVS(int selected_mcpVS_p) {
		m_selected_mcpVS = selected_mcpVS_p;
	}
	
	public void set_faf_distance_from_rw(double faf_from_rw){
		m_FAF_length_from_RW = faf_from_rw;
	}
	
	public double get_faf_distance_from_rw(){
		return m_FAF_length_from_RW;
	}
	public void setIsAPEngaged(boolean is_ap_engaged)
	{
		m_is_ap_engaged_b = is_ap_engaged;
	}
	public boolean getIsAPEngaged()
	{
		return m_is_ap_engaged_b;
	}
	
	public void setIsVNAVEngaged(boolean is_vnav_engaged)
	{
		m_is_vnav_engaged_b = is_vnav_engaged;
	}
	public boolean getIsVNAVEngaged()
	{
		return m_is_vnav_engaged_b;
	}
	
	public void setIsInFAF(boolean is_ac_in_faf)
	{
		m_is_ac_within_faf_b = is_ac_in_faf;
	}
	public boolean getIsInFAF()
	{
		return m_is_ac_within_faf_b;
	}

	public boolean get_is_at_engaged_b() {
		return m_is_at_engaged_b;
	}

	public void set_is_at_engaged_b(boolean m_is_at_engaged_b) {
		this.m_is_at_engaged_b = m_is_at_engaged_b;
	}

	public boolean get_is_at_in_hold_mode() {
		return m_is_at_in_hold_mode;
	}

	public void set_is_at_in_hold_mode(boolean m_is_at_in_hold_mode) {
		this.m_is_at_in_hold_mode = m_is_at_in_hold_mode;
	}
	
	public void set_is_at_in_retard_mode(boolean m_is_at_in_retard_mode) {
		this.m_is_at_in_retard_mode = m_is_at_in_retard_mode;
	}
	
	public boolean get_is_at_in_retard_mode() {
		return m_is_at_in_retard_mode;
	}

	public int get_thrust_value() {
		return m_thrust_value;
	}

	public void set_thrust_value(int m_thrust_value) {
		this.m_thrust_value = m_thrust_value;
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

	public ArrayList<Integer> get_throttle_values_vector() {
		return m_throttle_values_vector;
	}

	public void set_throttle_values_vector(ArrayList<Integer> m_throttle_values_vector) {
		this.m_throttle_values_vector = m_throttle_values_vector;
	}

	public boolean get_is_scenario_specific() {
		return m_is_scenario_specific;
	}

	public void set_is_scenario_specific(boolean m_is_scenario_specific) {
		this.m_is_scenario_specific = m_is_scenario_specific;
	}
	
	public void set_at_retard_status_vector(ArrayList<Integer> m_at_retard_status_vector) {
		this.m_at_retard_status_vector = m_at_retard_status_vector;
	}
	
	public ArrayList<Integer> get_at_retard_status_vector() {
		return m_at_retard_status_vector;
	}

	public ArrayList<Integer> get_vnav_status_vector() {
		return m_vnav_status_vector;
	}

	public void set_vnav_status_vector(ArrayList<Integer> m_vnav_status_vector) {
		this.m_vnav_status_vector = m_vnav_status_vector;
	}

	public double get_distance_covered_in_time_slice() {
		return m_distance_covered_in_time_slice;
	}

	public void set_distance_covered_in_time_slice(
			double m_distance_covered_in_time_slice) {
		this.m_distance_covered_in_time_slice = m_distance_covered_in_time_slice;
	}
}

