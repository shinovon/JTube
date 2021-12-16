package models;

import javax.microedition.lcdui.Item;

import ui.Commands;
import ui.ModelForm;

public abstract class AbstractModel implements Commands {
	
	public abstract Item makeItemForList();

	public abstract void setFromSearch();
	
	public abstract boolean isFromSearch();
	
	public abstract boolean isExtended();
	
	public abstract void dispose();
	
	public abstract void disposeExtendedVars();
	
	public abstract ModelForm makeForm();
	
}
