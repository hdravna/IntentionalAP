

import javax.swing.JDialog;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class ProcedureSpecificDialogManager extends JDialog {

	protected int m_selected_cas;
	protected int m_selected_altitude;
	protected int m_selected_vs;
	protected MCPParameters m_aircraftParameters;
	public ProcedureSpecificDialogManager(JFrame parent, String title, boolean is_modal){
		super(parent, title, is_modal);
		m_aircraftParameters = MCPParameters.getInstance();
		m_selected_altitude = m_aircraftParameters.get_selected_mcpAltitude();
		m_selected_cas= m_aircraftParameters.get_selected_mcpCAS();
		m_selected_vs = m_aircraftParameters.get_selected_mcpVS();
	}

	
	protected void bindToProcedureDialogManager(){
		
	}
	
	public void refreshAircraftParameters(){
		m_aircraftParameters.set_selected_mcpAltitude(m_selected_altitude);
		m_aircraftParameters.set_selected_mcpCAS(m_selected_cas);
		m_aircraftParameters.set_selected_mcpVS(m_selected_vs);
		m_aircraftParameters.set_target_altitude(m_selected_altitude);
		m_aircraftParameters.set_target_CAS(m_selected_cas);
		m_aircraftParameters.set_target_VS(m_selected_vs);	
		m_aircraftParameters.notify_all_observers();
	}
	
	public void setSelectedCAS(int cas_p){
		m_selected_cas = cas_p;
	}
	
	public void setSelectedAltitude(int altitude_p){
		m_selected_altitude = altitude_p;
	}
	
	public void setSelectedVS(int vs_p){
		m_selected_vs = vs_p;
	}
	protected int getSelectedCAS(){
		return m_selected_cas;
	}
	
	protected int getSelectedAltitude(){
		return m_selected_altitude;
	}
	
	protected int getSelectedVS(){
		return m_selected_vs;
	}
}
