package ui;

import models.ILoader;
import models.AbstractModel;

public interface IModelScreen extends ILoader {

	public abstract AbstractModel getModel();

	public abstract void setContainerScreen(UIScreen s);
}
	