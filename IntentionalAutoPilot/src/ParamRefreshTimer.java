


import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Timer;

//Should be a singleton
public class ParamRefreshTimer implements Observer{

	Observable m_observable;
	private Timer m_timer;
	private static boolean m_stop_timer_after_duration_b;
	static ParamRefreshTimer m_unique_prt_obj; 

	private ParamRefreshTimer(ActionListener action_listener){	
		this.m_observable = (Observable)action_listener;
		m_timer = new Timer(1000, action_listener);
	}

	public static ParamRefreshTimer getInstance(ActionListener action_listener_p){
		if(m_unique_prt_obj == null){
			m_unique_prt_obj = new ParamRefreshTimer(action_listener_p);
		}
		return m_unique_prt_obj;
	}

	public void setIsLimitedTime(boolean is_limited_time_b){
		m_stop_timer_after_duration_b = is_limited_time_b;
		if(m_stop_timer_after_duration_b)
		{
			m_observable.addObserver(this);
		}
		else
		{
			m_observable.deleteObserver(this);
		}
	}

	public void startTimer(){

		if(m_stop_timer_after_duration_b)
		{
			Aircraft_Performance_Model.counter = 15;
		}
		m_timer.start();
	}

	public static boolean getIsLimitedTime(){
		return m_stop_timer_after_duration_b;
	}
	public void stopTimer(){
		if(m_stop_timer_after_duration_b)
		{
			m_timer.stop();
		}
	}
	
	public void stop_timer_at_once(){
		if(m_timer.isRunning()){
			m_timer.stop();
		}
			
	}
	
	public boolean is_main_timer_running()
	{
		return m_timer.isRunning();
	}
	public void restartTimer(){
		Aircraft_Performance_Model.counter = 150;
		m_timer.restart();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		stopTimer();
	}

};

