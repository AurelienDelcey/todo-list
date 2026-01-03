package viewmodel;
	
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import model.Task;
	
	public class TaskCell extends ListCell<Task>{
		
		private final Text text = new Text();
		private final CheckBox checkBox = new CheckBox();
		private final HBox cell = new HBox(10,checkBox,text);
		
		private Task boundTask;
		
		public TaskCell() {
	        checkBox.setFocusTraversable(false);
	    }
		
		@Override
		protected void updateItem(Task task, boolean empty) {
			super.updateItem(task, empty);
			
			
			if (boundTask != null) {
	            text.textProperty().unbind();
	            checkBox.selectedProperty().unbindBidirectional(boundTask.stateProperty());
	            boundTask = null;
	        }
			
			if(empty || task==null) {
				setGraphic(null);
				setText(null);
			}else {
				boundTask = task;
				
				task.stateProperty().addListener((obs, oldVal, newVal) -> {
					text.setStrikethrough(newVal);
				});
				
				checkBox.selectedProperty().bindBidirectional(task.stateProperty());
				text.textProperty().bind(task.nameProperty());
				text.setStrikethrough(task.getState());
				
				
				cell.setAlignment(Pos.CENTER_LEFT);
				cell.setSpacing(10);
				cell.setPrefHeight(30);
				
				text.getStyleClass().setAll("label-task-cell");
				checkBox.getStyleClass().setAll("checkBox-task-cell");
				cell.getStyleClass().setAll("task-cell");
				
				
				setGraphic(cell);
			}
		}
	}
