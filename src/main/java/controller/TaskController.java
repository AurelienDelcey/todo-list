package controller;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

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
import persistence.Saver;
import viewmodel.TaskCell;

public class TaskController {

    // ----------------------------
    // LISTES ET VARIABLES DE FENÊTRE
    // ----------------------------
    private ObservableList<Task> tasks;          // liste observable contenant toutes les tâches
    private FilteredList<Task> filteredTasks;    // liste filtrée pour l'affichage selon filtre
    private Saver saver;                          // gestionnaire de sauvegarde
    private double xOffset;                       // position x pour le drag
    private double yOffset;                       // position y pour le drag

    // ----------------------------
    // ELEMENTS FXML
    // ----------------------------
    @FXML
    private TextField taskField;                 // champ pour saisir le nom d'une tâche

    @FXML
    private ListView<Task> taskList;             // affichage des tâches

    @FXML
    private Region dragZone;                     // zone de drag pour la fenêtre

    @FXML
    private CheckBox checkFilterAll;             // filtre toutes les tâches
    @FXML
    private CheckBox checkFilterToDo;            // filtre les tâches "à faire"
    @FXML
    private CheckBox checkFilterCompleted;       // filtre les tâches terminées

    // ----------------------------
    // GESTION DES TÂCHES
    // ----------------------------

    @FXML
    public void onAddTask() {
        String name = taskField.getText().trim();
        this.taskField.clear();

        if (name.isEmpty()) return;

        // Eviter les doublons
        for (Task i : tasks) {
            if (i.getName().equals(name)) {
                return;
            }
        }

        // Création et ajout de la tâche
        Task task = new Task();
        task.setName(name);
        this.tasks.add(task);
    }

    @FXML
    public void onRemoveTask() {
        Task selected = taskList.getSelectionModel().getSelectedItem();

        if (selected != null) {
            tasks.remove(selected);
        } else if (!tasks.isEmpty()) {
            tasks.remove(0);
        }
    }

    @FXML
    public void onCheckTask() {
        Task selected = taskList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // bascule de l'état (fait / à faire)
            selected.setState(!selected.getState());
        }
    }

    @FXML
    public void onSaveTasks() {
        try {
            saver.saveAll(this.tasks.toArray(i -> new Task[this.tasks.size()]));
        } catch (StreamWriteException e) {
            System.out.println("ERROR: stream write exception: " + e.getMessage());
            e.printStackTrace();
        } catch (DatabindException e) {
            System.out.println("ERROR: data bind exception: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR: IO exception: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    // ----------------------------
    // GESTION DE LA FENÊTRE
    // ----------------------------

    @FXML
    public void onCloseApp() {
        onSaveTasks();
        Stage stage = (Stage) this.taskField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onMinimizeWindow() {
        Stage stage = (Stage) this.taskField.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void onMaximizeWindow() {
        Stage stage = (Stage) this.taskField.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    // ----------------------------
    // FILTRAGE DES TÂCHES
    // ----------------------------

    @FXML
    public void onSetFilter(ActionEvent event) {
        CheckBox source = (CheckBox) event.getSource();

        // un seul filtre actif à la fois
        checkFilterAll.setSelected(source == checkFilterAll);
        checkFilterToDo.setSelected(source == checkFilterToDo);
        checkFilterCompleted.setSelected(source == checkFilterCompleted);

        applyFilter();
    }

    private void applyFilter() {
        if (checkFilterCompleted.isSelected()) {
            filteredTasks.setPredicate(Task::getState); // afficher seulement les tâches faites
        } else if (checkFilterToDo.isSelected()) {
            filteredTasks.setPredicate(t -> !t.getState()); // afficher seulement les tâches à faire
        } else {
            filteredTasks.setPredicate(t -> true); // aucun filtre
        }
    }

    // ----------------------------
    // INITIALISATION
    // ----------------------------

    @FXML
    public void initialize() {

        // Création du Saver avec un chemin générique relatif
        this.saver = new Saver(new File("save/save1.json"));

        // ----------------------------
        // INITIALISATION DES LISTES
        // ----------------------------
        this.tasks = FXCollections.observableArrayList(
                task -> new javafx.beans.Observable[] { task.stateProperty() }
        );
        this.filteredTasks = new FilteredList<>(this.tasks);
        this.taskList.setItems(filteredTasks);

        // Cellules personnalisées
        this.taskList.setCellFactory(i -> new TaskCell());

        // Ajouter une tâche avec ENTER dans le champ texte
        this.taskField.setOnAction(i -> onAddTask());

        // ----------------------------
        // DRAG DE LA FENÊTRE
        // ----------------------------
        this.dragZone.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        this.dragZone.setOnMouseDragged(event -> {
            Stage stage = (Stage) this.dragZone.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // ----------------------------
        // GESTION DES TOUCHES
        // ----------------------------
        this.taskList.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            switch (e.getCode()) {
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

        // ----------------------------
        // CHARGEMENT DES TÂCHES EXISTANTES
        // ----------------------------
        if (saver.getFile().exists()) {
            try {
                this.tasks.setAll(this.saver.load());
            } catch (StreamReadException e) {
                System.out.println("ERROR: stream read exception: " + e.getMessage());
                e.printStackTrace();
            } catch (DatabindException e) {
                System.out.println("ERROR: data bind exception: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("ERROR: IO exception: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }
}
