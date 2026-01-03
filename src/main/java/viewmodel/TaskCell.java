	package viewmodel;
	
	import javafx.geometry.Pos;
	import javafx.scene.control.CheckBox;
	import javafx.scene.control.Label;
	import javafx.scene.control.ListCell;
	import javafx.scene.layout.HBox;
	import model.Task;
	
	public class TaskCell extends ListCell<Task>{
		
		private final Label label = new Label();
		private final CheckBox checkBox = new CheckBox();
		private final HBox cell = new HBox(10,checkBox,label);
		
		private Task boundTask;
		
		public TaskCell() {
	        checkBox.setFocusTraversable(false);
	    }
		
		@Override
		protected void updateItem(Task task, boolean empty) {
			super.updateItem(task, empty);
			
			
			if (boundTask != null) {
	            label.textProperty().unbind();
	            checkBox.selectedProperty().unbindBidirectional(boundTask.stateProperty());
	            boundTask = null;
	        }
			
			if(empty || task==null) {
				setGraphic(null);
				setText(null);
			}else {
				boundTask = task;
				
				label.textProperty().bind(task.nameProperty());
				checkBox.selectedProperty().bindBidirectional(task.stateProperty());
				
				cell.setAlignment(Pos.CENTER_LEFT);
				cell.setSpacing(10);
				cell.setPrefHeight(30);
				
				label.getStyleClass().setAll("label-task-cell");
				checkBox.getStyleClass().setAll("checkBox-task-cell");
				cell.getStyleClass().setAll("task-cell");
				
				setGraphic(cell);
			}
		}
	}
