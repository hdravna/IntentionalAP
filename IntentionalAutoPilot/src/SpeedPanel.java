

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class SpeedPanel extends JPanel implements ActionListener {
	
	private JButton m_inc_speed_button;
	private JButton m_dec_speed_button;
	private JButton m_set_cur_spd_button;
	private JTextField m_speed_value;
	private JTextField m_speed_txt;
	private JPanel m_speed_buttons_panel;
	private int m_selected_speed; 
	private static SpeedPanel m_unique_instance;
	
	public static SpeedPanel getInstance(){
		if(m_unique_instance == null){
			m_unique_instance = new SpeedPanel();
		}
		return m_unique_instance;
	}
	
	private SpeedPanel(){ 
	m_speed_buttons_panel = new JPanel();
     BoxLayout spdButtonsBoxLayout = new BoxLayout(m_speed_buttons_panel, BoxLayout.PAGE_AXIS);
     
     m_selected_speed = MCPParameters.getInstance().get_target_CAS();
     m_speed_buttons_panel.setLayout(spdButtonsBoxLayout);
     
     m_speed_txt = new JTextField("Speed");
     m_speed_txt.setEditable(false);
     m_speed_txt.setOpaque(true);
     Font fontArialB14 = new Font("arial", Font.BOLD, 14);
     m_speed_txt.setFont(fontArialB14);
     
     m_inc_speed_button = new JButton("+");
     m_inc_speed_button.setActionCommand("inc_spd");
     m_inc_speed_button.addActionListener(this);
     
     m_dec_speed_button = new JButton ("-");
     m_dec_speed_button.setActionCommand("dec_spd");
     m_dec_speed_button.addActionListener(this);
     
     m_set_cur_spd_button = new JButton("Cur");
     m_set_cur_spd_button.setActionCommand("set_cur_spd");
     m_set_cur_spd_button.addActionListener(this);
     
     m_speed_value = new JTextField(Integer.toString(m_selected_speed));
     m_speed_value.setEditable(false);
     //m_speed_value.setHorizontalAlignment(JTextField.CENTER);
     
     m_speed_buttons_panel.add(m_speed_txt);
     m_speed_buttons_panel.add(Box.createRigidArea(new Dimension(0, 20)));
     m_speed_buttons_panel.add(m_inc_speed_button);
     m_speed_buttons_panel.add(Box.createRigidArea(new Dimension(0, 10)));
     m_speed_buttons_panel.add(m_speed_value);
     m_speed_buttons_panel.add(Box.createRigidArea(new Dimension(0, 10)));
     m_speed_buttons_panel.add(m_dec_speed_button);     
     m_speed_buttons_panel.add(Box.createRigidArea(new Dimension(0, 20)));
     m_speed_buttons_panel.add(m_set_cur_spd_button);
	}

	public int get_selected_speed(){
		return m_selected_speed;
	}
	
	public void set_selected_speed(int selected_speed_p){
		m_selected_speed = selected_speed_p;
	}
	
	public JPanel getSpeedPanel(){
		return m_speed_buttons_panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "inc_spd":
			m_selected_speed = m_selected_speed + 10;
			break;
		case "dec_spd":
			m_selected_speed = m_selected_speed - 10;
			break;
		case "set_cur_spd":
			m_selected_speed = MCPParameters.getInstance().get_current_CAS();
			break;
		default:
			break;
		}
		repaint(getGraphics());
	}
	
	public void repaint(Graphics g){
		m_speed_value.setText(Integer.toString(m_selected_speed));
		m_speed_value.validate();
	}

}
