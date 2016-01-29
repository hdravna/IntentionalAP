/*
Author: Anvardh Nanduri
Organisation: CATSR, GMU
Supervisor: Lance Sherry
Date: September 2014
Code for Intentional Auto Pilot Prototype. No part of this software code can be reused in any form without prior approval of the author.
email: anvardh@gmail.com
*/

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MaintainCurrentGSPanel extends ProcedureSpecificDialogManager implements ActionListener {

	private SpeedPanel m_speed_panel;
	//private AltitudePanel m_alt_panel;
	
	private JButton m_go_button;
	private JPanel m_parent_panel;

	public MaintainCurrentGSPanel(JFrame parent, String title){
		super(parent, title, false); 
		setLocation(2, 525);
		m_parent_panel = new JPanel();
		m_speed_panel = SpeedPanel.getInstance();
		m_go_button = new JButton("GO");
		m_go_button.setActionCommand("go");
		m_go_button.addActionListener(this);

		BoxLayout parentBoxLayout = new BoxLayout(m_parent_panel, BoxLayout.PAGE_AXIS);
		JPanel go_button_panel = new JPanel();      
		go_button_panel.add(m_go_button);

		m_parent_panel.add(m_speed_panel.getSpeedPanel(), parentBoxLayout);

		m_parent_panel.add(Box.createRigidArea(new Dimension(0, 30)));
		m_parent_panel.add(go_button_panel, parentBoxLayout);

		getContentPane().add(m_parent_panel, BorderLayout.CENTER);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack(); 
		setVisible(true);
	}

	public Dimension getPreferredSize() {
		return new Dimension(270,230);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getActionCommand().compareTo("go") == 0)
		{
			MCPParameters mcp_parameters = MCPParameters.getInstance();
			super.setSelectedAltitude(mcp_parameters.get_current_altitude());
			super.setSelectedCAS(m_speed_panel.get_selected_speed());
		}
		refreshAircraftParameters(); //this would update the Aircraft Parameters object
		super.dispose();
	}
}
