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
			
			
			if (boundTask != null) {                   //si la cellule a été recyclée et contient une reference on debind tout et on efface la reference
	            text.textProperty().unbind();
	            checkBox.selectedProperty().unbindBidirectional(boundTask.stateProperty());
	            boundTask = null;
	        }
			
			if(empty || task==null) {
				setGraphic(null);
				setText(null);
			}else {
				boundTask = task;                      //on garde une reference pour debind tout en cas de recyclage
				
				task.stateProperty().addListener((obs, oldVal, newVal) -> {
					text.setStrikethrough(newVal);
				});
				text.setStrikethrough(task.getState()); //réapliquer le strikeThrough a chaque recyclage pour eviter les bugs visuel
				
				checkBox.selectedProperty().bindBidirectional(task.stateProperty());
				text.textProperty().bind(task.nameProperty());
				
				
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
