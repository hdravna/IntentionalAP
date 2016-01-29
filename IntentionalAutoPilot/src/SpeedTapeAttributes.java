

import java.awt.Image;

public class SpeedTapeAttributes {

	private int m_y_coord_position;
	private int m_speed_value;
	private Image m_optional_image;
	//private boolean m_barber_pole_visible;
	
	public SpeedTapeAttributes()
	{
		m_y_coord_position = 0;
		m_speed_value = 0;
		m_optional_image = null;
	}
	public void set_y_position(int y_coord)
	{
		m_y_coord_position = y_coord;
	}
	
	public void set_speed_value(int speed_value)
	{
		m_speed_value = speed_value;
	}
	
	public void set_image(Image image)
	{
		m_optional_image = image;
	}
	
	public void increment_y_position()
	{
		m_y_coord_position++;
	}
	
	public void decrement_y_position()
	{
		m_y_coord_position--;
	}
	
	public int get_y_position()
	{
		return m_y_coord_position;
	}
	
	public int get_speed_value()
	{
		return m_speed_value;
	}
	
	public Image get_image()
	{
		return m_optional_image;
	}
	
/*	public void set_barber_pole_visible()
	{
		m_barber_pole_visible = true;
	}
	
	public boolean get_barber_pole_visible()
	{
		return m_barber_pole_visible;
	}*/
}
