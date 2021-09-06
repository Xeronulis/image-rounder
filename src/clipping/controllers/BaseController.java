package clipping.controllers;

public abstract class BaseController<T> {

	protected T parentController;

	public T getParentController() {
		return parentController;
	}

	public void setParentController(T parentController) {
		this.parentController = parentController;
	}
	
	
	
}
