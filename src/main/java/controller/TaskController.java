package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.Task;
import viewmodel.TaskCell;

public class TaskController {
	
	private FilteredList<Task> filteredTasks;
	private ObservableList<Task> tasks;
	private double xOffset;
	private double yOffset;
	
	@FXML
	private TextField taskField;

	@FXML
	private ListView<Task> taskList;
	
	@FXML
	private Region dragZone;
	
	@FXML
	private CheckBox checkFilterAll;
	
	@FXML
	private CheckBox checkFilterToDo;
	
	@FXML
	private CheckBox checkFilterCompleted;
	
	@FXML
	public void onAddTask() {
		String name = taskField.getText().trim();
		this.taskField.clear();
		if(name.isEmpty()) {
			return;
		}
		for(Task i:tasks) {
			if (i.getName().equals(name)) {
			    return;
			}
		}
		Task task = new Task(name);
		this.tasks.add(task);
	}
	
	@FXML
	public void onCloseApp() {
		Stage stage = (Stage)this.taskField.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	public void onMinimizeWindow() {
		Stage stage = (Stage)this.taskField.getScene().getWindow();
		stage.setIconified(true);
	}
	
	@FXML
	public void onMaximizeWindow() {
		Stage stage = (Stage)this.taskField.getScene().getWindow();
		stage.setMaximized(!stage.isMaximized());
	}
	
	@FXML
	public void onRemoveTask() {
		Task selected = taskList.getSelectionModel().getSelectedItem();
	    if (selected != null) {
	        tasks.remove(selected);
		}else if(!tasks.isEmpty()){
			tasks.remove(0);
		}
	}
	
	@FXML
	public void onCheckTask() {
		Task selected = taskList.getSelectionModel().getSelectedItem();
		if(selected != null) {
			//System.out.println("before setting: " + selected.getState());
			selected.setState(selected.getState()==false?true:false);
			//System.out.println("after setting: " + selected.getState());
		}
	}
	
	@FXML
	public void onSetFilter(ActionEvent event) {
		CheckBox source = (CheckBox) event.getSource();

	    checkFilterAll.setSelected(source == checkFilterAll);
	    checkFilterToDo.setSelected(source == checkFilterToDo);
	    checkFilterCompleted.setSelected(source == checkFilterCompleted);

	    applyFilter();
	}
	
	@FXML
	public void initialize() {
		
		this.tasks = FXCollections.observableArrayList(
				task -> new javafx.beans.Observable[] { task.stateProperty() }
				);
		this.filteredTasks = new FilteredList<>(this.tasks);
		this.taskList.setItems(filteredTasks);
		this.taskList.setCellFactory(i-> new TaskCell());
		this.taskField.setOnAction(i->onAddTask());
		
		
		this.dragZone.setOnMousePressed(event -> {
		    xOffset = event.getSceneX();
		    yOffset = event.getSceneY();
		});
		
		
		this.dragZone.setOnMouseDragged(event -> {
		    Stage stage = (Stage) this.dragZone.getScene().getWindow();
		    stage.setX(event.getScreenX() - xOffset);
		    stage.setY(event.getScreenY() - yOffset);
		});
		
		
		this.taskList.addEventFilter(KeyEvent.KEY_RELEASED,e->{
			switch(e.getCode()) {
			case DELETE:
				onRemoveTask();
				e.consume();
				break;
			/*case ENTER:
				Task selected = taskList.getSelectionModel().getSelectedItem();
			    if (selected != null) {
			        e.consume();
			        Platform.runLater(() -> {
			            selected.setState(!selected.getState());
			        });
			    }
			    break;*/
			default:
				break;
			}
		});
	}
	
	private void applyFilter() {
	    if (checkFilterCompleted.isSelected()) {
	        filteredTasks.setPredicate(Task::getState);
	    } else if (checkFilterToDo.isSelected()) {
	        filteredTasks.setPredicate(t -> !t.getState());
	    } else {
	        filteredTasks.setPredicate(t -> true);
	    }
	}
}
