

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ProcedureList extends Component implements MouseListener, Observer{

	private String ProcedureInputs[] = {"Clb Maint Alt(Preset Alt, Preset Speed/Cur Speed",
			"Clb Maint Alt with ROC",
			"Des Maint Alt (Preset Alt, Preset Speed/Cur Speed)",
			"Des Maint Alt with ROD",
			"Maint Cur Alt- ",
			"Go Around (Preset GA Alt)",
			"Glideslope Capture From Above(Preset ROD, Preset Speed/Cur Speed)",
			"Maintain Current Glideslope (Preset Speed/Cur Speed)",
			"Glideslope Capture From Below(Preset Speed/Cur Speed)",
			"Flare"	};
	private JList<String> m_procedureList;
	private JFrame m_parent_frame;

	//For setting current altitude for "Maint Cur Alt-"
	private MCPParameters m_mcp_parameters;
	
	public ProcedureList(JPanel parentPanel){
		m_procedureList = new JList<String>(ProcedureInputs);
		m_procedureList.setFixedCellHeight(50);
		m_procedureList.addMouseListener(this);
		
		//binding this to observable (MCP Parameters)
		m_mcp_parameters = MCPParameters.getInstance();
		m_mcp_parameters.addObserver(this);
	}

	public JList<String> getProcedureList(){
		return m_procedureList;
	}
	
	protected void setParentFrame(JFrame parent_frame){
		m_parent_frame = parent_frame;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1)
		{
			int index = m_procedureList.locationToIndex(e.getPoint());
			m_procedureList.setSelectedIndex(index);
			m_procedureList.setSelectionBackground(Color.BLUE);
			m_procedureList.setSelectionForeground(Color.WHITE);
		}
		if (e.getClickCount() == 2) {
			int index = m_procedureList.locationToIndex(e.getPoint());
			switch(index){

			case 0: //CLB MAINT ALT
				new ClimbMaintainAltitudePanel(m_parent_frame, "Clb Maintain Altitude");
				break;

			case 1://CLB MAINT ALT WITH ROC
				new ClimbWithRateOfClimbPanel(m_parent_frame, "Clb with ROC");
				break;
			
				
			case 2: // DES MAINT ALT
				new DescentMaintainAltitudePanel(m_parent_frame, "Des Maintain Altitude");
				break;
				
			case 3://DES MAINT ALT WITH ROD
				new DescentWithRateOfDescentPanel(m_parent_frame, "Des with ROD");
				break;
				
			case 4: //MAINT CUR ALT
				new MaintainCurrentAltitudePanel(m_parent_frame,"Maintain Current Altitude");
				break;

			case 5: // GO AROUND
				break;

			case 6: 
			//  GLIDESLOPE CAPTURE FROM ABOVE
				if(m_mcp_parameters.getIsVNAVEngaged())
				{
					new CaptureGSFromAbovePanel(m_parent_frame, "Capture GS from above");
				}
				break;

			case 7: //MAINTAIN GLIDESLOPE
				if(m_mcp_parameters.getIsVNAVEngaged())
				{
					new MaintainCurrentGSPanel(m_parent_frame, "Maintain Current GS");
				}
				break;

			case 8: // GLIDESLOPE CAPTURE FROM BELOW
				break;
				
			case 9: //FLARE
				break;

			default:
				break;

			}
			//m_procedureList.setSelectionBackground(Color.BLUE);
		}
	}
	
	private void setCurrentAltitude(int current_altitude)
	{
		ProcedureInputs[4] = "Maint Cur Alt- " + Integer.toString(current_altitude) + "ft (Preset Speed)";
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void disable_glideslope_procedures()
	{
		//m_procedureList.getComponent(6).setEnabled(false);
		//m_procedureList.getComponent(6).setBackground(Color.GRAY);
		//m_procedureList.getComponent(7).setEnabled(false);
		//m_procedureList.getComponent(7).setFocusable(false);
		//m_procedureList.getComponent(8).setEnabled(false);
		//m_procedureList.getComponent(8).setFocusable(false);
	}
	@Override
	public void update(Observable arg0, Object mcpParams) {
		MCPParameters mcpParamsObj = (MCPParameters)mcpParams;
	    setCurrentAltitude(mcpParamsObj.get_current_altitude());
	}
}

