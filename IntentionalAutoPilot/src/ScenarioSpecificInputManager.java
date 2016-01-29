/*
Author: Anvardh Nanduri
Organisation: CATSR, GMU
Supervisor: Lance Sherry
Date: September 2014
Code for Intentional Auto Pilot Prototype. No part of this software code can be reused in any form without prior approval of the author.
email: anvardh@gmail.com
*/

import java.util.ArrayList;
import java.awt.Image;
import java.io.BufferedInputStream; 
import java.io.InputStream; 
import java.util.Iterator;
import javax.swing.ImageIcon;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class ScenarioSpecificInputManager {

	private MCPParameters m_aircraftParameters;

	private ArrayList<Integer> m_cas_targets_vector;
	private ArrayList<Integer> m_altitude_targets_vector;
	private ArrayList<Integer> m_vs_targets_vector;
	private ArrayList<Integer> m_at_engagement_status_vector;
	private ArrayList<Integer> m_ap_engagement_status_vector;
	private ArrayList<Integer> m_at_hold_status_vector;
	private ArrayList<Integer> m_at_retard_status_vector;
	private ArrayList<Integer> m_vnav_status_vector;
	private ArrayList<Integer> m_throttle_values_vector;
	private int initial_altitude;
	private int initial_vs;
	private double initial_dtd;
	private int initial_speed;
	
	private Image m_graph_image;
	private MyPanel m_myPanelObj;

	private ArrayList<Double> m_dtd_vector;


	private int m_scenarioID;
	public ScenarioSpecificInputManager(int scenarioID, MyPanel myPanelObj)
	{
		m_myPanelObj = myPanelObj;
		m_scenarioID = scenarioID;
		initializeValueVectors();
		m_aircraftParameters = MCPParameters.getInstance();
		sendPreSpecifiedInputs();		
	}

	private void sendPreSpecifiedInputs()
	{
		m_aircraftParameters.set_cas_targets_vector(m_cas_targets_vector);
		m_aircraftParameters.set_altitude_targets_vector(m_altitude_targets_vector);
		m_aircraftParameters.set_vs_targets_vector(m_vs_targets_vector);
		m_aircraftParameters.set_ap_engagement_status_vector(m_ap_engagement_status_vector);
		m_aircraftParameters.set_vs_targets_vector(m_vs_targets_vector);
		m_aircraftParameters.set_at_engagement_status_vector(m_at_engagement_status_vector);
		m_aircraftParameters.set_at_hold_status_vector(m_at_hold_status_vector);
		m_aircraftParameters.set_at_retard_status_vector(m_at_retard_status_vector);
		m_aircraftParameters.set_throttle_values_vector(m_throttle_values_vector);
		m_aircraftParameters.set_vnav_status_vector(m_vnav_status_vector);	
		
		m_aircraftParameters.setInitialAlt(initial_altitude);
		m_aircraftParameters.setInitialSpeed(initial_speed);
		m_aircraftParameters.setInitialDTD(initial_dtd);
		m_aircraftParameters.setInitialVS(initial_vs);
		m_aircraftParameters.set_faf_distance_from_rw(initial_dtd);
	}
	
	public void setGraphToDisplay()
	{
		m_myPanelObj.setGraphToUse(m_graph_image);
	}
	
	public void initializeValueVectors()
	{
		m_cas_targets_vector = new ArrayList<Integer>();
		m_altitude_targets_vector = new ArrayList<Integer>();
		m_at_engagement_status_vector = new ArrayList<Integer>();
		m_ap_engagement_status_vector = new ArrayList<Integer>();
		m_at_hold_status_vector = new ArrayList<Integer>();
		m_throttle_values_vector = new ArrayList<Integer>();
		m_dtd_vector = new ArrayList<Double>();
		m_vs_targets_vector = new ArrayList<Integer>();
		m_vnav_status_vector = new ArrayList<Integer>();
		m_at_retard_status_vector = new ArrayList<Integer>();
		
		InputStream input = null;
		InputStream is = null;
		switch(m_scenarioID)
		{
		case 1:
			try { 
				is = IAPFrame.class.getResourceAsStream("/resources/asiana.xls");
				input = new BufferedInputStream(is);
				m_graph_image = (new ImageIcon(MyPanel.class.getResource("/resources/graph.png"))).getImage();
				//Set the graph to use in MyPanel 
				setGraphToDisplay();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			break;
		case 2:
			try { 
				is = IAPFrame.class.getResourceAsStream("/resources/turkish.xls");
				input = new BufferedInputStream(is);
				m_graph_image = (new ImageIcon(MyPanel.class.getResource("/resources/turkish_1951_graph.png"))).getImage();
				//Set the graph to use in MyPanel 
				setGraphToDisplay();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			break;
		}
		
		
		try { 

			POIFSFileSystem fs = new POIFSFileSystem(input); 
			HSSFWorkbook wb = new HSSFWorkbook(fs); 
			HSSFSheet sheet = wb.getSheetAt(0);

			@SuppressWarnings("rawtypes")
			Iterator rows = sheet.rowIterator();
			//DTD	CAS_target	Alt_target	VS_target	AT_status	AT_hold_status	AT_retard_status AP_status	VNAV_status Thrust_values
			rows.next();
			
			HSSFRow row = (HSSFRow) rows.next();
			@SuppressWarnings("rawtypes")
			Iterator cells = row.cellIterator();
			
			HSSFCell cell;
			cell = (HSSFCell) cells.next();
			
			//Initial Altitude	
			if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
			{
				initial_altitude = (int) cell.getNumericCellValue();
			}
			
			cell =(HSSFCell)cells.next();
			//Initial speed	
			if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
			{
				initial_speed = (int) cell.getNumericCellValue();
			}
			
			cell =(HSSFCell)cells.next();
			//Initial DTD
			if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
			{
				initial_dtd = (double) cell.getNumericCellValue();
			}			
			
			cell =(HSSFCell)cells.next();
			//Initial VS
			if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
			{
				initial_vs = (int) cell.getNumericCellValue();
			}
			
			rows.next();
			while(rows.hasNext()) 
			{ 
				row = (HSSFRow) rows.next(); 
				cells = row.cellIterator(); 
				
				cell = (HSSFCell) cells.next(); 

				//DTD	
				if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
				{
					m_dtd_vector.add((double) cell.getNumericCellValue());
				}

				//CAS_target
				cell =(HSSFCell)cells.next();
				if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
				{
					m_cas_targets_vector.add((int) cell.getNumericCellValue());
				}

				//Alt_target
				cell =(HSSFCell)cells.next();
				if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
				{
					m_altitude_targets_vector.add((int) cell.getNumericCellValue());
				}

				//VS_target	
				cell =(HSSFCell)cells.next();
				if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
				{
					m_vs_targets_vector.add((int) cell.getNumericCellValue());
				}	

				//AT_status	
				cell =(HSSFCell)cells.next();
				if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
				{
					m_at_engagement_status_vector.add((int) cell.getNumericCellValue());
				}	
				//AT_hold_status	
				cell =(HSSFCell)cells.next();
				if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
				{
					m_at_hold_status_vector.add((int) cell.getNumericCellValue());
				}		
				
				//AT_retard_status
				cell =(HSSFCell)cells.next();
				if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
				{
					m_at_retard_status_vector.add((int) cell.getNumericCellValue());
				}
				
				//AP_status	
				cell =(HSSFCell)cells.next();
				if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
				{
					m_ap_engagement_status_vector.add((int) cell.getNumericCellValue());
				}
				
				//VNAV_status	
				cell =(HSSFCell)cells.next();
				if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
				{
					m_vnav_status_vector.add((int) cell.getNumericCellValue());
				}
				
				//Thrust_values
				cell =(HSSFCell)cells.next();
				if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
				{
					m_throttle_values_vector.add((int) cell.getNumericCellValue());
				}	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public ArrayList<Integer> get_vnav_status_vector() {
		return m_vnav_status_vector;
	}

	public void set_vnav_status_vector(ArrayList<Integer> m_vnav_status_vector) {
		this.m_vnav_status_vector = m_vnav_status_vector;
	}

	public ArrayList<Integer> get_at_retard_status_vector() {
		return m_at_retard_status_vector;
	}

	public void set_at_retard_status_vector(ArrayList<Integer> m_at_retard_status_vector) {
		this.m_at_retard_status_vector = m_at_retard_status_vector;
	}
}

