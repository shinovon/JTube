package ui;

import models.ILoader;
import models.AbstractModel;

public abstract class ModelScreen extends AbstractListScreen implements ILoader {

	public ModelScreen(String title) {
		super(title, null);
	}

	public abstract AbstractModel getModel();

	public abstract void setFormContainer(UIScreen s);
}
	