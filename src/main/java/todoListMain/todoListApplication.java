package todoListMain;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

public class todoListApplication extends Application{
	
	

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskView.fxml"));
		
		Parent mainView = loader.load();
		
		Scene scene = new Scene(mainView);
		
		scene.setFill(Color.TRANSPARENT);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(scene);
		stage.setTitle("to-do list!");
		stage.show();
	}

	public static void main(String[] args) {
		launch(todoListApplication.class);
	}

}
