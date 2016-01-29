/*
Author: Anvardh Nanduri
Organisation: CATSR, GMU
Supervisor: Lance Sherry
Date: September 2014
Code for Intentional Auto Pilot Prototype. No part of this software code can be reused in any form without prior approval of the author.
email: anvardh@gmail.com
*/
//import java.applet.Applet;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class IAPFrame {
		static JFrame f;
		static JPanel myPanel; 
		public static void main(String[] args) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI(); 
				}
			});
		}

		public static void createAndShowGUI() {
			//System.out.println("Created GUI on EDT? "+ SwingUtilities.isEventDispatchThread());
			f = new JFrame("Auto Pilot Tutor");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			myPanel = new MyPanel(f);
			f.add(myPanel);
			f.pack();
			f.setVisible(true);
			f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		} 
	}

	@SuppressWarnings("serial")
	class MyPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ActionListener, Observer{
		Observable m_observable;
		
		Aircraft_Performance_Model m_apm_obj;
		
		//private int CAS = 250;
		static Font fontHelB12, fontHelP16, fontHelB18, fontHelP12, fontHelP10, fontHelB8, fontHelP18, fontSerifP14, fontSerifB14, fontSerifB18;
		private int m_VMAX = 340; //knots
		private int m_VMIN = 130; //knots
		//private int altitude = 10000;
		//private int vs = 0;
		private int radioAltitude = -8;
		private MCPParameters m_mcpParameters;
		private ScenarioSpecificInputManager m_scenarioManager;
		//private int mcpAltitude = 10000, mcpCAS = 250, mcpVS = 0;
		
		//private int selected_mcpCAS = 250; //will become target if speed knob is pulled
		//private int selected_mcpAltitude = 10000; //will become target if alt knob is pulled
		private boolean m_is_at_engaged_b = false;
		private boolean m_is_ap_engaged_b = false;
		//private boolean m_is_at_previously_engaged_b = false;
		//private boolean m_is_ac_within_capture_region_b;
		//private double dtd = 10.0;
		private boolean m_is_radio_alt_valid_b = false;
		private Graphics m_graphics_obj;
		private ParamRefreshTimer m_main_timer;
		private boolean m_is_main_timer_running = false;
		private Timer m_timer;
		private Timer m_graph_x_axis_timer;
		private Timer m_vertical_profile_y_axis_timer;
		private int m_vertical_prof_x_axis_vector[][];
		private int m_vertical_prof_y_axis_vector[][];
		private int m_vertical_prof_y_axis_line_vector[];
		private int m_vertical_prof_x_axis_line_vector[];
		private int m_rate_of_alt_tape_roll;
		private int m_throttles_value;
		private boolean m_is_vnav_engaged_b = false;
		private boolean m_is_toga_active_b = false;
		private int m_pitch;
		//TODO- Remove this after test
		private int m_vertical_speed;
		private String inten;
		private String sitConfig;
		private String sitEvent;
		private String vsString;
		private ProcedureList m_procedure_list;
		private String m_fmaspdtgt = "250"; 
		private String m_fmaalttgt = "15000";
		private String m_fmahdgtgt = "180";
		private String m_fmahdgmode = "HEADING   "; //10 characters left justified
		private String m_fmaspdmode = " "; //10 chars right justified
		private String m_fmaaltmode = " "; //11 chars left justified
		private String m_active_procedure = " ";
		private String m_active_procedure_str = " "; 
		private String m_situation = " ";
		private String m_previous_fmaspdmode = " ";
		private String m_previous_fmsaltmode = " ";
		private boolean m_alt_tape_processing_initiated_b = false;
		private int m_throttle_x_cord = 400+137;
		private int m_throttle_y_cord = 600+97;
		private int m_dummy_int = m_throttle_y_cord;
		private boolean blink_magnifier_b = false;
		private int m_graph_x = 10;
		private int m_graph_y = 12;
		
		private Image m_graph_image;
		//private Image m_graph_frame_image;
		//private Image m_graph_label_image;
		private Image m_thrust_engine_image;
		private Image m_white_thrust_engine_image;
		private Image m_idle_engine_image;
		private Image m_hold_engine_image;
		private Image m_retard_for_flare_engine_image;
		private Image m_alt_tape_engine_image;
		private Image m_elevator_spd_tape_image;
		private Image m_elevator_alt_tape_image;
		private Image m_white_elevator_alt_tape_image;
		private Image m_active_speed_bug_image;
		private Image m_active_speed_bug_image_for_modified_pfd; //green colored
		private Image m_attitude_indicator_horizon_image;
		private Image m_attitude_indicator_bg_image;
		private Image m_attitude_indicator_markers_image;
		private Image m_active_crossed_speed_bug_image;
		private Image m_active_crossed_speed_bug_image_for_modified_pfd; //green colored
		private Image m_inactive_crossed_speed_bug_image;
		private Image m_inactive_white_speed_bug_image;
		private Image m_speed_target_cross_image;
		private Image m_yellow_barber_pole_image;
		private Image m_vp_axes_image;
		private Image m_descend_ac_icon;
		private Image m_cruise_ac_icon;
		private Image m_yoke_frame_image;
		private Image m_yoke_bar_image;
		private Image m_gray_frame_image;
		private Image m_speed_magnifier_image;
		private Image m_barber_bg_image;
		private Image m_active_speed_magnifier_image;
		private Image m_alt_magnifier_image;
		private Image m_active_alt_magnifier_image;
		private Image m_double_cross_image;
		private int m_yellow_pole_position = 500;
		private int m_speed_bug_position;
		private int m_source_pole_y_position = 1;
		private Image m_red_barber_pole_image;
		private ArrayList<Integer> m_glideslope_alt_targets;
		private Path2D.Double path;
		
		private int m_red_pole_position;
		private boolean m_barber_pole_visible = false;
		private boolean m_is_thrust_held_b = false;
		private boolean m_is_thrust_retard_b = false;
		private boolean m_is_speed_bug_in_tape_range_b = false;
		private boolean m_is_first_time_b = true;
		private boolean m_has_cas_target_changed_b = false;
		private boolean m_has_situation_changed = false;
		private int m_previous_sitNo = 2;
		private int m_sitNo = -1;
		private ArrayList<SpeedTapeAttributes> m_speed_tape_vector;
		private int m_speed_tape_line_vector[];
		//FilteredImageSource m_filtered_engine_image;
		private int vp_x = 10;
		private int vp_y = 300;
		private int vp_panel_x = 10;
		private int vp_panel_y = 10;
		private int spdtapeBegin = 190;
		private int spdtapeEnd = 510;
		private int yoke_frame_x = 1000;
		private int yoke_frame_y = 610;
		private int yoke_bar_x = 1025;
		private int yoke_bar_y = 630;
		private boolean m_yoke_released_b = true;
		int isatx = 10;
		int isaty = 20;
		private String sitInputs[] = {"Altitude",
				"Flight Directors",
				"Radio Altitude",
				"MCP Knob/Wheel Action",
				"Prev Speed|Alt FMA",
				"MCP ALT ",
				"Vertical Speed",
				"Airspeed",
				"MCP VS",
				"Autothrotle",
				"PROF"			
	};

		
		private String sitInputStates [][] = {
				{
					"is above 1500' baro", 
					"is between 400' RA and 1500' baro", 
					"is below 400' RA", 
					"is On Ground"
				}, //"Altitude" 0, 

				{
					"None", 
					"Single", 
					"Dual"
				},//Flight Directors 1

				{
					"is not Valid", 
					"is Valid"
				},//Radio Altitude 2
				
				{
					"VS wheel Rotated", 
					"Alt knob pulled", 
					"Alt knob pushed", 
					"None"
				},//MCP Knob/Wheel Action  3
				
				{	"PITCH | T/O Clamp", 
					"PITCH | Go Around", 
					"PITCH | CLB THRUST",
					"PITCH | IDLE",
					"THRUST | VS",
					"THRUST | ALT HOLD",
					"THRUST | ALT CAP",
					"THRUST | G/S",
					"RETARD | FLARE",
					"WINDSHEAR | WINDSHEAR"}, //Prev Speed|Alt FMA 4
				{						
						"is in CLIMB region",
					    "is in CLIMB capture region",
				   		"is within HOLD region",
				   		"is AT target",
						"is in DESCENT capture region",
						"is in DESCENT region" 
				}, //MCP ALT 5

				{
					"is greater than + 600 fpm",
					"is between +600 and +300 fpm",
					"is between +300 to 0 fpm",
					"is zero",
					"is between 0 to -300 fpm",
					"is between -300 and -600 fpm",
					"is less than -600 fpm"
				}, //Vertical Speed 6
				
				{
					"is above vmax+10",
					"is above vmax+10 in 10 secs",
					"is vmax+5 to vmax+10",
					"is vmax+5 to vmax+10 in 10 secs",
					"is vmax + 5 to vmin - 5",
					"is vmin - 5 to vmin -10 in 10 secs",
					"is vmin -5 to vmin -10",
					"is below vmin -10 in 10 secs",
					"is less than vmin -10"
				}, //Airspeed 7
				
				{
					"is more than 0 fpm",
					"is zero",
					"is less than 0 fpm"
				}, //MCP VS 8

				{
					"not available",
					"Available/not engaged",
					"engaged"
				}, //Autothrotle 9

				{
					"Transitions to Engaged",
					"Engaged",
					"Transitions to disengaged",
					"Disengaged"
				}, //PROF/VNAV 10
				
				{
					"is Above Glideslope",
					"is On Glideslope",
					"is Below Glideslope"
				} //GLIDESLOPE 11
		};
		
		private int sitInputStatesNormal[][] = {{1, 1, 1, 1},
				{1, 1, 1},
				{1, 1},
				{0, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
				{1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1},
				{0, 0, 0, 0, 1, 0, 0, 0, 0},
				{0, 0, 0},
				{1, 1, 1},
				{1, 1, 0, 1},
				{0, 0, 0}
				};

		private int activeSitInput[][] = {{1, 1, 1, 0},
				{1, 1, 1},
				{1, 1},
				{0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 1, 0, 0}, //AT target 
				{0, 0, 0, 1, 0, 0, 0},//zero VS
				{0, 0, 0, 0, 1, 0, 0, 0, 0},
				{0, 1, 0},
				{0, 0, 1},
				{1, 1, 1, 1},
				{0, 0, 0} //Glide slope
				};
		
		private int m_sitDef[][][] = 
					{
						{{1,1,1,0}, {1,1,1}, {1,1}, {0,0,0,1}, {1,1,1,1,1,1,1,1,1,1}, {0,0,1,0,0,0}, {0,0,1,1,1,0,0}, {0,0,0,0,1,0,0,0,0}, {1,1,1}, {0,0,1}, {1,1,1,1}, {0,0,0}}, //Procedure- HOLD ALT XXX
						{{1,1,1,0}, {1,1,1}, {1,1}, {0,0,0,1}, {1,1,1,1,1,1,1,1,1,1}, {0,1,0,0,1,0}, {0,1,1,1,1,1,0}, {0,0,0,0,1,0,0,0,0}, {1,1,1}, {0,0,1}, {1,1,1,1}, {0,0,0}}, //Procedure- CAPTURE ALT XXX
						{{1,1,1,0}, {1,1,1}, {1,1}, {0,0,0,1}, {1,1,1,1,1,1,1,1,1,1}, {1,0,0,0,0,0}, {1,1,1,1,1,1,1}, {0,0,0,0,1,0,0,0,0}, {0,1,0}, {0,0,1}, {1,1,1,1}, {0,0,0}},//Procedure- CLB MAINT XXX
						{{1,1,1,0}, {1,1,1}, {1,1}, {0,0,0,1}, {1,1,1,1,1,1,1,1,1,1}, {1,0,0,0,0,0}, {1,1,1,0,0,0,0}, {0,0,0,0,1,0,0,0,0}, {1,0,0}, {0,0,1}, {1,1,1,1}, {0,0,0}},  //Procedure- CLB MAINT XXX-ROC
						{{1,1,1,0}, {1,1,1}, {1,1}, {0,0,0,1}, {1,1,1,1,1,1,1,1,1,1}, {0,0,0,0,0,1}, {1,1,1,1,1,1,1}, {0,0,0,0,1,0,0,0,0}, {0,1,0}, {0,0,1}, {1,1,1,1}, {0,0,0}},  //Procedure- DES MAINT XXX
						{{1,1,1,0}, {1,1,1}, {1,1}, {0,0,0,1}, {1,1,1,1,1,1,1,1,1,1}, {0,0,0,0,0,1}, {0,0,0,0,1,1,1}, {0,0,0,0,1,0,0,0,0}, {0,0,1}, {0,0,1}, {1,1,1,1}, {0,0,0}},  //Procedure- DES MAINT XXX-ROD
						{{1,1,1,0}, {1,1,1}, {1,1}, {0,0,0,1}, {1,1,1,1,1,1,1,1,1,1}, {0,0,0,1,0,0}, {0,0,1,1,1,0,0}, {0,0,0,0,1,0,0,0,0}, {1,1,1}, {0,0,1}, {1,1,1,1}, {0,0,0}},  //Procedure- MAINT XXX
						{{1,1,1,0}, {1,1,1}, {1,1}, {0,0,0,1}, {1,1,1,1,1,1,1,1,1,1}, {1,1,1,1,1,1}, {0,0,0,1,1,1,1}, {0,0,0,0,1,0,0,0,0}, {1,1,1}, {0,0,1}, {1,1,0,0}, {1,0,0}},  //Procedure- CAPTURE GLIDESLOPE FROM ABOVE
						{{1,1,1,0}, {1,1,1}, {1,1}, {0,0,0,1}, {1,1,1,1,1,1,1,1,1,1}, {1,1,1,1,1,1}, {0,0,0,1,1,1,1}, {0,0,0,0,1,0,0,0,0}, {1,1,1}, {0,0,1}, {1,1,0,0}, {0,1,0}}, //Procedure- MAINT GLIDESLOPE 
						{{1,1,1,0}, {1,1,1}, {1,1}, {0,0,0,1}, {1,1,1,1,1,1,1,1,1,1}, {1,1,1,1,1,1}, {0,0,0,1,1,1,1}, {0,0,0,0,1,0,0,0,0}, {1,1,1}, {0,0,1}, {1,1,0,0}, {0,0,1}} //Procedure- CAPTURE GLIDESLOPE FROM BELOW
					};
		
		private JFrame m_parent_frame;
		private JScrollPane proceduresPane;
		private JRadioButton m_modified_pfd;
		private JRadioButton m_unmodified_pfd;
		private boolean m_is_modified_pfd = true;

		public MyPanel(JFrame parent_frame) {
						
			m_mcpParameters = MCPParameters.getInstance();
			m_parent_frame = parent_frame;
			setBorder(BorderFactory.createLineBorder(Color.black));
			this.setLayout(new GridBagLayout());
			
			m_procedure_list = new ProcedureList(this);
			m_procedure_list.setParentFrame(m_parent_frame);
			//m_procedure_list.getProcedureList().setPreferredSize(new Dimension(50,50));
			proceduresPane = new JScrollPane(m_procedure_list.getProcedureList(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			proceduresPane.setPreferredSize(new Dimension(395, 500));
			//proceduresPane.setLocation(new Point(150,50));
			proceduresPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			proceduresPane.getViewport().setView(m_procedure_list.getProcedureList());

						
			GridBagConstraints pp_constraints = new GridBagConstraints();
			//pp_constraints.fill = GridBagConstraints.HORIZONTAL;
			pp_constraints.gridx = 0;
			pp_constraints.gridy = 0;
			pp_constraints.gridheight = 1;
			//pp_constraints.gridwidth = 1;
			pp_constraints.insets = new Insets(50,350,0,0);
			//pp_constraints.anchor = GridBagConstraints.NORTHEAST;
			pp_constraints.weighty = 0;
			pp_constraints.weightx = 0;
		    //pp_constraints.anchor= GridBagConstraints.NORTHWEST;
			this.add(proceduresPane, pp_constraints);
		
			m_procedure_list.setFocusable(true);
			m_procedure_list.addMouseListener(this);
			
			JButton fly_button = new JButton("FLY");
			fly_button.setEnabled(false);
			JButton restart_button = new JButton("RESET");
			//JButton increment_button = new JButton("INCREMENT");
			JButton scenario1_button = new JButton("Asiana 314");
			JButton scenario2_button = new JButton("Turkish 1951");			
			
			m_modified_pfd = new JRadioButton("Modified PFD", true);
			m_unmodified_pfd = new JRadioButton("Original PFD", true);
			ButtonGroup radioGroup = new ButtonGroup();
			radioGroup.add(m_modified_pfd);
			radioGroup.add(m_unmodified_pfd);
						
			GridBagConstraints fly_button_constraints = new GridBagConstraints();
			GridBagConstraints inc_button_constraints = new GridBagConstraints();
			fly_button_constraints.insets = new Insets(0, 0, 0, 0);
			fly_button_constraints.weighty = 1.0;
			fly_button_constraints.weightx = 1.0;
		    fly_button_constraints.gridheight = 2;
		    fly_button_constraints.anchor = GridBagConstraints.NORTHEAST;
		    inc_button_constraints.insets = new Insets(0, 0, 0, 0);
		    inc_button_constraints.gridheight = 2;
			inc_button_constraints.anchor = GridBagConstraints.NORTHEAST;
			
			GridBagConstraints restart_button_constraints = new GridBagConstraints();
			restart_button_constraints.insets = new Insets(0, 0, 0, 0);
			restart_button_constraints.weighty = 1.0;
			restart_button_constraints.weightx = 1.0;
			restart_button_constraints.gridheight = 2;
			restart_button_constraints.anchor = GridBagConstraints.NORTH;
		    					
			this.add(fly_button, fly_button_constraints);
			//this.add(restart_button,restart_button_constraints);
			this.add(restart_button,inc_button_constraints);
			//this.add(increment_button, inc_button_constraints);
			this.add(scenario1_button, inc_button_constraints);
			this.add(scenario2_button, inc_button_constraints);			
			this.add(m_modified_pfd, inc_button_constraints);
			this.add(m_unmodified_pfd, inc_button_constraints);			
			
			fly_button.setActionCommand("pressed_fly");
			fly_button.addActionListener(this);

			restart_button.setActionCommand("pressed_restart");
			restart_button.addActionListener(this);
			
			//increment_button.setActionCommand("pressed_increment");
			//increment_button.addActionListener(this);

			scenario1_button.setActionCommand("scenario_1");
			scenario1_button.addActionListener(this);
			
			scenario2_button.setActionCommand("scenario_2");
			scenario2_button.addActionListener(this);
			
			m_modified_pfd.setActionCommand("modified_pfd");
			m_modified_pfd.addActionListener(this);
			
			m_unmodified_pfd.setActionCommand("unmodified_pfd");
			m_unmodified_pfd.addActionListener(this);
			
			addMouseListener(this);
			addMouseMotionListener(this);
			addMouseWheelListener(this);

			try {
				//m_thrust_engine_image = new ImageIcon(MyPanel.class.getResource("resources/engine_thrust.png"));
				
				m_thrust_engine_image = (new ImageIcon(MyPanel.class.getResource("/resources/engine_thrust.png"))).getImage();
				m_attitude_indicator_horizon_image = (new ImageIcon(MyPanel.class.getResource("/resources/attitude_indicator_horizon.png"))).getImage();
				
				//m_white_thrust_engine_image = (new ImageIcon(MyPanel.class.getResource("/resources/white_engine_thrust.png"))).getImage();
				//m_idle_engine_image = (new ImageIcon(MyPanel.class.getResource("/resources/engine_idle.png"))).getImage();
				//m_hold_engine_image = (new ImageIcon(MyPanel.class.getResource("/resources/engine_hold.png"))).getImage();
				//m_retard_for_flare_engine_image = (new ImageIcon(MyPanel.class.getResource("/resources/engine_flare.png"))).getImage();
				//m_alt_tape_engine_image = (new ImageIcon(MyPanel.class.getResource("/resources/altitude_tape_engine.png"))).getImage();

				//m_elevator_spd_tape_image = (new ImageIcon(MyPanel.class.getResource("/resources/elevator_spd_tape.png"))).getImage();
				//m_elevator_alt_tape_image = (new ImageIcon(MyPanel.class.getResource("/resources/elevator_alt_tape.png"))).getImage();
				//m_white_elevator_alt_tape_image = (new ImageIcon(MyPanel.class.getResource("/resources/white_elevator_alt_tape.png"))).getImage();

				m_active_speed_bug_image = (new ImageIcon(MyPanel.class.getResource("/resources/active_speed_bug.png"))).getImage();
				m_active_speed_bug_image_for_modified_pfd = (new ImageIcon(MyPanel.class.getResource("/resources/active_speed_bug_for_modified_pfd.png"))).getImage();
				m_active_crossed_speed_bug_image = (new ImageIcon(MyPanel.class.getResource("/resources/active_crossed_speed_bug.png"))).getImage();
				m_active_crossed_speed_bug_image_for_modified_pfd = (new ImageIcon(MyPanel.class.getResource("/resources/active_crossed_speed_bug_for_modified_pfd.png"))).getImage();
				m_inactive_crossed_speed_bug_image = (new ImageIcon(MyPanel.class.getResource("/resources/inactive_crossed_speed_bug.png"))).getImage();
				m_inactive_white_speed_bug_image = (new ImageIcon(MyPanel.class.getResource("/resources/inactive_white_speed_bug.png"))).getImage();
				m_speed_target_cross_image = (new ImageIcon(MyPanel.class.getResource("/resources/speed_target_cross.png"))).getImage();

				//m_yellow_barber_pole_image = (new ImageIcon(MyPanel.class.getResource("/resources/yellow_red_barber_pole.png"))).getImage();
				//m_red_barber_pole_image = (new ImageIcon(MyPanel.class.getResource("/resources/red_barber_pole.png"))).getImage();
				
				
				m_attitude_indicator_bg_image = (new ImageIcon(MyPanel.class.getResource("/resources/attitude_indicator_bg.png"))).getImage();
				m_attitude_indicator_markers_image = (new ImageIcon(MyPanel.class.getResource("/resources/attitude_indicator_markers.png"))).getImage();
				
				m_vp_axes_image = (new ImageIcon(MyPanel.class.getResource("/resources/vp_axes.png"))).getImage();
				m_descend_ac_icon = (new ImageIcon(MyPanel.class.getResource("/resources/descending_airplane_icon.png"))).getImage();
				m_cruise_ac_icon = (new ImageIcon(MyPanel.class.getResource("/resources/cruising_airplane_icon.png"))).getImage();
				
				m_yoke_frame_image = (new ImageIcon(MyPanel.class.getResource("/resources/yoke_frame.png"))).getImage();
				m_yoke_bar_image = (new ImageIcon(MyPanel.class.getResource("/resources/yoke_bar.png"))).getImage();
				m_gray_frame_image = (new ImageIcon(MyPanel.class.getResource("/resources/gray_frame.png"))).getImage();
				
				m_speed_magnifier_image = (new ImageIcon(MyPanel.class.getResource("/resources/speed_magnifier.png"))).getImage();
				m_active_speed_magnifier_image = (new ImageIcon(MyPanel.class.getResource("/resources/active_speed_magnifier.png"))).getImage();
				
				m_alt_magnifier_image = (new ImageIcon(MyPanel.class.getResource("/resources/alt_magnifier.png"))).getImage();
				m_active_alt_magnifier_image = (new ImageIcon(MyPanel.class.getResource("/resources/active_alt_magnifier.png"))).getImage();
				//m_graph_frame_image = (new ImageIcon(MyPanel.class.getResource("/resources/Graph_frame.png"))).getImage();
				//m_graph_label_image = (new ImageIcon(MyPanel.class.getResource("/resources/graph_labels.png"))).getImage();
				
				m_double_cross_image = (new ImageIcon(MyPanel.class.getResource("/resources/double_cross.png"))).getImage();
				m_barber_bg_image = (new ImageIcon(MyPanel.class.getResource("/resources/barber_bg.png"))).getImage();

			} catch (Exception e) {
				e.printStackTrace();
			}

			m_speed_tape_vector = new ArrayList<SpeedTapeAttributes>();
			init_speed_tape_attributes();
			
			//init_speeds_positions();
			
			m_speed_tape_line_vector = new int[11];
			init_speed_tape_line_vector();
			setupSpeedTapeTimer();
			setupGraphTimer();
			setupVerticalProfileYTimer();
			m_vertical_prof_y_axis_vector = new int[4][2]; //[y_position][value]
			m_vertical_prof_y_axis_line_vector = new int[5];
			init_vertical_prof_y_axis_vector_positons();
			init_vertical_prof_y_axis_vector_values();
			init_vert_prof_y_axis_line_vector();			
			path = new Path2D.Double(Path2D.Double.WIND_EVEN_ODD);
		}
		
		public void setGraphToUse(Image graphImage)
		{
			m_graph_image = graphImage;
		}
		private void init_vertical_prof_y_axis_vector_positons()
		{
			m_vertical_prof_y_axis_vector[0][0] = vp_y;
			m_vertical_prof_y_axis_vector[1][0] = vp_y + 40;
			m_vertical_prof_y_axis_vector[2][0] = vp_y + 80;
			m_vertical_prof_y_axis_vector[3][0] = vp_y + 120;
		}
		
		private void init_vertical_prof_y_axis_vector_values()
		{
			m_vertical_prof_y_axis_vector[0][1] = 5000;
			m_vertical_prof_y_axis_vector[1][1] = 4000;
			m_vertical_prof_y_axis_vector[2][1] = 3000;
			m_vertical_prof_y_axis_vector[3][1] = 2000;
		}
		
			
		private void init_vert_prof_y_axis_line_vector()
		{
			m_vertical_prof_y_axis_line_vector[0] = vp_y;
			m_vertical_prof_y_axis_line_vector[1] = vp_y + 40;
			m_vertical_prof_y_axis_line_vector[2] = vp_y + 80;
			m_vertical_prof_y_axis_line_vector[3] = vp_y + 120;
			m_vertical_prof_y_axis_line_vector[4] = vp_y + 160;
		}
		
		private void exchange_vertical_profile_y_values(){
			int current_alt = m_mcpParameters.get_current_altitude();

			int alt_at_index_0 = m_vertical_prof_y_axis_vector[0][1];
			int alt_at_index_1 = m_vertical_prof_y_axis_vector[1][1];
			int alt_at_index_2 = m_vertical_prof_y_axis_vector[2][1];
			int alt_at_index_3 = m_vertical_prof_y_axis_vector[3][1];

			m_vertical_prof_y_axis_vector[0][1] = alt_at_index_1;
			m_vertical_prof_y_axis_vector[1][1] = alt_at_index_2;
			m_vertical_prof_y_axis_vector[2][1] = alt_at_index_3;
			m_vertical_prof_y_axis_vector[3][1] = alt_at_index_3 - 100;
		}
		
		private void init_speed_tape_attributes()
		{
			int spdtapey = 300;
			int spdinc = 32;
			int pfdy = 50;

			SpeedTapeAttributes value_set_0 = new SpeedTapeAttributes(); 
			value_set_0.set_y_position(pfdy + spdtapey - 5*spdinc);
			value_set_0.set_speed_value(m_mcpParameters.get_current_CAS() + 60);
			m_speed_tape_vector.add(value_set_0);
			
			SpeedTapeAttributes value_set_1 = new SpeedTapeAttributes();
			value_set_1.set_y_position(pfdy + spdtapey - 4*spdinc);
			value_set_1.set_speed_value(m_mcpParameters.get_current_CAS() + 40);
			m_speed_tape_vector.add(value_set_1);
			
			SpeedTapeAttributes value_set_2 = new SpeedTapeAttributes();
			value_set_2.set_y_position(pfdy + spdtapey - 2*spdinc);
			value_set_2.set_speed_value(m_mcpParameters.get_current_CAS()+20);
			m_speed_tape_vector.add(value_set_2);
			
			SpeedTapeAttributes value_set_3 = new SpeedTapeAttributes();
			value_set_3.set_y_position(pfdy + spdtapey);
			value_set_3.set_speed_value(m_mcpParameters.get_current_CAS());
			m_speed_tape_vector.add(value_set_3);
			
			SpeedTapeAttributes value_set_4 = new SpeedTapeAttributes();
			value_set_4.set_y_position(pfdy + spdtapey + 2*spdinc);
			value_set_4.set_speed_value(m_mcpParameters.get_current_CAS()-20);
			m_speed_tape_vector.add(value_set_4);
			
			SpeedTapeAttributes value_set_5 = new SpeedTapeAttributes();
			value_set_5.set_y_position(pfdy + spdtapey + 4*spdinc);
			value_set_5.set_speed_value(m_mcpParameters.get_current_CAS()-40);
			m_speed_tape_vector.add(value_set_5);
			
			SpeedTapeAttributes value_set_6 = new SpeedTapeAttributes();
			value_set_6.set_y_position(pfdy + spdtapey + 5*spdinc);
			value_set_6.set_speed_value(m_mcpParameters.get_current_CAS()-60);
			m_speed_tape_vector.add(value_set_6);
		}

		
		private void init_speeds_positions()
		{
			int spdtapey = 300;
			int spdinc = 32;
			int pfdy = 50;
			m_speed_tape_vector.get(0).set_y_position(pfdy + spdtapey - 5*spdinc);
			m_speed_tape_vector.get(1).set_y_position(pfdy + spdtapey - 4*spdinc);
			m_speed_tape_vector.get(2).set_y_position(pfdy + spdtapey - 2*spdinc);
			m_speed_tape_vector.get(3).set_y_position(pfdy + spdtapey);
			m_speed_tape_vector.get(4).set_y_position(pfdy + spdtapey + 2*spdinc);
			m_speed_tape_vector.get(5).set_y_position(pfdy + spdtapey + 4*spdinc);
			m_speed_tape_vector.get(6).set_y_position(pfdy + spdtapey + 5*spdinc);
		}
		
		private void exchange_speed_values(){

			int current_cas = m_mcpParameters.get_current_CAS();
			int target_cas = m_mcpParameters.get_target_CAS();
			boolean is_AT_in_hold_mode = m_mcpParameters.get_is_at_in_hold_mode(); //decreasing speed if AT in hold mode
			boolean is_AT_in_retard_mode = m_mcpParameters.get_is_at_in_retard_mode(); //decreasing speed if AT in retard mode
			//if current_cas <= 100 recovery not possible
			boolean is_ac_stalled = false;
			
			if(current_cas <= 110)
			{
				is_ac_stalled = true;
			}
			
			//if(!is_ac_stalled && (target_cas >= current_cas && !(is_AT_in_hold_mode || is_AT_in_retard_mode)))
			//if(target_cas > current_cas)

			
			//if ac_is_stalled or at in retrd or hold or (target_cas < current_cas)
			if(is_ac_stalled || (target_cas < current_cas) || is_AT_in_hold_mode || is_AT_in_retard_mode)
			{
				int speed_at_index_1 = m_speed_tape_vector.get(1).get_speed_value();
				int speed_at_index_2 = m_speed_tape_vector.get(2).get_speed_value();
				int speed_at_index_3 = m_speed_tape_vector.get(3).get_speed_value();
				int speed_at_index_4 = m_speed_tape_vector.get(4).get_speed_value();
				int speed_at_index_5 = m_speed_tape_vector.get(5).get_speed_value();
				int speed_at_index_6 = m_speed_tape_vector.get(6).get_speed_value();

				m_speed_tape_vector.get(0).set_speed_value(speed_at_index_1);
				m_speed_tape_vector.get(1).set_speed_value(speed_at_index_2);
				m_speed_tape_vector.get(2).set_speed_value(speed_at_index_3);
				m_speed_tape_vector.get(3).set_speed_value(speed_at_index_4);
				m_speed_tape_vector.get(4).set_speed_value(speed_at_index_5);
				m_speed_tape_vector.get(5).set_speed_value(speed_at_index_6);
				m_speed_tape_vector.get(6).set_speed_value(speed_at_index_6 - 20);
			}
			
			else
			{
				int speed_at_index_0 = m_speed_tape_vector.get(0).get_speed_value();
				int speed_at_index_1 = m_speed_tape_vector.get(1).get_speed_value();
				int speed_at_index_2 = m_speed_tape_vector.get(2).get_speed_value();
				int speed_at_index_3 = m_speed_tape_vector.get(3).get_speed_value();
				int speed_at_index_4 = m_speed_tape_vector.get(4).get_speed_value();
				int speed_at_index_5 = m_speed_tape_vector.get(5).get_speed_value();
				
				m_speed_tape_vector.get(0).set_speed_value(speed_at_index_0 + 20);
				m_speed_tape_vector.get(1).set_speed_value(speed_at_index_0);
				m_speed_tape_vector.get(2).set_speed_value(speed_at_index_1);
				m_speed_tape_vector.get(3).set_speed_value(speed_at_index_2);
				m_speed_tape_vector.get(4).set_speed_value(speed_at_index_3);
				m_speed_tape_vector.get(5).set_speed_value(speed_at_index_4);
				m_speed_tape_vector.get(6).set_speed_value(speed_at_index_5);				
			}
		}
		
		private void init_speed_tape_line_vector()
		{
			int spdtapey = 300;
			int spdinc = 32;
			int pfdy = 50;

			m_speed_tape_line_vector[0] = pfdy+spdtapey-(5*spdinc);
			m_speed_tape_line_vector[1] = pfdy+spdtapey-(4*spdinc);
			m_speed_tape_line_vector[2] = pfdy+spdtapey-(3*spdinc);
			m_speed_tape_line_vector[3] = pfdy+spdtapey-(2*spdinc);
			m_speed_tape_line_vector[4] = pfdy+spdtapey-(1*spdinc);
			m_speed_tape_line_vector[5] = pfdy+spdtapey;
			m_speed_tape_line_vector[6] = pfdy+spdtapey +(1*spdinc);
			m_speed_tape_line_vector[7] = pfdy+spdtapey +(2*spdinc);
			m_speed_tape_line_vector[8] = pfdy+spdtapey +(3*spdinc);
			m_speed_tape_line_vector[9] = pfdy+spdtapey +(4*spdinc);
			m_speed_tape_line_vector[10] = pfdy+spdtapey +(5*spdinc);
		}
		@Override
		//upon clicking Fly button
		public void actionPerformed(ActionEvent e) {

			boolean is_time_limited_b = false;
			
			switch(e.getActionCommand()){
			case "scenario_1":
				m_scenarioManager = new ScenarioSpecificInputManager(1, this);
				break;
				
			case "scenario_2":
				m_scenarioManager = new ScenarioSpecificInputManager(2, this);
				break;
			}
			
			boolean is_restart_b = false;
			switch(e.getActionCommand()){
			case "pressed_restart":
				m_scenarioManager = null;
				m_main_timer.stopTimer();
				//TODO- Have to redesign efficiently.
				Aircraft_Performance_Model.m_unique_apm_obj = null;
				MCPParameters.m_unique_instance = null;
				ParamRefreshTimer.m_unique_prt_obj = null;
				Initial_Performance_Parameters.m_unique_ipp_obj = null;
				
				IAPFrame.f.dispose();
				IAPFrame.f = new JFrame("Auto Pilot Tutor");
				IAPFrame.f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				IAPFrame.myPanel = new MyPanel(IAPFrame.f);
				IAPFrame.f.add(IAPFrame.myPanel);
				IAPFrame.f.pack();
				IAPFrame.f.setVisible(true);
				IAPFrame.f.setExtendedState(IAPFrame.f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
				is_restart_b = true;				
				break;
			}
						
			if(!is_restart_b)
			{
				initialize_critical_components();
			}
			
			switch(e.getActionCommand()){
			case "pressed_fly":
				is_time_limited_b = false;
				m_main_timer.setIsLimitedTime(is_time_limited_b);
				m_mcpParameters.set_is_scenario_specific(false);
				break;				

			case "pressed_increment":
				is_time_limited_b = true;
				m_main_timer.setIsLimitedTime(is_time_limited_b);
				m_mcpParameters.set_is_scenario_specific(false);
				break;
				
			case "scenario_1":
				is_time_limited_b = false;
				m_main_timer.setIsLimitedTime(is_time_limited_b);
				//m_scenarioManager = new ScenarioSpecificInputManager(1, this);
				m_mcpParameters.set_is_scenario_specific(true);
				break;
				
			case "scenario_2":
				is_time_limited_b = false;
				m_main_timer.setIsLimitedTime(is_time_limited_b);
				//m_scenarioManager = new ScenarioSpecificInputManager(2, this);
				m_mcpParameters.set_is_scenario_specific(true);
				break;
				
			case "modified_pfd":
				m_is_modified_pfd = true;
				break;
				
			case "unmodified_pfd":
				m_is_modified_pfd = false;
				break;				
			}
			
			if(!is_restart_b)
			{
				m_main_timer.startTimer();
				m_is_main_timer_running = m_main_timer.is_main_timer_running();
				init_speed_tape_attributes();
			}
		}
		
		private void initialize_critical_components()
		{
			m_mcpParameters = MCPParameters.getInstance();
			Initial_Performance_Parameters ipp = Initial_Performance_Parameters.getInstance();
			m_apm_obj = Aircraft_Performance_Model.getInstance(ipp, this);			
			m_glideslope_alt_targets = m_apm_obj.get_glideslope_alt_target();
			m_main_timer = ParamRefreshTimer.getInstance(m_apm_obj);
			this.m_observable = m_mcpParameters;
			m_observable.addObserver(this);
		}
		public Dimension getPreferredSize() {
			return new Dimension(1000,600);
		}
		public JFrame getParentFrame(){
			return m_parent_frame;
		}
		protected void paintComponent(Graphics g) {
			super.repaint();
			super.paintComponent(g);
			
			//animateGraph();
			drawGraph(g);
			//g.drawImage(m_graph_frame_image,0,0,null);
			
			int current_vs = m_mcpParameters.get_current_VS();
			int target_altitude = m_mcpParameters.get_target_mcpaltitude();
			int target_cas = m_mcpParameters.get_target_CAS();
			//int target_cas = 170;
			int previous_target_cas = m_mcpParameters.get_previous_cas_target();
			int current_CAS = m_mcpParameters.get_current_CAS();
			int current_altitude = m_mcpParameters.get_current_altitude();
			
			if(m_mcpParameters.get_is_scenario_specific())
			{
				m_is_ap_engaged_b = m_mcpParameters.getIsAPEngaged();
				m_is_at_engaged_b = m_mcpParameters.get_is_at_engaged_b();
				m_is_thrust_held_b = m_mcpParameters.get_is_at_in_hold_mode();
				m_is_thrust_retard_b = m_mcpParameters.get_is_at_in_retard_mode();
				m_is_vnav_engaged_b = m_mcpParameters.getIsVNAVEngaged();
				m_throttles_value = m_mcpParameters.get_thrust_value();
				m_vertical_speed = m_mcpParameters.get_current_VS();
			}
			
			fontHelB12 = new Font("Helvetica", Font.BOLD, 12);
			fontHelP16 = new Font("Helvetica", Font.PLAIN, 16);
			fontHelB18 = new Font("Helvetica", Font.BOLD, 18);
			fontHelP12 = new Font("Helvetica", Font.PLAIN, 12);
			fontHelP10 = new Font("Helvetica", Font.PLAIN, 10);
			fontHelB8 = new Font("Helvetica", Font.BOLD, 8);
			fontHelP18 = new Font("Helvetica", Font.PLAIN, 18);
			fontHelB18 = new Font("Helvetica", Font.BOLD, 18);
			fontSerifP14 = new Font("Serif", Font.PLAIN, 14);
			fontSerifB14 = new Font("Serif", Font.BOLD, 14);
			fontSerifB18 = new Font("Serif", Font.BOLD, 18);

			int mcpx = 400; //407;
			int mcpy = 620;
			
			g.drawRect(mcpx, mcpy, 300, 100);
			g.setColor(Color.DARK_GRAY);
			g.fillRect(mcpx, mcpy, 300, 100);
			
			//A/T Button
			g.setFont(fontHelB12);
			g.setColor(Color.white);
			g.drawRect(mcpx+ 17, mcpy+10, 34, 20);
			g.setColor(Color.black);
			g.fillRect(mcpx+17, mcpy+10, 34, 20);
			if(m_is_at_engaged_b)
			{
				g.setColor(Color.green);
				if(m_apm_obj != null)
				{
					m_apm_obj.set_is_at_engaged(true);
				}
			}			
			else
			{
				g.setColor(Color.white);
				if(m_apm_obj != null)
				{
					m_apm_obj.set_is_ap_engaged(false);
				}
			}
			
			g.drawString("A/T", mcpx+25, mcpy+25);

			//AutoPilot A/P Button
			g.setFont(fontHelB12);
			g.setColor(Color.white);
			g.drawRect(mcpx+ 17, mcpy+50, 34, 20);
			g.setColor(Color.black);
			g.fillRect(mcpx+17, mcpy+50, 34, 20);
			if(m_is_ap_engaged_b)
			{
				g.setColor(Color.green);
				if(m_apm_obj != null)
				{
					m_apm_obj.set_is_ap_engaged(true);
				}
			}
			else
			{
				g.setColor(Color.white);
				if(m_apm_obj != null)
				{
					m_apm_obj.set_is_ap_engaged(false);
				}
			}
			g.drawString("A/P", mcpx+25, mcpy+65);
			
			//VNAV Button
			g.setFont(fontHelB12);
			g.setColor(Color.white);
			g.drawRect(mcpx+ 67, mcpy+10, 44, 20);
			g.setColor(Color.black);
			g.fillRect(mcpx+67, mcpy+10, 44, 20);
			if(m_is_vnav_engaged_b)
			{
				g.setColor(Color.green);
				if(m_apm_obj != null)
				{
					m_apm_obj.set_is_vnav_engaged(true);
				}
			}
			else
			{
				g.setColor(Color.white);
				if(m_apm_obj != null)
				{
					m_apm_obj.set_is_vnav_engaged(false);
					m_procedure_list.disable_glideslope_procedures();
				}
			}
			g.drawString("ILS", mcpx+75, mcpy+25);
			
			//TOGA Button
			g.setFont(fontHelB12);
			g.setColor(Color.white);
			g.drawRect(mcpx+ 67, mcpy+50, 44, 20);
			g.setColor(Color.black);
			g.fillRect(mcpx+67, mcpy+50, 44, 20);
			if(m_is_toga_active_b)
			{
				g.setColor(Color.green);
			}
			else
			{
				g.setColor(Color.white);
			}
			g.drawString("TOGA", mcpx+75, mcpy+65);
			
			//THRUST HOLD Button
			g.setFont(fontHelB12);
			g.setColor(Color.white);
			g.drawRect(mcpx+ 237, mcpy+10, 44, 20);
			g.setColor(Color.black);
			g.fillRect(mcpx+ 237, mcpy+10, 44, 20);
			if(m_is_thrust_held_b)
			{
				g.setColor(Color.green);
			}
			else
			{
				g.setColor(Color.white);
			}
			g.drawString("HOLD", mcpx+245, mcpy+25);
			
			//THRUST RETARD Button
			g.setFont(fontHelB12);
			g.setColor(Color.white);
			g.drawRect(mcpx+ 237, mcpy+50, 50, 20);
			g.setColor(Color.black);
			g.fillRect(mcpx+ 237, mcpy+50, 50, 20);
			if(m_is_thrust_retard_b)
			{
				g.setColor(Color.green);
			}
			else
			{
				g.setColor(Color.white);
			}
			g.drawString("RETARD", mcpx+242, mcpy+65);
			
			//Throttles
			g.setFont(fontHelB12);
			g.setColor(Color.BLACK);
			g.drawRect(mcpx+ 150, mcpy+10, 10, 75);
			g.fillRect(mcpx+ 150, mcpy+10, 10, 75);
			
			g.drawRect(mcpx+ 170, mcpy+10, 10, 75);
			g.fillRect(mcpx+ 170, mcpy+10, 10, 75);
	
			//PFD
			int pfdx = 750;
			int pfdy = 50;
			int fmaspdtgtx = 30, fmaspdmodex = 120, fmahdgmodex = 200+40, fmahdgtgtx = 340, fmaaltmodex = 390, fmaalttgtx = 490;
			int fmay = 100;
			
			//pfd background
			g.setColor(Color.black);
			g.fillRect(pfdx,  pfdy,  600, 550);
			//g.drawImage(m_gray_frame_image,750, 600, null);
			//speed FMA
			//g.setColor(Color.magenta);
			//g.setColor(Color.WHITE);
			//g.setFont(fontHelB18);
			//g.drawString(Integer.toString(target_cas),  pfdx+30,  pfdy+118);

			//alt FMA
			//g.setColor(Color.magenta);
			
			int vstapex = 570, vstapey = 300, vstapeheight = 320;

			//VS Indicator
			g.setColor(Color.white);
			g.setFont(fontHelB8);
			g.drawLine(pfdx+vstapex-10,  pfdy+vstapey,  pfdx+vstapex+10,  pfdy+vstapey);

			//drawing Attitude indicator background
			int hsix = 306;
			int hsiy = 310;
			
			redrawAttitudeIndicator(g);
			g.drawImage(m_gray_frame_image,750, 600, null);
			//horiz line at Origin
			g.drawString("0", pfdx+vstapex+15, pfdy+vstapey+3);

			for(int i = 0; i<11; i++)
			{
				g.setColor(Color.white);
				g.drawLine(pfdx+vstapex-3, pfdy+vstapey- (i*((vstapeheight/4)/10)),  
						pfdx+vstapex+3, pfdy+vstapey-(i*((vstapeheight/4)/10)));
				g.drawLine(pfdx+vstapex-3, pfdy+vstapey+ (i*((vstapeheight/4)/10)),  
						pfdx+vstapex+3, pfdy+vstapey+(i*((vstapeheight/4)/10)));
			}
			g.setColor(Color.white);
			g.drawLine(pfdx+vstapex-7, pfdy+vstapey-((vstapeheight/4)/2),
					pfdx+vstapex+7, pfdy+vstapey-((vstapeheight/4)/2));
			g.setColor(Color.white);
			g.drawLine(pfdx+vstapex-7, pfdy+vstapey+((vstapeheight/4)/2), 
					pfdx+vstapex+7, pfdy+vstapey+((vstapeheight/4)/2));

			//1000 fpm
			g.setColor(Color.white);
			g.setFont(fontHelB8);
			g.drawLine(pfdx+vstapex-10, pfdy+vstapey-(vstapeheight/4), 
					pfdx+vstapex+10, pfdy+vstapey-(vstapeheight/4));
			g.drawString("1", pfdx+vstapex+15, pfdy+vstapey+3-(vstapeheight/4));
			g.setColor(Color.white);
			g.setFont(fontHelB8);
			g.drawLine(pfdx+vstapex-10, pfdy+vstapey+(vstapeheight/4), 
					pfdx+vstapex+10, pfdy+vstapey+(vstapeheight/4));
			g.drawString("1", pfdx+vstapex+15, pfdy+vstapey+3+(vstapeheight/4));

			//1500 fpm
			g.setColor(Color.white);
			g.drawLine(pfdx+vstapex-3, pfdy+vstapey-((5*vstapeheight)/16), 
					pfdx+vstapex+3, pfdy+vstapey-((5*vstapeheight)/16));
			g.setColor(Color.white);
			g.drawLine(pfdx+vstapex-3, pfdy+vstapey+((5*vstapeheight)/16), 
					pfdx+vstapex+3, pfdy+vstapey+((5*vstapeheight)/16));

			//2000 fpm
			g.setColor(Color.white);
			g.setFont(fontHelB8);
			g.drawLine(pfdx+vstapex-10, pfdy+vstapey-((3*vstapeheight)/8), 
					pfdx+vstapex+10, pfdy+vstapey-((3*vstapeheight)/8));
			g.drawString("2", pfdx+585, pfdy+vstapey+3-((3*vstapeheight)/8));
			g.setColor(Color.white);
			g.setFont(fontHelB8);
			g.drawLine(pfdx+vstapex-10, pfdy+vstapey+((3*vstapeheight)/8), 
					pfdx+vstapex+10, pfdy+vstapey+((3*vstapeheight)/8));
			g.drawString("2", pfdx+vstapex+15, pfdy+vstapey+3+((3*vstapeheight)/8));

			//3000 fpm
			g.setColor(Color.white);
			g.setFont(fontHelB8);
			g.drawLine(pfdx+vstapex-10, pfdy+vstapey-((7*vstapeheight)/16), 
					pfdx+vstapex+10, pfdy+vstapey-((7*vstapeheight)/16));
			g.drawString("3", pfdx+vstapex+15, pfdy+vstapey+3-((7*vstapeheight)/16));
			g.setColor(Color.white);
			g.setFont(fontHelB8);
			g.drawLine(pfdx+vstapex-10, pfdy+vstapey+((7*vstapeheight)/16), 
					pfdx+vstapex+10, pfdy+vstapey+((7*vstapeheight)/16));
			g.drawString("3", pfdx+vstapex+15, pfdy+vstapey+3+((7*vstapeheight)/16));

			//4000 fpm
			g.setColor(Color.white);
			g.setFont(fontHelB8);
			g.drawLine(pfdx+vstapex-10, pfdy+vstapey-(vstapeheight/2), 
					pfdx+vstapex+10, pfdy+vstapey-(vstapeheight/2));
			g.drawString("4", pfdx+vstapex+15, pfdy+vstapey+3-(vstapeheight/2));
			g.setColor(Color.white);
			g.setFont(fontHelB8);
			g.drawLine(pfdx+vstapex-10, pfdy+vstapey+(vstapeheight/2), 
					pfdx+vstapex+10, pfdy+vstapey+(vstapeheight/2));
			g.drawString("4", pfdx+vstapex+15, pfdy+vstapey+3+(vstapeheight/2));

			//increment/decrement VS
			g.setColor(Color.yellow);
			g.drawRect(pfdx+vstapex+10, pfdy+vstapey-25, 10, 10);
			g.drawRect(pfdx+vstapex+10,pfdy+vstapey+15, 10, 10);
			g.setColor(Color.yellow);
			g.setFont(fontHelB12);
			g.drawString("+", pfdx+vstapex+10+3, pfdy+vstapey-16);
			g.drawString("-", pfdx+vstapex+10+3, pfdy+vstapey+23);

			//VS pointer
			g.setColor(Color.green);
			//vs pointer for vs>0

			if((current_vs>=0)&& (current_vs <= 1000))
			{
				g.drawRect(pfdx+vstapex-10+5, pfdy+vstapey-(((vstapeheight/4)/10)*(current_vs/100)), 10, (((vstapeheight/4)/10)*(current_vs/100)));
			}
			else if ((current_vs>1000 ) && (current_vs <= 2000))
			{
				int height = (((( vstapeheight/8)/10)*((current_vs-1000)/100)) + (vstapeheight/4));
				g.drawRect(pfdx+vstapex-10+5,  pfdy+vstapey-height, 10, height);
			}
			
			else if (current_vs > 2000)
			{
				if(current_vs>4000)
				{
					current_vs = 4000;
				}
				int height = ((((vstapeheight/16)/10)*((current_vs-2000)/100))+(vstapeheight/8)+(vstapeheight/4));
				g.drawRect(pfdx+vstapex-10+5,  pfdy+vstapey-height, 10, height);
			}
			else if ((current_vs < 0) && (current_vs >= -1000))
			{
				g.drawRect(pfdx+vstapex-10+5, pfdy+vstapey, 10, (((vstapeheight/4)/10)*(-current_vs/100)));
			}

			else if((current_vs < - 1000) && (current_vs >= -2000))
			{
				int height = ((((vstapeheight/8)/10)*(-(current_vs+1000)/100)) + (vstapeheight/4));
				g.drawRect(pfdx+vstapex-10+5, pfdy+vstapey, 10, height);

			}
			
			else if (current_vs < -2000)
			{
				if(current_vs < -4000) 
				{
					current_vs = -4000;
				}
				int height = ((((vstapeheight/16)/10)*(-(current_vs+2000)/100))+(vstapeheight/8)+(vstapeheight/4));
				g.drawRect(pfdx+vstapex-10+5, pfdy+vstapey, 10, height);
			}

			//alt tape
			int alttapex = 510;
			int alttapey = 300;
			int alttapeheight = 320;
			int altinc = (alttapeheight/10);

			//capture region
			if(activeSitInput[5][0] == 1)
			{
				//mcpAltitude = 5000;
				g.setColor(Color.white);
				//g.fillRect(pfdx+alttapex+30, pfdy+alttapey-15, 8, 30);
			}

			else if (activeSitInput[5][1] == 1)
			{
				//mcpAltitude = 10000;
				g.setColor(Color.white);
				g.fillRect(pfdx+alttapex+30, pfdy+alttapey-(5*altinc)-30,8,30);
				g.setColor(Color.white);
				g.setFont(fontHelP12);
				//g.drawString(Integer.toString(target_altitude), pfdx+alttapex, pfdy+alttapey-(5*altinc)-30+10);
			}
			//alt tape frame
			g.setColor(Color.darkGray);
			g.fillRect(pfdx+alttapex-(60/2), pfdy+alttapey-(320/2), 60, 320);

			g.setFont(fontHelP12);
			//values for 500 ft  increments
			g.setColor(Color.white);
			//g.drawString(Integer.toString((current_altitude/100)*100 + 500), pfdx+alttapex-6, pfdy+alttapey-(5*altinc)+5);
			//g.drawString(Integer.toString(current_altitude- 500), pfdx+alttapex-6, pfdy+alttapey+(5*altinc)+5);

			//tabs at each 100 ft
			for(int i=0; i<6; i++)
			{
				g.setColor(Color.white);
				g.drawLine(pfdx+alttapex-30, pfdy+alttapey-(i*altinc), pfdx+alttapex-25,pfdy+alttapey-(i*altinc));
				g.drawLine(pfdx+alttapex-30, pfdy+alttapey+(i*altinc), pfdx+alttapex-25,pfdy+alttapey+(i*altinc));
			}
			

/*			if(!m_alt_tape_processing_initiated_b)
			{
				initiate_process_altitude_tape();
				m_alt_tape_processing_initiated_b = true;
			}*/
            boolean draw_b = true;
            int altitude_to_nearest_hundred = ((current_altitude + 99)/100 )* 100;
            int j =1;
			for(int i = 1; i < (alttapeheight/2) + 1; i++)
			{
				
				if(i % altinc == 0)
				{
					g.drawLine(pfdx+alttapex-30, pfdy+alttapey-(i), pfdx+alttapex-25,pfdy+alttapey-(i));
					g.drawLine(pfdx+alttapex-30, pfdy+alttapey+(i), pfdx+alttapex-25,pfdy+alttapey+(i));
					
					if(draw_b)
					{
						g.drawString(Integer.toString(altitude_to_nearest_hundred - j*100), pfdx+alttapex-10, pfdy+alttapey+i);
						g.drawString(Integer.toString(altitude_to_nearest_hundred + j*100), pfdx+alttapex-10, pfdy+alttapey-i+5);
						
					}
					if(draw_b)
					{
						draw_b = false;
					}
					else
					{
						draw_b = true;
					}
					
					j++;
				}
			}
            
            //g.drawString(Integer.toString(altitude_to_nearest_hundred - 100), pfdx+alttapex-10, pfdy+alttapey+m_dummy_int+5);
			//m_dummy_int++;
			//increment/decrement altitude by 100
			//g.setColor(Color.yellow);
			//g.drawRect(pfdx+alttapex, pfdy+alttapey-25, 10, 10);
			//g.drawRect(pfdx+alttapex, pfdy+alttapey+15, 10, 10);
			//g.setColor(Color.yellow);
			//g.setFont(fontHelP12);
			//g.drawString("+", pfdx+alttapex+3, pfdy+alttapey-16);
			//g.drawString("-", pfdx+alttapex+3, pfdy+alttapey+23);

			/*//increment/decrement alt by 5000
			g.setColor(Color.yellow);
			g.drawRect(pfdx+alttapex-2, pfdy+alttapey-47, 14, 20);
			g.drawRect(pfdx+alttapex-2, pfdy+alttapey+27, 14, 20);
			g.setColor(Color.yellow);
			g.setFont(fontHelP16);
			g.drawString("+", pfdx+alttapex+1, pfdy+alttapey-32);
			g.drawString("-", pfdx+alttapex+2, pfdy+alttapey+40);*/
			

			//FD
			if(activeSitInput[1][1] == 1 || activeSitInput[1][2] == 1)
			{
				//Vertical FD
				g.setColor(Color.magenta);
				g.fillRect(pfdx+hsix-5,  pfdy+hsiy-50, 3, 100);

				//horizontal FD
				g.setColor(Color.magenta);
				g.fillRect(pfdx+hsix-55,  pfdy+hsiy-10, 100, 3);
			}

			//RA
			g.setColor(Color.white);
			g.drawRect(pfdx+hsix-15, pfdy+hsiy+130, 30, 20);

			g.setColor(Color.blue);
			g.fillRect(pfdx+hsix-15+1, pfdy+hsiy+130, 30-2, 20-2);
			//cross the radio altitude
			/*if(activeSitInput[2][0] == 1)
			{
				g.setColor(Color.yellow);
				g.drawLine(pfdx+hsix-15, pfdy+hsiy+90+40, pfdx+hsix-15+30, pfdy+hsiy+90+60);
				g.drawLine(pfdx+hsix-15, pfdy+hsiy+90+60, pfdx+hsix-15+30, pfdy+hsiy+90+40);
			}*/
			if(activeSitInput[2][1] == 1)
			{
				g.setColor(Color.white);
				g.setFont(fontHelP12);
				g.drawString(Integer.toString(radioAltitude), pfdx+hsix-10, pfdy+hsiy+145);
			}

			//speed tape
			int spdtapex = 60;
			int spdtapey = 300;
			int spdinc = 32;
			
			
			g.setColor(Color.darkGray);
			g.fillRect(pfdx+spdtapex-(60/2), pfdy+spdtapey-(320/2), 60, 320);
						
			//speed tape outline and tabs			
			//20 knots increments
			g.setColor(Color.white);
			g.setFont(fontHelP12);	
			
			
			if(m_yellow_pole_position >= (pfdy + spdtapey + 4*spdinc))
			{
				m_barber_pole_visible = false;
			}
			if(m_speed_tape_vector.get(4).get_speed_value() <= 160)
			{
				m_barber_pole_visible = true;
			}
			
			if(m_speed_tape_vector.get(0).get_y_position() > pfdy+spdtapey-(5*spdinc))
			{
				m_speed_tape_vector.get(0).set_speed_value(m_speed_tape_vector.get(1).get_speed_value() + 20);
				g.drawString(Integer.toString(m_speed_tape_vector.get(0).get_speed_value()), pfdx+spdtapex-6, 
						m_speed_tape_vector.get(0).get_y_position()+5);
			}
			else if(m_speed_tape_vector.get(6).get_y_position() < pfdy+spdtapey+(5*spdinc))
			{
				m_speed_tape_vector.get(6).set_speed_value(m_speed_tape_vector.get(5).get_speed_value() - 20); 
				g.drawString(Integer.toString(m_speed_tape_vector.get(6).get_speed_value()), pfdx+spdtapex-6, 
						m_speed_tape_vector.get(6).get_y_position()+5);
			}
			
			if((m_speed_tape_vector.get(0).get_y_position() >= pfdy + spdtapey - (4*spdinc)) || 
					(m_speed_tape_vector.get(6).get_y_position() <= pfdy+spdtapey+(4*spdinc)))
			{
				exchange_speed_values();
				init_speeds_positions();
			}
						
			if(m_speed_tape_vector.get(1).get_y_position() >= pfdy+spdtapey-(5*spdinc))
			{
				g.drawString(Integer.toString(m_speed_tape_vector.get(1).get_speed_value()), pfdx+spdtapex-6, m_speed_tape_vector.get(1).get_y_position()+5);
			}
			
			g.drawString(Integer.toString(m_speed_tape_vector.get(2).get_speed_value()), pfdx+spdtapex-6, m_speed_tape_vector.get(2).get_y_position()+5);
			g.drawString(Integer.toString(m_speed_tape_vector.get(3).get_speed_value()), pfdx+spdtapex-6, m_speed_tape_vector.get(3).get_y_position()+5);
			g.drawString(Integer.toString(m_speed_tape_vector.get(4).get_speed_value()), pfdx+spdtapex-6, m_speed_tape_vector.get(4).get_y_position()+5);
			
			if(m_speed_tape_vector.get(5).get_y_position() <= pfdy+spdtapey + (5*spdinc))
			{
				g.drawString(Integer.toString(m_speed_tape_vector.get(5).get_speed_value()), pfdx+spdtapex-6, m_speed_tape_vector.get(5).get_y_position()+5);
			}

			g.setColor(Color.white);

			if(m_speed_tape_line_vector[0] >= pfdy+spdtapey - (5*spdinc))
			{
				g.drawLine(pfdx+spdtapex+25,  m_speed_tape_line_vector[0], pfdx+spdtapex+30, m_speed_tape_line_vector[0]);
			}
			
			g.drawLine(pfdx+spdtapex+25,  m_speed_tape_line_vector[1], pfdx+spdtapex+30, m_speed_tape_line_vector[1]);
			g.drawLine(pfdx+spdtapex+25,  m_speed_tape_line_vector[2], pfdx+spdtapex+30, m_speed_tape_line_vector[2]);
			g.drawLine(pfdx+spdtapex+25,  m_speed_tape_line_vector[3], pfdx+spdtapex+30, m_speed_tape_line_vector[3]);
			g.drawLine(pfdx+spdtapex+25,  m_speed_tape_line_vector[4], pfdx+spdtapex+30, m_speed_tape_line_vector[4]);

			g.drawLine(pfdx+spdtapex+25,  m_speed_tape_line_vector[5], pfdx+spdtapex+30, m_speed_tape_line_vector[5]);

			g.drawLine(pfdx+spdtapex+25,  m_speed_tape_line_vector[6], pfdx+spdtapex+30, m_speed_tape_line_vector[6]);
			g.drawLine(pfdx+spdtapex+25,  m_speed_tape_line_vector[7], pfdx+spdtapex+30, m_speed_tape_line_vector[7]);
			g.drawLine(pfdx+spdtapex+25,  m_speed_tape_line_vector[8], pfdx+spdtapex+30, m_speed_tape_line_vector[8]);
			g.drawLine(pfdx+spdtapex+25,  m_speed_tape_line_vector[9], pfdx+spdtapex+30, m_speed_tape_line_vector[9]);
			
			if(m_speed_tape_line_vector[10] <= pfdy+spdtapey+ (5*spdinc))
			{
				g.drawLine(pfdx+spdtapex+25,  m_speed_tape_line_vector[10], pfdx+spdtapex+30, m_speed_tape_line_vector[10]);
			}
			
			if((m_speed_tape_line_vector[9] >= pfdy+spdtapey+(5*spdinc)) || (m_speed_tape_line_vector[1] <= pfdy+spdtapey-(5*spdinc)))
			{
				init_speed_tape_line_vector();  
			}
			
			/*g.setColor(Color.white);
			g.setFont(fontHelP12);
			g.drawString(Integer.toString(current_CAS+20), pfdx+spdtapex-6, pfdy+spdtapey-(2*spdinc)+5);
			g.drawString(Integer.toString(current_CAS-20), pfdx+spdtapex-6, pfdy+spdtapey+(2*spdinc)+5);
			g.drawString(Integer.toString(current_CAS+40), pfdx+spdtapex-6, pfdy+spdtapey-(4*spdinc)+5);
			g.drawString(Integer.toString(current_CAS-40), pfdx+spdtapex-6, pfdy+spdtapey+(4*spdinc)+5);
			
			g.setColor(Color.white);

			g.drawLine(pfdx+spdtapex+25,  pfdy+spdtapey-(5*spdinc), pfdx+spdtapex+30, pfdy+spdtapey-(5*spdinc));
			g.drawLine(pfdx+spdtapex+25,  pfdy+spdtapey-(4*spdinc), pfdx+spdtapex+30, pfdy+spdtapey-(4*spdinc));
			g.drawLine(pfdx+spdtapex+25,  pfdy+spdtapey-(3*spdinc), pfdx+spdtapex+30, pfdy+spdtapey-(3*spdinc));
			g.drawLine(pfdx+spdtapex+25,  pfdy+spdtapey-(2*spdinc), pfdx+spdtapex+30, pfdy+spdtapey-(2*spdinc));
			g.drawLine(pfdx+spdtapex+25,  pfdy+spdtapey-(1*spdinc), pfdx+spdtapex+30, pfdy+spdtapey-(1*spdinc));

			g.drawLine(pfdx+spdtapex+25,  pfdy+spdtapey, pfdx+spdtapex+30, pfdy+spdtapey);

			g.drawLine(pfdx+spdtapex+25,  pfdy+spdtapey+(1*spdinc), pfdx+spdtapex+30, pfdy+spdtapey+(1*spdinc));
			g.drawLine(pfdx+spdtapex+25,  pfdy+spdtapey+(2*spdinc), pfdx+spdtapex+30, pfdy+spdtapey+(2*spdinc));
			g.drawLine(pfdx+spdtapex+25,  pfdy+spdtapey+(3*spdinc), pfdx+spdtapex+30, pfdy+spdtapey+(3*spdinc));
			g.drawLine(pfdx+spdtapex+25,  pfdy+spdtapey+(4*spdinc), pfdx+spdtapex+30, pfdy+spdtapey+(4*spdinc));
			g.drawLine(pfdx+spdtapex+25,  pfdy+spdtapey+(5*spdinc), pfdx+spdtapex+30, pfdy+spdtapey+(5*spdinc));
*/
			
			if(m_barber_pole_visible)
			{
				if(m_yellow_pole_position >= pfdy + spdtapey - (5*spdinc))
				{
				g.setColor(Color.BLACK);
				g.drawRect(pfdx+spdtapex+30, m_yellow_pole_position, 6, Math.abs(m_yellow_pole_position - (pfdy + spdtapey + 5*spdinc)));
				g.setColor(Color.RED);
				g.fillRect(pfdx+spdtapex+30, m_yellow_pole_position, 6, Math.abs(m_yellow_pole_position - (pfdy + spdtapey + 5*spdinc)));
								
				}
				else if(m_yellow_pole_position < pfdy + spdtapey - (5*spdinc))
				{
					g.setColor(Color.BLACK);
					g.drawRect(pfdx+spdtapex+30, pfdy+spdtapey-(5*spdinc), 6, 310);
					g.setColor(Color.RED);
					g.fillRect(pfdx+spdtapex+30, pfdy+spdtapey-(5*spdinc), 6, 310);
				}
				
				//750, 600
				//this bg image will make solid barber pole into dashed barber pole
				g.drawImage(m_barber_bg_image, pfdx+spdtapex+30, pfdy+spdtapey-170, null);
				g.drawImage(m_barber_bg_image, pfdx+spdtapex+33, pfdy+spdtapey-170, null);
			}
			
			
			interpret_all_input_values();
			int active_situation = analyze_situation(m_sitDef);
			determine_action();
			
			g.setColor(Color.white);
			g.drawRect(pfdx+75, pfdy+10, 400, 80);
			g.drawLine(pfdx+75, pfdy+50, pfdx+475, pfdy+50);
			g.setColor(Color.white);
			//m_active_procedure_str = m_mcpParameters.get_active_procedure();
			g.setFont(fontSerifB18);
			g.drawString(m_active_procedure_str, pfdx+175, pfdy+40);
			
			g.setFont(fontSerifB14);
			g.drawString(m_situation, pfdx+ 90, pfdy+75);
			
			g.setFont(fontSerifB14);
			g.drawString(m_fmaaltmode, pfdx+ 300, pfdy+75);
			
			g.setFont(fontSerifB14);
			g.drawString(m_fmaspdmode, pfdx+ 385, pfdy+75);
			
			g.setFont(fontHelP10);
			g.setColor(Color.GREEN);
			g.drawString("(FMA Alt mode)", pfdx + 280, pfdy + 88);
			
			g.setFont(fontHelP10);
			g.setColor(Color.GREEN);
			g.drawString("(FMA Spd mode)", pfdx + 375, pfdy + 88);
			
			m_graphics_obj = g;
			redrawCurrentThrottles();
			animateSpeedTape();
			
			g.setColor(Color.BLACK);
			g.fillRect(pfdx+10, spdtapey-150, 60, 40);
			
			//g.setColor(Color.red);
			g.fillRect(pfdx+10, spdtapey+210, 55, 30);
			
/************************************************************************************/
			
			//speed bug positioning
			m_has_cas_target_changed_b = (target_cas != previous_target_cas);
			//if(target_cas >  m_speed_tape_vector.get(0).get_speed_value() || (m_speed_bug_position < pfdy + spdtapey - (5*spdinc)))
			if(target_cas >  m_speed_tape_vector.get(0).get_speed_value() || (m_speed_bug_position < spdtapeBegin))
			{
				m_is_speed_bug_in_tape_range_b = false;
				//m_speed_bug_position = pfdy + spdtapey - (5*spdinc);
				m_speed_bug_position = spdtapeBegin;
			}
			
			//else if(target_cas <  m_speed_tape_vector.get(6).get_speed_value() || (m_speed_bug_position > pfdy + spdtapey + (5*spdinc)))
			else if(target_cas <  m_speed_tape_vector.get(6).get_speed_value() || (m_speed_bug_position > spdtapeEnd))
			{
				m_is_speed_bug_in_tape_range_b = false;
				//m_speed_bug_position = pfdy + spdtapey + (5*spdinc);
				m_speed_bug_position = spdtapeEnd;
			}
			else
			{
				m_is_speed_bug_in_tape_range_b = true;
				int correction = 0;
				//since spdinc = 32, for 10 kts on spd tape we are using 32 pixels
				//so for 1 knot 3.2 pixels are allocated
				double pixel_correction = 0.0;
				
				if((target_cas != current_CAS) && (m_is_first_time_b || m_has_cas_target_changed_b))
				{
					
					correction = current_CAS - target_cas;
					pixel_correction = 3.2 * correction;
					if(correction > 0)
					{
						m_speed_bug_position = spdtapeBegin + (5*spdinc) + (int)pixel_correction;
					}
					else
					{
						m_speed_bug_position = spdtapeBegin + (5*spdinc) - (int)pixel_correction;
					}
					/*if((target_cas <= m_speed_tape_vector.get(0).get_speed_value()) && (target_cas > m_speed_tape_vector.get(1).get_speed_value()))
					{
						//m_speed_bug_position = pfdy + spdtapey - (5*spdinc);
						correction = m_speed_tape_vector.get(0).get_speed_value() - target_cas;
						pixel_correction = 3.2 * correction;
						m_speed_bug_position = spdtapeBegin + (0*spdinc) + (int)pixel_correction;
					}
					else if((target_cas <= m_speed_tape_vector.get(1).get_speed_value()) && (target_cas > m_speed_tape_vector.get(2).get_speed_value()))
					{
						//m_speed_bug_position = pfdy + spdtapey - (4*spdinc);
						correction = m_speed_tape_vector.get(1).get_speed_value() - target_cas;
						pixel_correction = 3.2 * correction;
						m_speed_bug_position = spdtapeBegin + (1*spdinc) + (int)pixel_correction;
					}
					else if((target_cas <= m_speed_tape_vector.get(2).get_speed_value()) && (target_cas > m_speed_tape_vector.get(3).get_speed_value()))
					{
							//m_speed_bug_position = pfdy + spdtapey- (2*spdinc);
						correction = m_speed_tape_vector.get(2).get_speed_value() - target_cas;
						pixel_correction = 3.2 * correction;
						m_speed_bug_position = spdtapeBegin + (3*spdinc) + (int)pixel_correction;						
					}
					else if((target_cas <= m_speed_tape_vector.get(3).get_speed_value()) && (target_cas > m_speed_tape_vector.get(4).get_speed_value()))
					{
						m_speed_bug_position = pfdy + spdtapey + (2*spdinc);
						//correction = m_speed_tape_vector.get(3).get_speed_value() - target_cas;
						//pixel_correction = 3.2 * correction;
						//m_speed_bug_position = spdtapeBegin + (5*spdinc) + (int)pixel_correction;
					}
					else if((target_cas <= m_speed_tape_vector.get(4).get_speed_value()) && (target_cas > m_speed_tape_vector.get(5).get_speed_value()))
					{
						m_speed_bug_position = pfdy + spdtapey + (2*spdinc);						
						//correction = m_speed_tape_vector.get(4).get_speed_value() - target_cas;
						//pixel_correction = 3.2 * correction;
						//m_speed_bug_position = spdtapeBegin + (7*spdinc) + (int)pixel_correction;						
					}					
					else if((target_cas <= m_speed_tape_vector.get(5).get_speed_value()) && (target_cas > m_speed_tape_vector.get(6).get_speed_value()))
					{
						m_speed_bug_position = pfdy + spdtapey + (3*spdinc)+(spdinc/2);
						//correction = m_speed_tape_vector.get(5).get_speed_value() - target_cas;
						//pixel_correction = 3.2 * correction;
						//m_speed_bug_position = spdtapeBegin + (9*spdinc) + (int)pixel_correction;						
					}*/
					
					
					m_is_first_time_b = false;
					m_has_cas_target_changed_b = false;
				} 
			}
			
			//TODO- remove this hack and make m_is_vnav_engaged_b false through out
			m_is_vnav_engaged_b = false;
			if(current_CAS != target_cas)
			{
				if(m_is_at_engaged_b && m_is_ap_engaged_b && !m_is_thrust_retard_b)
				{
					if(m_is_modified_pfd)
					{
						g.drawImage(m_active_speed_bug_image_for_modified_pfd, pfdx+spdtapex+15, m_speed_bug_position, null);
					}
					else
					{
						g.drawImage(m_active_speed_bug_image, pfdx+spdtapex+15, m_speed_bug_position, null);
					}
				}
				//else if((m_is_thrust_held_b||m_is_thrust_retard_b) && m_is_at_engaged_b && m_is_vnav_engaged_b)
				else if((m_is_thrust_held_b||m_is_thrust_retard_b) && m_is_at_engaged_b)
				{
					if(m_is_modified_pfd)
					{
						g.drawImage(m_active_crossed_speed_bug_image_for_modified_pfd, pfdx+spdtapex+15, m_speed_bug_position, null);
					}
					else
					{
						g.drawImage(m_active_speed_bug_image, pfdx+spdtapex+15, m_speed_bug_position, null);
					}
				}
				else if(!m_is_at_engaged_b)
				{
					if(m_is_modified_pfd)
					{
						g.drawImage(m_inactive_crossed_speed_bug_image, pfdx+spdtapex+15, m_speed_bug_position, null);
					}
					else
					{
						g.drawImage(m_inactive_white_speed_bug_image, pfdx+spdtapex+15, m_speed_bug_position, null);
					}
				}
				else if (!(m_is_thrust_held_b||m_is_thrust_retard_b) && m_is_at_engaged_b && m_is_vnav_engaged_b)
				{
					if(m_is_modified_pfd)
					{
						g.drawImage(m_active_speed_bug_image_for_modified_pfd, pfdx+spdtapex+15, m_speed_bug_position, null);
					}
					else
					{
						g.drawImage(m_active_speed_bug_image, pfdx+spdtapex+15, m_speed_bug_position, null);
					}
				}
				else if ((m_is_at_engaged_b && !m_is_vnav_engaged_b) && !m_is_ap_engaged_b)
				{
					if(m_is_modified_pfd)
					{
						g.drawImage(m_inactive_crossed_speed_bug_image, pfdx+spdtapex+15, m_speed_bug_position, null);
					}
					else
					{
						g.drawImage(m_inactive_white_speed_bug_image, pfdx+spdtapex+15, m_speed_bug_position, null);
					}
					
				}
			}
			else
			{
				m_is_first_time_b = true;
			}
			
			//remove this hack while fixing vnav_engagement status
			m_is_vnav_engaged_b = true;
			
			//*****************************************************************************************//			
			/*
			if(m_is_at_engaged_b && m_is_vnav_engaged_b && ((m_sitNo == 0) ||(m_sitNo == 1) || (m_sitNo == 3)|| (m_sitNo == 5) || (m_sitNo == 6) || (m_sitNo == 7)
					   || (m_sitNo == 8) || (m_sitNo == 9)))
			{
				if(m_is_thrust_held_b)
				{
					g.drawImage(m_hold_engine_image, 763, 334, null);
				}
				else
				{
					g.drawImage(m_thrust_engine_image, 763, 334, null);
				}
				g.setColor(Color.white);
				g.setFont(fontHelB18);
			}

			else if(m_is_at_engaged_b && m_is_vnav_engaged_b && m_sitNo == 2)
			{
				g.drawImage(m_elevator_spd_tape_image, 763, 336, null);
				g.setColor(Color.white);
				g.setFont(fontHelB18);
			}
			
			else if(m_is_at_engaged_b && m_is_vnav_engaged_b && ((m_sitNo == 4) ))
			{

				if(m_is_thrust_held_b)
				{
					g.drawImage(m_hold_engine_image, 763, 334, null);
				}
				else
				{
					g.drawImage(m_idle_engine_image, 763, 334, null);
				}
				g.setColor(Color.white);
				g.setFont(fontHelB18);
			}
			
			else if((!m_is_at_engaged_b && !m_is_vnav_engaged_b) || (!m_is_at_engaged_b && m_is_vnav_engaged_b))
			{
				g.setColor(Color.white);
				//g.drawRect(pfdx+spdtapex-25,  pfdy+spdtapey-10, 48, 20);
				//draw current cas
				g.setColor(Color.WHITE);
				g.setFont(fontHelB18);
			}
			
			else if(m_is_at_engaged_b ||!m_is_vnav_engaged_b )
			{
				if(m_is_thrust_held_b)
				{
					g.drawImage(m_hold_engine_image, 763, 334, null);
				}
				else
				{
					g.drawImage(m_white_thrust_engine_image, 763, 334, null);
				}
				
				g.setColor(Color.WHITE);
				g.setFont(fontHelB12);
			}*/
				
			g.setColor(Color.WHITE);
			g.setFont(fontHelP18);
			g.drawImage(m_speed_magnifier_image, pfdx+spdtapex-35, pfdy+spdtapey-22,null);

			if(m_is_ap_engaged_b)
			{
				if(m_is_modified_pfd)
				{
					g.drawImage(m_active_speed_magnifier_image, pfdx+spdtapex-35, pfdy+spdtapey-22,null);
				}
			}
			else if(!m_is_ap_engaged_b || m_is_thrust_held_b)
			{
				if(m_is_modified_pfd)
				{
					g.drawImage(m_double_cross_image, pfdx+spdtapex-50, pfdy+spdtapey-20,null);
				}
			}
			
			if(m_is_thrust_retard_b)
			{
				g.drawImage(m_speed_magnifier_image, pfdx+spdtapex-35, pfdy+spdtapey-22,null);
				if(m_is_modified_pfd)
				{
					g.drawImage(m_double_cross_image, pfdx+spdtapex-50, pfdy+spdtapey-20,null);
					//g.drawImage(m_double_cross_image, pfdx+alttapex-50, pfdy+alttapey-20,null);
				}
			}

			g.drawString(Integer.toString(current_CAS), pfdx+spdtapex-20, pfdy+spdtapey+5);
			
			/**************************************************************************/
			
			//speed FMA
			if(m_is_at_engaged_b && m_is_vnav_engaged_b)
			{
				//g.setColor(Color.MAGENTA);
			}
			else if (!m_is_at_engaged_b || !m_is_vnav_engaged_b)
			{	
				g.setColor(Color.WHITE);
			}
			
			g.setColor(Color.WHITE);
			
			if(m_is_ap_engaged_b || !m_is_ap_engaged_b)
			{
				if(m_is_modified_pfd)
					g.setColor(Color.GREEN);				
				else
					g.setColor(Color.MAGENTA);	
			}

			g.setFont(fontHelB18);
			g.drawString(Integer.toString(target_cas),  pfdx+50,  pfdy+120);

			if (!m_is_at_engaged_b || m_is_thrust_held_b||m_is_thrust_retard_b)
			{
				//cross the speed target in white
				g.setColor(Color.WHITE);
				if(m_is_modified_pfd)
				{
					g.drawImage(m_speed_target_cross_image, pfdx + 37, pfdy + 100, null);
				}
			}
			
			
			//alt-tape visual cues
			/*****************************************************************/
			/*
			if(m_is_ap_engaged_b && m_is_vnav_engaged_b && ((m_sitNo == 0) ||(m_sitNo == 1) || (m_sitNo == 3) || (m_sitNo == 5) || (m_sitNo == 6) || (m_sitNo == 7)
			   || (m_sitNo == 8) || (m_sitNo == 9)))
			{	
				g.drawImage(m_elevator_alt_tape_image, 1230, 337, null);
				g.setColor(Color.WHITE);
				g.setFont(fontHelB12);
			}

			else if(m_is_ap_engaged_b && m_is_vnav_engaged_b && m_sitNo == 2)
			{
				g.drawImage(m_alt_tape_engine_image, 1230, 332, null);
				g.setColor(Color.white);
				g.setFont(fontHelB12);
			}

			else if(m_is_ap_engaged_b && m_is_vnav_engaged_b && m_sitNo == 4)
			{
				g.drawImage(m_elevator_alt_tape_image, 1230, 337, null);
				g.setColor(Color.MAGENTA);
				g.setFont(fontHelB12);
			}
			if((m_is_ap_engaged_b && m_is_vnav_engaged_b) || (!m_is_ap_engaged_b && m_is_vnav_engaged_b))
			{
				g.setColor(Color.MAGENTA);
			}*/

			g.setColor(Color.white);
			g.setFont(fontHelB18);
			g.drawImage(m_alt_magnifier_image, pfdx+alttapex-30, pfdy+alttapey-23, null);
				
			//Current alt
			if(m_is_ap_engaged_b)
			{
				if(m_is_modified_pfd)
				{
					g.drawImage(m_active_alt_magnifier_image, pfdx+alttapex-30, pfdy+alttapey-23, null);
				}
			}			
			
			g.setFont(fontHelB18);
			g.drawString(Integer.toString(current_altitude), pfdx+alttapex-5, pfdy+alttapey+5);			
			
			/***************************************************************************/
			//target altitude above the tape
			g.setColor(Color.white);
			if(m_is_ap_engaged_b || !m_is_ap_engaged_b) //TODO- try to get rid of this
			{
				if(m_is_modified_pfd)
				{
					g.setColor(Color.GREEN);
				}
				else
				{
					g.setColor(Color.MAGENTA);
				}
			}
			g.setFont(fontHelB18);
			g.drawString(Integer.toString(target_altitude), pfdx+fmaalttgtx, pfdy+fmay+18);
			 
			if(!m_is_ap_engaged_b)
			{
				//g.setColor(Color.white);
				//White box surrounding current alt
				//g.drawRect(pfdx+alttapex-20, pfdy+alttapey-10, 48, 20);

				//cross the altitude target
				g.setColor(Color.GREEN);
				if(m_is_modified_pfd)
				{
					g.drawImage(m_speed_target_cross_image, pfdx + fmaalttgtx-9, pfdy+fmay-5, null);
				}
			}
			
			/*else if(m_is_ap_engaged_b && !m_is_vnav_engaged_b)
			{
				g.setColor(Color.WHITE);
				g.drawImage(m_white_elevator_alt_tape_image, 1230, 337, null);
			}*/
			
			set_procedure_list_focus();
			g.setColor(Color.BLACK);
			g.drawRect(770, 610, 200, 90);
			g.setColor(Color.BLACK);
			g.fillRect(770, 610, 200, 90);
			
			//Update Current DTD
			g.setFont(fontHelP12);
			g.setColor(Color.WHITE);
			g.drawString("Current DTD:", 782, 628);
			g.setFont(fontHelB12);
			g.setColor(Color.GREEN);
			g.drawString(Double.toString(m_mcpParameters.get_current_DTD()) + " nm",870, 628);
			
			//update GLIDE SLOPE STATUS
			g.setFont(fontHelP12);
			g.setColor(Color.WHITE);
			g.drawString("A/C position:", 782, 658);
			g.setFont(fontHelB12);
			
			String gs_status = "";
			if(activeSitInput[11][0] == 1)
			{
				g.setColor(Color.RED);
				gs_status = "Above GS path";
			}
			if(activeSitInput[11][1] == 1)
			{
				g.setColor(Color.GREEN);
				gs_status = "On GS path";
			}
			if(activeSitInput[11][2] == 1)
			{
				g.setColor(Color.RED);
				gs_status = "Below GS path";
			}
			g.drawString(gs_status,870, 658);
			
			//end all if current alt <= 10 ft
			if(m_mcpParameters.get_current_altitude() <= 30)
			{
				stopSpeedTapeTimer(true);
				m_main_timer.stop_timer_at_once();				
			}
			
			g.setFont(fontHelP12);
			g.setColor(Color.WHITE);
			g.drawString("Alt Difference:", 782, 688);
			g.setFont(fontHelB12);
			
			int alt_difference = m_mcpParameters.get_alt_diff();
			g.drawString(Integer.toString(alt_difference), 870, 688);
			
			redrawVerticalProfile(g);
			redrawYoke();
			
			g.setColor(Color.blue);
			g.setFont(fontHelB12);
			g.drawString("Throttle%: " + Integer.toString(m_throttles_value), 1200, 650);
			
			g.setColor(Color.blue);
			g.setFont(fontHelB12);
			g.drawString("Current VS: " + Integer.toString(m_vertical_speed), 1200, 700);
			
			g.setColor(Color.blue);
			g.setFont(fontHelB12);
			g.drawString("Current CAS: " + Integer.toString(current_CAS), 1200, 600);
			
		}//end of paint method
		
		public void drawGraph(Graphics g)
		{			
			g.drawImage(m_graph_image,m_graph_x,50,null);		
		}
		
		public void set_procedure_list_focus()
		{
			int list_index = 0;
			if(m_has_situation_changed)
			{
				if ((m_active_procedure.compareToIgnoreCase("CLB MAINT") == 0) || 
					((m_active_procedure.compareToIgnoreCase("CAPTURE ALT")) == 0 && (m_mcpParameters.get_current_VS() > 0)))
				{
					list_index = 0;
				}
				
				else if (m_active_procedure.compareToIgnoreCase("CLB MAINT WITH ROC") == 0)
				{
					list_index = 1;
				}
						
				else if ((m_active_procedure.compareToIgnoreCase("DES MAINT") == 0) ||
				((m_active_procedure.compareToIgnoreCase("CAPTURE ALT")) == 0 && (m_mcpParameters.get_current_VS() < 0)))
				{
					list_index = 2;
				}
				
				else if (m_active_procedure.compareToIgnoreCase("DES MAINT WITH ROD") == 0)
				{
					list_index = 3;
				}
					
				else if (m_active_procedure.compareToIgnoreCase("MAINT") == 0 || m_active_procedure.compareToIgnoreCase("HOLD ALT") == 0  )
				{
					list_index = 4;
				}
				
				else if (m_active_procedure.compareToIgnoreCase("CAPTURE GS FROM ABOVE") == 0 && m_is_vnav_engaged_b)
				{
					list_index = 6;
				}
				
				else if (m_active_procedure.compareToIgnoreCase("MAINT CAPTURED GS") == 0 && m_is_vnav_engaged_b)
				{
					list_index = 7;
				}
				
				else if (m_active_procedure.compareToIgnoreCase("CAPTURE GS FROM BELOW") == 0 && m_is_vnav_engaged_b)
				{
					list_index = 8;
				}
				m_procedure_list.getProcedureList().setSelectedIndex(list_index);
			}
		}
		
		private void animateSpeedTape()
		{
			int target_cas = m_mcpParameters.get_target_CAS();
			int current_cas = m_mcpParameters.get_current_CAS();

			if(current_cas <= 140) //&& current_cas > 100 )
			{
				m_timer.setDelay(300);
			}
			/*else if(current_cas <= 100 )
			{
				m_timer.setDelay(300);
			}*/
			
			if(m_is_main_timer_running && !m_timer.isRunning() && target_cas != current_cas && m_is_at_engaged_b)
			{
				m_timer.start();
			}
			stopSpeedTapeTimer(false);
		}
		
		private void stopSpeedTapeTimer(boolean is_force_stop)
		{
			int target_cas = m_mcpParameters.get_target_CAS();
			int current_cas = m_mcpParameters.get_current_CAS();
			if( m_timer.isRunning() && ((target_cas == current_cas) || is_force_stop))
			{
				{
					m_timer.stop();
				}			
			}
		}
		
		/*private void animateGraph()
		{
			double current_dtd = m_mcpParameters.get_current_DTD();
			if(m_is_main_timer_running && !m_graph_x_axis_timer.isRunning() && current_dtd <= 8)
			{
				m_graph_x_axis_timer.start();
			}			
			double ac_speed = m_mcpParameters.get_distance_covered_in_time_slice();
			if(current_dtd > 3)
			{
				ac_speed = ac_speed*1350;
			}
			else if(current_dtd <= 3)
			{
				ac_speed = ac_speed*2500;
			}
			
			else if(current_dtd <= 2)
			{
				ac_speed = ac_speed*2900;
			}
			
			m_graph_x_axis_timer.setDelay((int)ac_speed);
			
			if((m_graph_x_axis_timer.isRunning()) && current_dtd <= 1)
			{
				{
					m_graph_x_axis_timer.stop();
				}			
			}
		}*/
		
		private void animateVertProfYaxis()
		{
			int current_alt = m_mcpParameters.get_current_altitude();
			if(m_is_main_timer_running && !m_vertical_profile_y_axis_timer.isRunning())
			{
				m_vertical_profile_y_axis_timer.start();
			}
			if((m_vertical_profile_y_axis_timer.isRunning()) && m_vertical_prof_y_axis_vector[2][1] == 500)
			{
				{
					m_vertical_profile_y_axis_timer.stop();
				}			
			}
		}
		
		private void setupGraphTimer()
		{
			ActionListener GraphActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					/*double ac_speed = m_mcpParameters.get_distance_covered_in_time_slice();
					double dtd = m_mcpParameters.get_current_DTD();
					ac_speed = ac_speed*1350;
					if(dtd <= 2)
					{
						ac_speed = ac_speed*1800;
					}*/
					m_graph_x = m_graph_x - 1;
					//m_graph_x_axis_timer.setDelay((int)ac_speed);
				}
			};
			m_graph_x_axis_timer = new Timer(1000, GraphActionListener);
		}

		private void setupVerticalProfileYTimer()
		{
				ActionListener verticalProfileYaxisActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					
					if(m_vertical_prof_y_axis_vector[1][0] >= vp_y) 
					{
						for(int i = 0; i < 4; i++)
						{
							if(m_vertical_prof_y_axis_vector[i][0] > 0)
							m_vertical_prof_y_axis_vector[i][0]--;
						}
					}
					else
					{
						for(int i = 1; i < 3; i++)
						{
							m_vertical_prof_y_axis_vector[i][0]--;							
						}
					}
					
					for(int i = 0; i < 4; i++)
					{
						if(m_vertical_prof_y_axis_vector[i][0] > 0)
							m_vertical_prof_y_axis_line_vector[i]--;
					}
				}
			};
			m_vertical_profile_y_axis_timer = new Timer(1000, verticalProfileYaxisActionListener); 
		}
	
		
		private void setupSpeedTapeTimer()
		{
				ActionListener speedTapeActionListener = new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					int pfdy = 50;
					int spdinc = 32;
					int spdtapey = 300;
					int target_cas = m_mcpParameters.get_target_CAS();
					int current_cas = m_mcpParameters.get_current_CAS();
					boolean is_at_in_hold = m_mcpParameters.get_is_at_in_hold_mode();
					boolean is_at_in_retard = m_mcpParameters.get_is_at_in_retard_mode();
					boolean is_ac_stalled = false;
					// if current_cas <= 100 then recovery not possible
					if(current_cas <= 110)
					{
						is_ac_stalled = true;
					}
					if(!is_ac_stalled && (target_cas >= current_cas && !(is_at_in_hold || is_at_in_retard)))
					{
						if((m_speed_tape_vector.get(5).get_y_position() >= pfdy+spdtapey+(5*spdinc)) 
						|| (m_speed_tape_vector.get(6).get_y_position() < pfdy+spdtapey+(5*spdinc)))
						{
							for(int i = 0; i < 7; i++)
							{
								m_speed_tape_vector.get(i).increment_y_position();
							}
						}
						else
						{
							for(int i = 1; i < 6; i++)
							{
								m_speed_tape_vector.get(i).increment_y_position();							
							}
						}
						for(int i = 0; i < 11; i++)
						{
							m_speed_tape_line_vector[i]++;
						}
						
						if(m_barber_pole_visible && (m_yellow_pole_position <= 500))
						{
							m_yellow_pole_position++;
							m_source_pole_y_position++;
						}
						if(m_is_speed_bug_in_tape_range_b)
						{
							m_speed_bug_position++;
						}
					}
					else
					{
						if((m_speed_tape_vector.get(1).get_y_position() <= pfdy+spdtapey-(5*spdinc)) ||
						  (m_speed_tape_vector.get(0).get_y_position() > pfdy+spdtapey-(5*spdinc)))
						{
							for(int i = 0; i < 7; i++)
							{
								m_speed_tape_vector.get(i).decrement_y_position();
							}
						}
						else
						{
							for(int i = 1; i < 6; i++)
							{
								m_speed_tape_vector.get(i).decrement_y_position();
							}
						}
						for(int i = 0; i < 11; i++)
						{
							m_speed_tape_line_vector[i]--;
						}
						if(m_barber_pole_visible && m_yellow_pole_position >= pfdy+spdtapey- (5*spdinc))
						{
							m_yellow_pole_position--;
							m_source_pole_y_position--;
						}
						if(m_is_speed_bug_in_tape_range_b)
						{
							m_speed_bug_position--;
						}
					}
				}
			};
			
			m_timer = new Timer(500, speedTapeActionListener);			
		}
	
		/*	private void initiate_process_altitude_tape(){
			Graphics g = m_graphics_obj;
			int current_altitude;
			int pfdx = 400;
			int pfdy = 50;
			int alttapex = 510;
			int alttapey = 300;
			int alttapeheight = 320;
			int altinc = (alttapeheight/10);
			int i = 1;

			//alt tape frame
			g.setColor(Color.darkGray);
			g.fillRect(pfdx+alttapex-(60/2), pfdy+alttapey-(320/2), 60, 320);
			//current alt
			current_altitude = m_mcpParameters.get_current_altitude();
			g.setColor(Color.white);
			g.drawRect(pfdx+alttapex-10, pfdy+alttapey-10, 40, 20);
			g.setColor(Color.black);
			g.fillRect(pfdx+alttapex-10, pfdy+alttapey-10, 40, 20);
			g.setColor(Color.green);
			g.setFont(fontSerifB14);	
			g.drawString(Integer.toString(current_altitude), pfdx+alttapex-6, pfdy+alttapey+5);
			for(int j=1; j<6; j++)
			{
				g.setColor(Color.white);
				g.drawLine(pfdx+alttapex-30, pfdy+alttapey-(j*altinc), pfdx+alttapex-25,pfdy+alttapey-(j*altinc));
				g.drawLine(pfdx+alttapex-30, pfdy+alttapey+(j*altinc), pfdx+alttapex-25,pfdy+alttapey+(j*altinc));
			}
			m_rate_of_alt_tape_roll = 100;
			m_timer.start();
		}*/
		
/*				private void init_alt_tape_timer(){
				m_timer = new Timer(m_rate_of_alt_tape_roll, new ActionListener(){
				int current_altitude;
				int pfdx = 400;
				int pfdy = 50;
				int alttapex = 510;
				int alttapey = 300;
				int alttapeheight = 320;
				int altinc = (alttapeheight/10);
				//int i = 1;
				Graphics g = m_graphics_obj;
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					//g.setColor(Color.green);
					//g.drawRect(pfdx+alttapex-10, pfdy+alttapey-10, 40, 80);
					
					if(m_dummy_int < alttapeheight)
					{
						m_dummy_int++;
					}
				}
			});
		}*/
		public void redrawYoke(){
			Graphics g = m_graphics_obj;
			g.drawImage(m_yoke_frame_image, yoke_frame_x, yoke_frame_y, null);
			if(m_yoke_released_b)
			{
				if(yoke_bar_y > 630)
				{
					g.drawImage(m_yoke_bar_image, yoke_bar_x, yoke_bar_y, null);
					yoke_bar_y--;
				}
				else if(yoke_bar_y < 630)
				{
					g.drawImage(m_yoke_bar_image, yoke_bar_x, yoke_bar_y, null);
					yoke_bar_y++;
				}
				else
				{
					g.drawImage(m_yoke_bar_image, yoke_bar_x, yoke_bar_y, null);
				}
			}
			else
			{
				g.drawImage(m_yoke_bar_image, yoke_bar_x, yoke_bar_y, null);
			}

		}
		public void redrawCurrentThrottles(){
			int mcpx = 400; 
			int mcpy = 620;
			int throttles_column1_x = mcpx + 147;
			int throttles_column2_x = throttles_column1_x + 20;
			//int throttles_column1_top =  

			int throttle_column_top = mcpy+10;
			int throttle_column_bottom = throttle_column_top + 72;
			int length_throttle_column = throttle_column_bottom - throttle_column_top;
			Graphics g = m_graphics_obj;
			g.setColor(Color.WHITE);
			g.drawLine(throttles_column1_x+2, 620+10, throttles_column1_x+5, 620+10);
			g.drawLine(throttles_column1_x+2, 620+45, throttles_column1_x+5, 620+45);	
			g.drawLine(throttles_column1_x+2, 620+80, throttles_column1_x+5, 620+80);

			g.drawLine(throttles_column2_x+2, 620+10, throttles_column2_x+5, 620+10);
			g.drawLine(throttles_column2_x+2, 620+45, throttles_column2_x+5, 620+45);	
			g.drawLine(throttles_column2_x+2, 620+80, throttles_column2_x+5, 620+80);
			if(!m_mcpParameters.get_is_scenario_specific())
			{
				if(m_is_at_engaged_b && !m_mcpParameters.getIsInFAF())
				{
					if(m_fmaspdmode == "THRUST")
					{
						if(m_throttle_y_cord > (throttle_column_bottom - length_throttle_column))
						{
							g.setColor(Color.RED);
							g.drawRect(throttles_column1_x, m_throttle_y_cord, 16, 8);
							g.drawRect(throttles_column2_x, m_throttle_y_cord, 16, 8);
							m_throttle_y_cord--;
						}
						else
						{
							g.setColor(Color.RED);
							g.drawRect(throttles_column1_x, m_throttle_y_cord, 16, 8);
							g.drawRect(throttles_column2_x, m_throttle_y_cord, 16, 8);
							//m_throttle_y_cord = m_dummy_int;
						}
					}

					else if (m_fmaspdmode == "IDLE")
					{
						if(m_throttle_y_cord < throttle_column_bottom-10)
						{
							g.setColor(Color.RED);
							g.drawRect(throttles_column1_x, m_throttle_y_cord, 16, 8);
							g.drawRect(throttles_column2_x, m_throttle_y_cord, 16, 8);
							m_throttle_y_cord++;
						}
						else
						{
							g.setColor(Color.RED);
							g.drawRect(throttles_column1_x, m_throttle_y_cord, 16, 8);
							g.drawRect(throttles_column2_x, m_throttle_y_cord, 16, 8);
						}
					}
				}
				else if(m_is_at_engaged_b && m_mcpParameters.getIsInFAF())
				{
					if(m_throttle_y_cord < (throttle_column_bottom-25))
					{
						g.setColor(Color.RED);
						g.drawRect(throttles_column1_x, m_throttle_y_cord, 16, 8);
						g.drawRect(throttles_column2_x, m_throttle_y_cord, 16, 8);
						m_throttle_y_cord++;
					}
					else
					{
						g.setColor(Color.RED);
						g.drawRect(throttles_column1_x, m_throttle_y_cord, 16, 8);
						g.drawRect(throttles_column2_x, m_throttle_y_cord, 16, 8);
					}
				}

				else
				{	
					g.setColor(Color.RED);
					g.drawRect(throttles_column1_x, m_throttle_y_cord, 16, 8);
					g.drawRect(throttles_column2_x, m_throttle_y_cord, 16, 8);
				}
			}
			else // m_throttle_y_cord idle = 697; length of column = 75
			{
				double temp = m_throttles_value/100.0;
				temp = (72*temp);
				int throttle_target_position = (int) (throttle_column_bottom - temp) ;
				if(m_throttle_y_cord > throttle_target_position)
				{
					g.setColor(Color.RED);
					g.drawRect(throttles_column1_x, m_throttle_y_cord, 16, 8);
					g.drawRect(throttles_column2_x, m_throttle_y_cord, 16, 8);
					m_throttle_y_cord--;
				}
				else
				{
					g.setColor(Color.RED);
					g.drawRect(throttles_column1_x, m_throttle_y_cord, 16, 8);
					g.drawRect(throttles_column2_x, m_throttle_y_cord, 16, 8);
				}
				
				if (m_throttle_y_cord < throttle_target_position)
				{
					g.setColor(Color.RED);
					g.drawRect(throttles_column1_x, m_throttle_y_cord, 16, 8);
					g.drawRect(throttles_column2_x, m_throttle_y_cord, 16, 8);
					m_throttle_y_cord++;
				}
				else
				{
					g.setColor(Color.RED);
					g.drawRect(throttles_column1_x, m_throttle_y_cord, 16, 8);
					g.drawRect(throttles_column2_x, m_throttle_y_cord, 16, 8);
				}
			}
		}
		
		private void interpret_all_input_values(){
			int current_vs = m_mcpParameters.get_current_VS();
			boolean is_in_capture_region_b = m_mcpParameters.getIsInCaptureRegion();
			boolean is_ac_in_FAF_b = m_mcpParameters.getIsInFAF(); 
			int current_CAS = m_mcpParameters.get_current_CAS();
			int  target_altitude = m_mcpParameters.get_target_mcpaltitude();
			int current_altitude = m_mcpParameters.get_current_altitude();
			//int current_vs = m_mcpParameters.get_current_VS();
			// Altitude Active State Input
		/*	if(current_altitude == 0)
			{
				activeSitInput[0][0] = 0;
				activeSitInput[0][1] = 0;
				activeSitInput[0][2] = 0;
				activeSitInput[0][3] = 1;
			}
			else if (current_altitude> 0 && current_altitude <= 400)
			{
				activeSitInput[0][0] = 0;
				activeSitInput[0][1] = 1;
				activeSitInput[0][2] = 0;
				activeSitInput[0][3] = 0;
			}
			else if (current_altitude > 400 && current_altitude<= 1500)
			{
				activeSitInput[0][0] = 0;
				activeSitInput[0][1] = 0;
				activeSitInput[0][2] = 1;
				activeSitInput[0][3] = 0;
			}
			else if (current_altitude > 1500)
			{
				activeSitInput[0][0] = 1;
				activeSitInput[0][1] = 0;
				activeSitInput[0][2] = 0;
				activeSitInput[0][3] = 0;
			}

			//Flight Directors
			activeSitInput[1][1] = 1;

			//Radio Alt 
			if(m_is_radio_alt_valid_b)
			{
				activeSitInput[2][0] = 0;
				activeSitInput[2][1] = 1;
			}
			else
			{
				activeSitInput[2][0] = 1;
				activeSitInput[2][1] = 0;
			}*/

			/*"current_vs wheel Rotated", 
			"Alt knob pulled", 
			"Alt knob pushed", 
			"None"*/
			//MCP Knob/Wheel Action
		/*	if(m_vs_wheel_rotated_b || m_alt_knob_pulled_b || m_alt_knob_pushed_b)
			{
				if(m_vs_wheel_rotated_b)
				{
					activeSitInput[3][0] = 1;

				}
				if(m_alt_knob_pulled_b)
				{
					activeSitInput[3][1] = 1;
					activeSitInput[3][2] = 0;
				}
				if(m_alt_knob_pushed_b)
				{
					activeSitInput[3][1] = 0;
					activeSitInput[3][2] = 1;
				}
				activeSitInput[3][3] = 0;
			}
			else
			{
				activeSitInput[3][0] = 0;
				activeSitInput[3][1] = 0;
				activeSitInput[3][2] = 0;
				activeSitInput[3][3] = 1;
			}

			interpret_previous_fma(); */
			
			//CURRENT ALT
			/*
			"is in CLIMB region",
		    "is in CLIMB capture region",
	   		"is within HOLD REGION (+/- 30' and vs < 60 fps),
	   		"is AT target",
			"is in DESCENT capture region",
			"is in DESCENT region"*/ 

			activeSitInput[5][0] = 0;
			activeSitInput[5][1] = 0;
			activeSitInput[5][2] = 0;
			activeSitInput[5][3] = 0;
			activeSitInput[5][4] = 0;
			activeSitInput[5][5] = 0;

			if(current_altitude - target_altitude == 0)
			{
				activeSitInput[5][3] = 1;
			}

			else
			{
				if(!is_in_capture_region_b)
				{
					if(current_altitude < target_altitude) //is in Climb region
					{
						activeSitInput[5][0] = 1;
					}
					else if(current_altitude > target_altitude) //is in Descent region
					{
						activeSitInput[5][5] = 1;
					}
				}

				else if(is_in_capture_region_b)
				{	
					if ((target_altitude - current_altitude >= -30) && ((m_mcpParameters.get_current_VS() >= -360) && (m_mcpParameters.get_current_VS() < 0))||
					   ((target_altitude - current_altitude <= 30) && ((m_mcpParameters.get_current_VS()<= 360) && (m_mcpParameters.get_current_VS() > 0)))) //is in HOLD region
					{
						activeSitInput[5][2] = 1;
					}			

					else if(current_altitude - target_altitude > 30) //is in Descent capture region
					{
						activeSitInput[5][4] = 1;
					}

					else if(target_altitude - current_altitude > 30) //is in climb capture region
					{
						activeSitInput[5][1] = 1;
					}
				}
			}

			//Current Vertical Speed
			/*
			 * 		"is greater than + 900 fpm",
					"is between +900 and +300 fpm",
					"is between +300 fpm and 0",
					"is zero",
					"is between 0 fpm and -300",
					"is between -300 and -900 fpm",
					"is less than -900 fpm"
			 */
			for (int k = 0; k < sitInputStates[6].length; k++)
			{
				activeSitInput[6][k] = 0;
			}

			if(current_vs > 900)
			{
				activeSitInput[6][0] = 1;
			}
			else if(current_vs > 300 && current_vs <= 900)
			{
				activeSitInput[6][1] = 1;
			}
			else if (current_vs > 0 && current_vs <= 300)
			{
				activeSitInput[6][2] = 1;
			}
			else if (current_vs == 0)
			{
				activeSitInput[6][3] = 1;
			}
			else if (current_vs < 0 && current_vs >= -300)
			{
				activeSitInput[6][4] = 1;
			}
			else if (current_vs >= -900 && current_vs < -300)
			{
				activeSitInput[6][5] = 1;
			}
			else if (current_vs < -900)
			{
				activeSitInput[6][6] = 1;
			}

			//Airspeed
			/*
			 * 	
					0)"is above vmax+10", 
					1)"is above vmax+10 in 10 secs",
					2)"is vmax+5 to vmax+10",
					3)"is vmax+5 to vmax+10 in 10 secs",
					4)"is vmin + 5 to vmax - 5",
					5)"is vmin - 5 to vmin -10 in 10 secs",
					6)"is vmin -5 to vmin -10",
					7)"is below vmin -10 in 10 secs",
					8)"is less than vmin -10"
			 */
			
			for (int k = 0; k < sitInputStates[7].length; k++)
			{
				activeSitInput[7][k] = 0;
			}
			if(current_CAS > m_VMAX + 10)
			{
				activeSitInput[7][0] = 1;
			}
			else if((current_CAS > m_VMAX + 5) && (current_CAS <= m_VMAX + 10))
			{
				activeSitInput[7][2] = 1;
			}
			else if ((current_CAS > m_VMIN + 5) && (current_CAS < m_VMAX - 5))
			{
				activeSitInput[7][4] = 1;
			}
			else if((current_CAS > m_VMIN - 5) && (current_CAS <= m_VMIN -10 ))
			{
				activeSitInput[7][6] = 1;
			}
			else if(current_CAS < m_VMIN - 10)
			{
				activeSitInput[7][8] = 1;
			}

			//MCP VS
			/*"is more than 0 fpm",
			"is zero",
			"is less than 0 fpm"*/
			if(m_mcpParameters.get_target_VS() > 0)
			{
				activeSitInput[8][0] = 1;
				activeSitInput[8][1] = 0;
				activeSitInput[8][2] = 0;
			}
			else if(m_mcpParameters.get_target_VS()== 0)
			{
				activeSitInput[8][0] = 0;
				activeSitInput[8][1] = 1;
				activeSitInput[8][2] = 0;
			}
			else if(m_mcpParameters.get_target_VS() < 0)
			{
				activeSitInput[8][0] = 0;
				activeSitInput[8][1] = 0;
				activeSitInput[8][2] = 1;
			}

			//Auto Throttle
			// Always set
				activeSitInput[9][0] = 0;
				activeSitInput[9][1] = 0;
				activeSitInput[9][2] = 1;
			
			//PROF
			/*
			 * 		"Transitions to Engaged",
					"Engaged",
					"Transitions to disengaged",
					"Disengaged"
			 */
				//always engaged
				//activeSitInput[10][0] = 0;
				//activeSitInput[10][1] = 1;
				//activeSitInput[10][2] = 0;
				//activeSitInput[10][3] = 0;
			
			if (m_is_vnav_engaged_b && activeSitInput[10][1] == 0 && activeSitInput[10][0] == 0) //transition to engaged
			{
				activeSitInput[10][0] = 1;
				activeSitInput[10][1] = 0;
				activeSitInput[10][2] = 0;
				activeSitInput[10][3] = 0;
			}
			else if (m_is_vnav_engaged_b && activeSitInput[10][0] == 1) //VNAV engages if last cycle's state was 'transition to engaged'
			{
				activeSitInput[10][0] = 0;
				activeSitInput[10][1] = 1;
				activeSitInput[10][2] = 0;
				activeSitInput[10][3] = 0;
			}
			else if (!m_is_vnav_engaged_b && activeSitInput[10][1] == 1) 
				//VNAV transition to disengaged if last cycle's state was engaged and now PROF button has been clicked again
			{
				activeSitInput[10][0] = 0;
				activeSitInput[10][1] = 0;
				activeSitInput[10][2] = 1;
				activeSitInput[10][3] = 0;
			}	
			
			else if (!m_is_vnav_engaged_b) 
			{
				activeSitInput[10][0] = 0;
				activeSitInput[10][1] = 0;
				activeSitInput[10][2] = 0;
				activeSitInput[10][3] = 1;
			}	
				
				//GLIDE SLOPE
				if(is_ac_in_FAF_b && m_is_vnav_engaged_b)
				{
					if(m_mcpParameters.get_ac_position_wrt_gs_tgt() == 0) //above
					{
						activeSitInput[11][0] = 1; //above
						activeSitInput[11][1] = 0;
						activeSitInput[11][2] = 0;
					}
					if(m_mcpParameters.get_ac_position_wrt_gs_tgt() == 1) //equal
					{
						activeSitInput[11][0] = 0;
						activeSitInput[11][1] = 1; //equal
						activeSitInput[11][2] = 0; 
					}
					if(m_mcpParameters.get_ac_position_wrt_gs_tgt() == 2) //below
					{
						activeSitInput[11][0] = 0;
						activeSitInput[11][1] = 0; 
						activeSitInput[11][2] = 1; //below
					}
				}
				else
				{
					activeSitInput[11][0] = 0;
					activeSitInput[11][1] = 0;
					activeSitInput[11][2] = 0;
				}
		}

		private void interpret_previous_fma(){
			//Prev Speed|Alt FMA
			/*
			 * PITCH | T/O Clamp", 
					"PITCH | Go Around", 
					"PITCH | CLB THRUST",
					"PITCH | IDLE",
					"THRUST | VS",
					"THRUST | ALT HOLD",
					"THRUST | ALT HOLD (CAP)",
					"THRUST | G/S",
					"RETARD | FLARE",
					"WINDSHEAR | WINDSHEAR"
			 */
			for (int k = 0; k < sitInputStates[4].length; k++)
			{
				activeSitInput[4][k] = 0;
			}
			if(m_previous_fmaspdmode == "PITCH" && m_previous_fmsaltmode == "T/O CLAMP"){
				activeSitInput[4][0] = 1;
			}
			else if(m_previous_fmaspdmode == "PITCH" && m_previous_fmsaltmode == "GO AROUND"){
				activeSitInput[4][1] = 1;
			}
			else if(m_previous_fmaspdmode == "PITCH" && m_previous_fmsaltmode == "CLB THRUST"){

				activeSitInput[4][2] = 1;
			}
			else if(m_previous_fmaspdmode == "PITCH" && m_previous_fmsaltmode == "IDLE"){

				activeSitInput[4][3] = 1;
			}
			else if(m_previous_fmaspdmode == "THRUST" && m_previous_fmsaltmode == "VS"){

				activeSitInput[4][4] = 1;
			}
			else if(m_previous_fmaspdmode.compareTo("THRUST") == 0 && m_previous_fmsaltmode.compareTo("ALT HOLD") == 0){

				activeSitInput[4][5] = 1;
			}
			else if(m_previous_fmaspdmode.compareTo("THRUST") == 0 && m_previous_fmsaltmode.compareTo("ALT CAP") == 0){

				activeSitInput[4][6] = 1;
			}
			else if(m_previous_fmaspdmode.compareTo("THRUST") == 0 && m_previous_fmsaltmode.compareTo("G/S") == 0){

				activeSitInput[4][7] = 1;
			}
			else if(m_previous_fmaspdmode == "RETARD" && m_previous_fmsaltmode == "FLARE"){

				activeSitInput[4][8] = 1;
			}
			else if(m_previous_fmaspdmode == "WINDSHEAR" && m_previous_fmsaltmode == "WINDSHEAR"){

				activeSitInput[4][9] = 1;
			}

		}
		private int analyze_situation(int[][][] sitDef)
		{
			/*
			 * Sit #1- LEVEL FLT
			 * Sit #2- ALT CAPTURE
			 * Sit #3- CLB	
			 * Sit #4- DES
			 * 				*                       			Sit#1  #2  #3  #4
			 * ------------------------------------------------------------------
	0  	ALTITUDE			is above 1500' Baro						1	1	1	1
							is between 400' RA and 1500' Baro		1	1	1	1
							is below 400' RA						1	1	1	1
							is on Ground							0	0	0	0
								
	1	Flight Directors	None									1	1	1	1
							Single									1	1	1	1
							Dual									1	1	1	1
								
	2	Radio Alt 			is not Valid							1	1	1	1
							is Valid								1	1	1	1
								
	3	MCP Knob/Wheel 		VS wheel Rotated						0	0	0	0
		Action				Alt knob pulled							0	0	1	1
							Alt knob pushed							0	0	0	0
							None									1	1	1	1
								
	4	Prev Sped|Alt FMA	PITCH | T/O Clamp						1	1	1	1
							PITCH | Go Around 						1	1	1	1
							PITCH | CLB THRUST						1	1	1	1
							PITCH | IDLE							1	1	1	1
							THRUST | VS								1	1	1	1
							THRUST | ALT HOLD						1	1	1	1
							THRUST | ALT HOLD (CAP)					1	1	1	1
							THRUST | G/S							1	1	1	1
							RETARD | FLARE							1	1	1	1
							WINDSHEAR | WINDSHEAR					1	1	1	1
								
	5	MCP ALT 			is above capture region					0	0	0	1
							is within above capture region			0	1	0	0
							is within above overshoot region		0	0	0	0
							is within +/- 60 ft						1	0	0	0
							is within below overshoot region		0	0	0	0
							is within below capture region			0	1	0	0
							is below capture region					0	0	1	0
								
	6	Vertical Speed		is greater than + 900 fpm				0	1	1	1
							is between +900 and +300 fpm			0	1	1	1
							is between +/- 300 fpm					1	0	1	1
							is between -300 and -900 fpm			0	1	1	1
							is less than -900 fpm					0	1	1	1
								
	7	Airspeed			is above vmax+10						0	0	0	0
							is above vmax+10 in 10 secs				0	0	0	0
							is vmax+5 to vmax+10					0	0	0	0
							is vmax+5 to vmax+10 in 10 secs			0	0	0	0
							is vmax + 5 to vmin - 5					1	1	1	1
							is vmin - 5 to vmin -10 in 10 secs		0	0	0	0
							is vmin -5 to vmin -10					0	0	0	0
							is below vmin -10 in 10 secs			0	0	0	0
							is less than vmin -10					0	0	0	0
									
	8	MCP VS				is more than 100 fpm					0	0	0	0
							is zero									1	1	1	1
							is less than -100 fpm					0	0	0	0
								
	9	Auto Throttle		not available							0	0	0	0
							Available/not engaged					0	0	0	0
							engaged									1	1	1	1
								
	10	PROF				Transitions to Engaged					1	1	1	1
							Engaged									1	1	1	1
							Transitions to disengaged				1	1	1	1
							Disengaged								1	1	1	1
	*/
			//int count = 0;
			m_sitNo = -1;

			for(int l = 0; l < sitDef.length; l++)
			{
				//int count = -1;
				boolean is_active_sit_equals_def_b = false;
				boolean skip_to_next_sit_b = false;
				for(int m=0; m<sitDef[l].length;m++)
				{
					if(skip_to_next_sit_b)
					{
						break;
					}
					else{				
						for(int n=0; n<sitDef[l][m].length;n++)
						{
							//if((activeSitInput[m][n] == 1)&& (activeSitInput[m][n] == sitDef[l][m][n]))
							//{
							//	count++;
							//}
							if(activeSitInput[m][n] == 1 && activeSitInput[m][n] == sitDef[l][m][n])
							{
								is_active_sit_equals_def_b = true;
							}
							else
							{
								if(activeSitInput[m][n] == 1 && activeSitInput[m][n] != sitDef[l][m][n])
								{
									is_active_sit_equals_def_b = false;
									skip_to_next_sit_b = true;
									break;
								}
							}
						}
					}
				}//end sit input loop
				if(is_active_sit_equals_def_b)
				{
					m_sitNo = l;
					break;
				}
			}//end outer sit loop
			
			if(m_previous_sitNo != m_sitNo)
			{
				m_has_situation_changed = true;
			}
			else
			{
				m_has_situation_changed = false;
			}
			m_previous_sitNo = m_sitNo;
			return m_sitNo;
		}
		
		private void determine_action()
		{
			/* Action                ------------------  
			 * PITCH | T/O Clamp		0	0	0	0
				PITCH | Go Around 		0	0	0	0
				PITCH | CLB THRUST		0	0	1	0
				PITCH | IDLE			0	0	0	1
				THRUST | VS				0	0	0	0
				THRUST | ALT HOLD		1	0	0	0
				THRUST | ALT HOLD (CAP)	0	1	0	0
				THRUST | G/S			0	0	0	0
				RETARD | FLARE			0	0	0	0
				WINDSHEAR | WINDSHEAR	0	0	0	0
			 */
			
			int  target_altitude = m_mcpParameters.get_target_mcpaltitude();
			String target_alt_str = Integer.toString(target_altitude);
			target_alt_str = target_alt_str.concat("'");
			
			m_previous_fmaspdmode = m_fmaspdmode;
			m_previous_fmsaltmode = m_fmaaltmode;
					
			/*
			 * /0/Procedure- HOLD ALT XXX
		       /1/Procedure- CAPTURE ALT XXX
			   /2/Procedure- CLB MAINT XXX
			   /3/Procedure- CLB MAINT XXX-ROC
			   /4/Procedure- DES MAINT XXX
			   /5/Procedure- DES MAINT XXX-ROC
			   /6/Procedure- MAINT XXX
					};
			 */
			switch (m_sitNo)
			{
			case 0:
				m_fmaspdmode = "THRUST";
				m_fmaaltmode = "ELEV";
				m_active_procedure = "HOLD ALT";
				m_active_procedure_str = "HOLD ALT " + target_alt_str;
				m_situation = "HOLD ALT";
				m_fmaspdtgt = Integer.toString(m_mcpParameters.get_target_CAS());
				m_fmaalttgt = Integer.toString(target_altitude);
				break;

			case 1:
				m_fmaspdmode = "THRUST";
				m_fmaaltmode = "ELEV";
				m_active_procedure = "CAPTURE ALT";
				m_active_procedure_str = "CAPTURE ALT " + target_alt_str;
				m_situation = "CAPTURE ALT";
				m_fmaspdtgt = Integer.toString(m_mcpParameters.get_target_CAS());
				m_fmaalttgt = Integer.toString(target_altitude);
				break;

			case 2:
				m_fmaspdmode = "ELEV"; //speed on pitch 
				m_fmaaltmode = "THRUST";  //Climb with max thrust
				m_active_procedure = "CLB MAINT"; 
				m_active_procedure_str = "CLB MAINT " + target_alt_str;
				m_situation = "CLIMB REGION";
				m_fmaspdtgt = Integer.toString(m_mcpParameters.get_target_CAS());
				m_fmaalttgt = Integer.toString(target_altitude);
				break;

			case 3:
				m_fmaspdmode = "THRUST";
				m_fmaaltmode = "ELEV(ROC)";
				m_active_procedure = "CLB MAINT WITH ROC";
				m_active_procedure_str = "CLB MAINT " + target_alt_str + " - ROC";
				m_situation = "CLIMB REGION";
				m_fmaspdtgt = Integer.toString(m_mcpParameters.get_target_CAS());
				m_fmaalttgt = Integer.toString(target_altitude);
				break;
				
			case 4:
				m_fmaspdmode = "IDLE";
				m_fmaaltmode = "ELEV";
				m_active_procedure = "DES MAINT";
				m_active_procedure_str = "DES MAINT " + target_alt_str;
				m_situation = "DESCENT REGION";
				m_fmaspdtgt = Integer.toString(m_mcpParameters.get_target_CAS());
				m_fmaalttgt = Integer.toString(target_altitude);
				break;
				
			case 5:
				m_fmaspdmode = "THRUST";
				m_fmaaltmode = "ELEV(ROD)";
				m_active_procedure = "DES MAINT WITH ROD";
				m_active_procedure_str = "DES MAINT " + target_alt_str + " - ROD";
				m_situation = "DESCENT REGION";
				m_fmaspdtgt = Integer.toString(m_mcpParameters.get_target_CAS());
				m_fmaalttgt = Integer.toString(target_altitude);
				break;
				
			case 6:
				m_fmaspdmode = "THRUST";
				m_fmaaltmode = "ELEV";
				m_active_procedure = "MAINT";
				m_active_procedure_str = "MAINT " + target_alt_str;
				m_situation = "AT ALT";
				m_fmaspdtgt = Integer.toString(m_mcpParameters.get_target_CAS());
				m_fmaalttgt = Integer.toString(target_altitude);
				break;
				
			case 7:
				m_fmaspdmode = "THRUST";
				m_fmaaltmode = "ELEV";
				m_active_procedure = "CAPTURE GS FROM ABOVE";
				m_active_procedure_str = m_active_procedure;//"MAINT " + target_alt_str;
				m_situation = "CAPTURE ALT";
				m_fmaspdtgt = Integer.toString(m_mcpParameters.get_target_CAS());
				m_fmaalttgt = Integer.toString(target_altitude);
				break;
				
			case 8:
				m_fmaspdmode = "THRUST";
				m_fmaaltmode = "ELEV";
				m_active_procedure = "MAINT CAPTURED GS";
				m_active_procedure_str = m_active_procedure;//"MAINT " + target_alt_str;
				m_situation = "AT ALT";
				m_fmaspdtgt = Integer.toString(m_mcpParameters.get_target_CAS());
				m_fmaalttgt = Integer.toString(target_altitude);
				break;
				
			case 9:
				m_fmaspdmode = "THRUST";
				m_fmaaltmode = "ELEV";
				m_active_procedure = "CAPTURE GS FROM BELOW";
				m_active_procedure_str = m_active_procedure;//"MAINT " + target_alt_str;
				m_situation = "CAPTURE ALT";
				m_fmaspdtgt = Integer.toString(m_mcpParameters.get_target_CAS());
				m_fmaalttgt = Integer.toString(target_altitude);
				break;

			default:
				m_fmaspdmode = " ";
				m_fmaaltmode = " ";
				m_fmaspdtgt = Integer.toString(m_mcpParameters.get_target_CAS());
				m_fmaalttgt = Integer.toString(target_altitude);
				break;
			}
		}

		private void redrawAttitudeIndicator(Graphics g){
			//draw HSI
			int pfdx = 375;
			int pfdy = 125;	
			int hsix = 675;
			int hsiy = 230;
			
			if(m_is_ap_engaged_b)
			{
				m_pitch = (int)m_mcpParameters.getPitch();
				//m_attitude_indicator_horizon_image.paintIcon(this, g, hsix+160, hsiy-80 + (5*m_pitch));
				g.drawImage(m_attitude_indicator_horizon_image, hsix+160, hsiy-80 + (5*m_pitch), null);
				g.drawImage(m_attitude_indicator_markers_image, hsix+220, hsiy-28 + (5*m_pitch), null);
				g.drawImage(m_attitude_indicator_bg_image, hsix+120, hsiy-130, null);
			}
			else
			{
				if(!m_mcpParameters.get_is_scenario_specific())
				calibrate_pitch();
				//g.drawImage(m_attitude_indicator_horizon_image, hsix+160, hsiy-80 + (5*m_pitch), null);
				
				g.drawImage(m_attitude_indicator_horizon_image, hsix+160, hsiy-80 + (5*m_pitch), null);
				g.drawImage(m_attitude_indicator_markers_image, hsix+220, hsiy-28 + (5*m_pitch), null);
				g.drawImage(m_attitude_indicator_bg_image, hsix+120, hsiy-130, null);
			}
			g.setColor(Color.blue);
			g.setFont(fontHelB12);
			g.drawString("Pitch: " + Integer.toString(m_pitch), 1200, 650);
						
					/*g.setColor(Color.white);
					g.drawLine(pfdx+hsix-150, pfdy+hsiy-90, pfdx+hsix-150, pfdy+hsiy+90);//vert line lhs
					g.drawLine(pfdx+hsix+150, pfdy+hsiy-90, pfdx+hsix+150, pfdy+hsiy+90);//vert line rhs
					
					//in case of pitch up Blue arc area is more and yellow is less
					//For Blue arc's area to be more fill blue rect till further down and slide down yellow rect starting pt
					
					//in case of pitch down Blue arc's area is reduced and yellow arc's is increased

					g.drawArc(pfdx+hsix-150, pfdy+hsiy-160, 300, 160, -5, 185); //top arc
					g.setColor(Color.blue);
					g.fillArc(pfdx+hsix-150, pfdy+hsiy-160, 300, 160, -5, 185); //top arc
					
					g.fillRect(pfdx+hsix-150+1, pfdy+hsiy-90, 300-1, 90+pitch);

					g.setColor(Color.white);
					g.drawLine(pfdx+hsix-150+1, pfdy+hsiy-1+pitch, pfdx+hsix+150, pfdy+hsiy-1+pitch); //horiz line in middle

					g.setColor(Color.yellow);
					g.drawArc(pfdx+hsix-150, pfdy+hsiy, 300, 160, -185, 185); //bottom arc
					g.fillArc(pfdx+hsix-150, pfdy+hsiy, 300, 160, -185, 185); //bottom arc

					g.fillRect(pfdx+hsix-150+1, pfdy+hsiy+pitch, 300-1, 90);


					g.setColor(Color.black);
					g.drawArc(pfdx+hsix-150, pfdy+hsiy+2, 300, 160, -185, 185); //bottom border arc
					g.drawArc(pfdx+hsix-150-3, pfdy+hsiy+5, 302, 160, -185, 185); //bottom border arc
					g.drawArc(pfdx+hsix-150-1, pfdy+hsiy+4, 301, 160, -185, 185); //bottom border arc
					g.drawArc(pfdx+hsix-150-2, pfdy+hsiy+3, 302, 160, -185, 185); //bottom border arc*/

					

					//g.drawImage(m_attitude_indicator_bg_image, hsix+120, hsiy-140, null);
					//g.drawLine(pfdx+hsix-75, pfdy+hsiy+20, pfdx+hsix+75, pfdy+hsiy+20);
					//g.drawLine(pfdx+hsix-37, pfdy+hsiy+40, pfdx+hsix+37, pfdy+hsiy+40);
					//g.drawLine(pfdx+hsix-75, pfdy+hsiy+60, pfdx+hsix+75, pfdy+hsiy+60);
					
		}
		private void calibrate_pitch()
		{
			if(!m_yoke_released_b)
			{
				m_pitch = (int)(630 - yoke_bar_y)/2;
				if(m_pitch > 10)
				{
					m_pitch = 10;
				}
				else if(m_pitch < -10)
				{
					m_pitch = -10;
				}
			}
			else
			{
				if(m_pitch > 0)
				{
					m_pitch --;
				}
				else if (m_pitch < 0)
				{
					m_pitch ++;
				}
				else
				{
					m_pitch = 0;
				}
			}
			if(m_apm_obj != null)
			{
				m_apm_obj.set_pitch(m_pitch);
			}
		}
		private void redrawVerticalProfile(Graphics g)
		{
			g.setColor(Color.BLUE);
			g.drawRect(vp_panel_x, vp_panel_y, 345, 300);

			if(m_mcpParameters.getIsInFAF() && m_is_vnav_engaged_b)
			{
				if(m_glideslope_alt_targets != null)
				{
					for(int i = 0, j = 360; i <= m_glideslope_alt_targets.size() - 1 && j > vp_x;)
					{
						g.setColor(Color.BLUE);
						g.drawLine(j, vp_y+400, j, vp_y + 400 - (int)(m_glideslope_alt_targets.get(i)/8.5));
						i = i+2;
						j--;
					}
				}
			}

			g.setColor(Color.BLACK);
			g.drawLine(vp_x, vp_y + 400, vp_x, vp_y);
			g.drawLine(vp_x, vp_y + 400, vp_x+360, vp_y+400);

			//g.drawLine(vp_x, vp_y, vp_x + 10, vp_y);
			g.drawLine(vp_x, vp_y+100, vp_x + 10, vp_y+100);
			g.drawLine(vp_x, vp_y+200, vp_x + 10, vp_y+200);
			g.drawLine(vp_x, vp_y+300, vp_x + 10, vp_y+300);

			//g.drawString("4000", vp_x+20, vp_y+5);
			g.drawString(Integer.toString(3000), vp_x+15, vp_y+100+5);
			g.drawString(Integer.toString(2000), vp_x+15, vp_y+200+5);
			g.drawString(Integer.toString(1000), vp_x+15, vp_y+300+5);

			for(int i = 0; i <= 8; i++)
			{
				g.drawLine(vp_x + (43*i), vp_y + 95, vp_x + (43*i), vp_y + 405);
				g.drawString(Integer.toString(8 - i), vp_x + (43*i)-2, vp_y + 420); 
			}
			double current_dtd = m_mcpParameters.get_current_DTD();
			int current_alt = m_mcpParameters.get_current_altitude();
			int alt_diff = m_mcpParameters.get_alt_diff();
			if(current_dtd > 8)
			{
				g.drawImage(m_cruise_ac_icon, vp_x, (vp_y+400 -(int)(current_alt/10)), null);
			}
			else
			{
				g.drawImage(m_cruise_ac_icon, (int)(360 - (current_dtd*44)), (vp_y+400 -(int)(current_alt/10)), null);
				g.setColor(Color.BLUE);
				g.drawLine((int)(360 - (current_dtd*44)), (vp_y+400 -(int)(current_alt/10)), (int)(360 - (current_dtd*44)), 10);
			}			
		}

		private void redrawVSWindow(Graphics g){
			//VS Window
			int mcpx = 400;
			int mcpy = 20;
			int vscntx = 540;
			int windowsy = 28;
			
			g.setColor(Color.black);
			g.fillRect(mcpx+vscntx-40, mcpy+windowsy, 80, 25);
			g.setColor(Color.white);
			g.setFont(fontHelB18);
			if(m_mcpParameters.get_target_VS() == 0)
			{
				vsString = "+000";
			}

			else
			{
				vsString = Integer.toString(m_mcpParameters.get_target_VS());
			}
		}
		
		public String getAction()
		{
			return inten;
		}
		private void setValues (String event, int x, int y)
		{
			repaint();
		}
		private enum altitudeValues {
			ABOVE_1500_BARO, 
			BETWEEN_400_AND_1500_BARO, 
			BELOW_400_BARO, 
			IS_ON_GROUND
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			setValues("clicked", e.getX(), e.getY());
			if((e.getX() >= 417 && e.getX() <= 450) &&
					(e.getY() >= 630 && e.getY() <= 650))
			{
				if(!m_is_at_engaged_b)
				{
					m_is_at_engaged_b = true;
				}
				else
				{
					m_is_at_engaged_b = false;
				}
			}
			
			if((e.getX() > 467 && e.getX() < 511) &&
					(e.getY() > 630 && e.getY() < 630 + 20))
			{
				if(!m_is_vnav_engaged_b)
				{
					m_is_vnav_engaged_b= true;
				}
				else
				{
					m_is_vnav_engaged_b= false;
				}
			}
			 	
			if((e.getX() > 636 && e.getX() < 680) &&
					(e.getY() > 620 && e.getY() < 650))
			{
				if(!m_is_thrust_held_b)
				{
					m_is_thrust_held_b = true;
				}
				else
				{
					m_is_thrust_held_b = false;
				}
			}
			
			if((e.getX() > 636 && e.getX() < 680) &&
					(e.getY() > 670 && e.getY() < 690))
			{
				if(!m_is_thrust_retard_b)
				{
					m_is_thrust_retard_b = true;
				}
				else
				{
					m_is_thrust_retard_b = false;
				}
			}
			
			//a/p

			if((e.getX() > 417 && e.getX() < 450) &&
					(e.getY() > 670 && e.getY() < 670 + 20))
			{
				if(!m_is_ap_engaged_b)
				{
					m_is_ap_engaged_b = true;
				}
				else
				{
					m_is_ap_engaged_b = false;
				}
			}
			
			//toga
			if((e.getX() > 467 && e.getX() < 511) &&
					(e.getY() > 670 && e.getY() < 670 + 20))
			{
				if(!m_is_toga_active_b)
				{
					m_is_toga_active_b = true;
				}
				else
				{
					m_is_toga_active_b = false;
				}
			}
		}
				
		@Override
		public void mouseDragged(MouseEvent e) {
			setValues("clicked", e.getX(), e.getY());
			if((e.getX() > 400+155 && e.getX() < 400+185) && (e.getY() > 630 && e.getY() < 700))
			{
				if(!m_is_at_engaged_b && !m_is_thrust_held_b)
				{
					m_throttle_y_cord = e.getY();
				}
			}
			
			if((e.getX() > 1020 && e.getX() < 1070) && (e.getY() > 610 && e.getY() < 710))
			{
				if(!m_is_ap_engaged_b)
				{
					m_yoke_released_b = false;
					yoke_bar_y = e.getY()-30;
					m_pitch = 630 - yoke_bar_y;
				}
			}
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}
		@Override
		public void mousePressed(final MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			setValues("released", e.getX(), e.getY());
			if((e.getX() > 1000 && e.getX() < 1100) && (e.getY() > 400 && e.getY() < 800))
			{
				m_yoke_released_b = true;
			}
		}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
			}

			@Override
			public void update(Observable aircraftParameters, Object ap_obj) {
				repaint();
			}
	}

