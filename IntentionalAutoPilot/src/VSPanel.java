/*
Author: Anvardh Nanduri
Organisation: CATSR, GMU
Supervisor: Lance Sherry
Date: September 2014
Code for Intentional Auto Pilot Prototype. No part of this software code can be reused in any form without prior approval of the author.
email: anvardh@gmail.com
*/
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class VSPanel extends JPanel implements ActionListener{

	private JButton m_inc_vs_button;
	private JButton m_dec_vs_button;
	private JButton m_set_cur_vs_button;
	private JTextField m_vs_value;
	private int m_selected_vs;
	private JPanel m_vs_buttons_panel;
	private JTextField vs_txt;
	private boolean m_climb_only_b;
	private boolean m_descent_only_b;
	//private static AltitudePanel m_unique_instance; 

	public VSPanel(){

		m_vs_buttons_panel = new JPanel(); 
		BoxLayout vsbuttonsBoxLayout = new BoxLayout(m_vs_buttons_panel, BoxLayout.PAGE_AXIS);
		m_vs_buttons_panel.setLayout(vsbuttonsBoxLayout);
		m_selected_vs = MCPParameters.getInstance().get_current_VS();
		vs_txt = new JTextField("VS");
		//Font fontSerifP14 = new Font("Serif", Font.PLAIN, 14);
		Font fontArialB14 = new Font("arial", Font.BOLD, 14);
		vs_txt.setFont(fontArialB14);
		vs_txt.setEditable(false);
		vs_txt.setOpaque(false);

		m_inc_vs_button= new JButton("+");
		m_inc_vs_button.setActionCommand("inc_vs");
		m_inc_vs_button.addActionListener(this);

		m_dec_vs_button = new JButton ("-");
		m_dec_vs_button.setActionCommand("dec_vs");
		m_dec_vs_button.addActionListener(this);

		m_set_cur_vs_button= new JButton("Cur");
		m_set_cur_vs_button.setActionCommand("set_cur_vs");
		m_set_cur_vs_button.addActionListener(this);

		m_vs_value = new JTextField(Integer.toString(m_selected_vs));
		m_vs_value.setEditable(false);
		//m_alt_value.setHorizontalAlignment(JTextField.CENTER);

		m_vs_buttons_panel.add(vs_txt);
		m_vs_buttons_panel.add(Box.createRigidArea(new Dimension(0, 20)));
		m_vs_buttons_panel.add(m_inc_vs_button);
		m_vs_buttons_panel.add(Box.createRigidArea(new Dimension(0, 10)));
		m_vs_buttons_panel.add(m_vs_value);
		m_vs_buttons_panel.add(Box.createRigidArea(new Dimension(0, 10)));
		m_vs_buttons_panel.add(m_dec_vs_button);
		m_vs_buttons_panel.add(Box.createRigidArea(new Dimension(0, 20)));
		m_vs_buttons_panel.add(m_set_cur_vs_button);

		//m_container_panel.add(this);
	}

	public int get_selected_vs(){
		return m_selected_vs;
	}

	public void set_selected_vs(int selected_vs_p){
		m_selected_vs = selected_vs_p;
		repaint(getGraphics());
	}

	public JPanel getVSPanel(){
		return m_vs_buttons_panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "inc_vs":
			if(m_descent_only_b)
			{
				if(m_selected_vs < 0)
				{
					m_selected_vs = m_selected_vs + 100;
				}
			}
			else
			{
				m_selected_vs = m_selected_vs + 100;
			}
			break;
			
		case "dec_vs":
			
			if(m_climb_only_b)
			{
				if(m_selected_vs > 0)
				{
					m_selected_vs = m_selected_vs - 100;
				}
			}
			else
			{
				m_selected_vs = m_selected_vs - 100;
			}
			break;
		case "set_cur_vs":
			int current_vs = MCPParameters.getInstance().get_current_VS();
			if(m_descent_only_b)
			{
				 if(current_vs <= 0)
				 {
					 m_selected_vs = current_vs;
				 }
			}
			else if(m_climb_only_b)
			{
				if(current_vs >= 0)
				{
					m_selected_vs = current_vs;
				}
			}
			else
			{
				m_selected_vs = current_vs;
			}
			break;
		default:
			break;
		}
		repaint(getGraphics());
	}

	public void repaint(Graphics g){
		m_vs_value.setText(Integer.toString(m_selected_vs));
		m_vs_value.validate();
	}

	public void set_disable_inc_and_dec_buttons(){
		repaint(getGraphics());
	}
	
	public void set_vs_climb_only()
	{
		m_climb_only_b = true;
	}
	
	public void set_vs_descent_only()
	{
		m_descent_only_b = true;
	}

}
