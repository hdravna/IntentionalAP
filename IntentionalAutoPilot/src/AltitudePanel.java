

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
import javax.swing.Timer;

@SuppressWarnings("serial")
public class AltitudePanel extends JPanel implements ActionListener{

	private JButton m_inc_alt_button;
	private JButton m_dec_alt_button;
	private JButton m_set_cur_alt_button;
	private JTextField m_alt_value;
	private int m_selected_altitude;
	private JPanel m_alt_buttons_panel;
	private JTextField alt_txt;
	//private static AltitudePanel m_unique_instance; 
	private boolean m_keep_below_target_alt_b;
	private boolean m_keep_above_target_alt_b;
	//private boolean m_disable_inc_and_dec_b;
	private boolean m_keep_updating_current_alt_in_panel;
	private Timer m_timer;
	public AltitudePanel(){
		
	 m_alt_buttons_panel = new JPanel(); 
     BoxLayout altbuttonsBoxLayout = new BoxLayout(m_alt_buttons_panel, BoxLayout.PAGE_AXIS);
     m_alt_buttons_panel.setLayout(altbuttonsBoxLayout);
     m_selected_altitude = MCPParameters.getInstance().get_target_mcpaltitude();
      alt_txt = new JTextField("Altitude");
     //Font fontSerifP14 = new Font("Serif", Font.PLAIN, 14);
     Font fontArialB14 = new Font("arial", Font.BOLD, 14);
     alt_txt.setFont(fontArialB14);
     alt_txt.setEditable(false);
     alt_txt.setOpaque(false);
 
     m_inc_alt_button= new JButton("+");
     m_inc_alt_button.setActionCommand("inc_alt");
     m_inc_alt_button.addActionListener(this);
     
     m_dec_alt_button = new JButton ("-");
     m_dec_alt_button.setActionCommand("dec_alt");
     m_dec_alt_button.addActionListener(this);
     
     m_set_cur_alt_button= new JButton("Cur");
     m_set_cur_alt_button.setActionCommand("set_cur_alt");
     m_set_cur_alt_button.addActionListener(this);
          
     m_alt_value = new JTextField(Integer.toString(m_selected_altitude));
     m_alt_value.setEditable(false);
     //m_alt_value.setHorizontalAlignment(JTextField.CENTER);
     
     m_alt_buttons_panel.add(alt_txt);
     m_alt_buttons_panel.add(Box.createRigidArea(new Dimension(0, 20)));
     m_alt_buttons_panel.add(m_inc_alt_button);
     m_alt_buttons_panel.add(Box.createRigidArea(new Dimension(0, 10)));
     m_alt_buttons_panel.add(m_alt_value);
     m_alt_buttons_panel.add(Box.createRigidArea(new Dimension(0, 10)));
     m_alt_buttons_panel.add(m_dec_alt_button);
     m_alt_buttons_panel.add(Box.createRigidArea(new Dimension(0, 20)));
     m_alt_buttons_panel.add(m_set_cur_alt_button);
     init_timer();
    //m_container_panel.add(this);
	}
	
	public int get_selected_altitude(){
		return m_selected_altitude;
	}
	
	public void set_selected_altitude(int selected_alt_p){
		m_selected_altitude = selected_alt_p;
		repaint(getGraphics());
	}
	
	public JPanel getAltitudePanel(){
		return m_alt_buttons_panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "inc_alt":
			if(m_keep_below_target_alt_b)
			{
				if (m_selected_altitude < MCPParameters.getInstance().get_target_mcpaltitude())
				{
					m_selected_altitude = m_selected_altitude + 100;
				}
			}
			else
			{
				m_selected_altitude = m_selected_altitude + 100;
			}
			break;
		case "dec_alt":
			
			if(m_keep_above_target_alt_b)
			{
				if(m_selected_altitude > MCPParameters.getInstance().get_target_mcpaltitude())
				{
					m_selected_altitude = m_selected_altitude - 100;
				}
			}
			else
			{
				m_selected_altitude = m_selected_altitude - 100;
			}
			break;
		case "set_cur_alt":
			m_selected_altitude = MCPParameters.getInstance().get_current_altitude();
			break;
		default:
			break;
		}
		repaint(getGraphics());
	}

	public void repaint(Graphics g){
		if(m_keep_updating_current_alt_in_panel)
		{
				m_timer.start();
		}
		else
		{
			m_alt_value.setText(Integer.toString(m_selected_altitude));
			m_alt_value.validate();
		}
		
	}
	
	public void set_disable_inc_and_dec_buttons(){
		//m_disable_inc_and_dec_b = true;
   	    m_inc_alt_button.setEnabled(false);
   	    m_dec_alt_button.setEnabled(false);
   	    m_set_cur_alt_button.setEnabled(false);
   	   //m_selected_altitude = MCPParameters.getInstance().get_target_mcpaltitude();
   	    repaint(getGraphics());
	}
	
	public void set_keep_sel_alt_above_target_alt(){
		m_keep_above_target_alt_b = true;
	}
	
	public void set_keep_sel_alt_below_target_alt(){
		m_keep_below_target_alt_b = true;
	}
	
	public void set_keep_updating_current_alt(){
		m_keep_updating_current_alt_in_panel = true;
		repaint(getGraphics());
	}

	public void stop_current_altitude_timer(){
		if(m_timer != null)
		{
			m_timer.stop();
		}
	}
	
	private void init_timer(){
		m_timer = new Timer(100, new ActionListener(){
			public void actionPerformed(ActionEvent act_evnt){
				{
					m_alt_value.setText(Integer.toString(MCPParameters.getInstance().get_current_altitude()));
					m_alt_value.validate();
				}
			}
		});
	}
}
