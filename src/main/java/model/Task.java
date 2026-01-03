package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
	private StringProperty name ;
	private BooleanProperty state;
	
	public Task(String name) {
		this.name = new SimpleStringProperty(name);
		this.state = new SimpleBooleanProperty(false);
	}
	
	public BooleanProperty stateProperty() {
		return this.state;
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name.set(name);
	}
	
	public void setState(boolean state) {
		this.state.set(state);
	}
	
	public boolean getState() {
		return this.state.get();
	}
	
	public String getName() {
		return this.name.get();
	}
	
	public String toString() {
		return this.getName();
	}
}
