package ui;

import javax.microedition.lcdui.Form;

import models.ILoader;
import models.AbstractModel;

public abstract class ModelForm extends Form implements ILoader {

	public ModelForm(String title) {
		super(title);
	}

	public abstract AbstractModel getModel();
	
}
