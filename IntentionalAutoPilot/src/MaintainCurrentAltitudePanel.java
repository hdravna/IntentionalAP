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
public class MaintainCurrentAltitudePanel extends ProcedureSpecificDialogManager implements ActionListener {

	private SpeedPanel m_speed_panel;
	//private AltitudePanel m_alt_panel;
	
	private JButton m_go_button;
	private JPanel m_parent_panel;

	public MaintainCurrentAltitudePanel(JFrame parent, String title){
		super(parent, title, false); 
		setLocation(2, 400);
		m_parent_panel = new JPanel();
		//m_alt_panel = new AltitudePanel();
		//m_alt_panel.set_disable_inc_and_dec_buttons();
		//m_alt_panel.set_keep_updating_current_alt();
		m_speed_panel = SpeedPanel.getInstance();
		m_go_button = new JButton("GO");
		m_go_button.setActionCommand("go");
		m_go_button.addActionListener(this);

		BoxLayout parentBoxLayout = new BoxLayout(m_parent_panel, BoxLayout.PAGE_AXIS);
		JPanel go_button_panel = new JPanel();      
		go_button_panel.add(m_go_button);

		//m_parent_panel.add(m_alt_panel.getAltitudePanel(), parentBoxLayout);
		m_parent_panel.add(m_speed_panel.getSpeedPanel(), parentBoxLayout);

		m_parent_panel.add(Box.createRigidArea(new Dimension(0, 30)));
		m_parent_panel.add(go_button_panel, parentBoxLayout);

		getContentPane().add(m_parent_panel, BorderLayout.CENTER);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack(); 
		setVisible(true);
	}

	public Dimension getPreferredSize() {
		return new Dimension(300,250);
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
		//m_alt_panel.stop_current_altitude_timer();
		super.dispose();
	}
}
