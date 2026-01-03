# README – To-Do List JavaFX

## 1. Objectif du projet

Application To-Do List en JavaFX avec :

- Ajout, suppression et validation des tâches.
- Filtrage des tâches (`All`, `To-do`, `Completed`).
- Barre de titre personnalisée avec boutons `minimize`, `maximize`, `close`.
- Drag & drop de la fenêtre.
- Interface stylisée avec CSS et cellules personnalisées (`ListCell`) incluant CheckBox.

Cette application sert à expérimenter JavaFX, la gestion des événements, les bindings, et le CSS appliqué aux composants.

---

## 2. Difficultés techniques et solutions

### 2.1 Barre de titre personnalisée

**Problèmes rencontrés :**
- Remplacer la barre de titre native de Windows.
- Gérer les boutons `minimize`, `maximize`, `close`.

**Solution :**
- `StageStyle.TRANSPARENT`.
- `Region` invisible pour drag.
- Gestion des boutons via `onMouseClicked` ou `onAction`.

```java
@FXML
public void onCloseApp() { ... }
@FXML
public void onMinimizeWindow() { ... }
@FXML
public void onMaximizeWindow() { ... }
```

**Drag de la fenêtre :**
```java
dragZone.setOnMousePressed(...);
dragZone.setOnMouseDragged(...);
```

### 2.2 ListView et FilteredList

**Problèmes rencontrés :**
- Filtrage dynamique avec `FilteredList`.
- Gestion de la sélection après modification d'état.

**Solution :**
- `ObservableList<Task>` reliée à `FilteredList<Task>`.
- Application de filtres via `setPredicate()`.

```java
private void applyFilter() {
    if (checkFilterCompleted.isSelected()) {
        filteredTasks.setPredicate(Task::getState);
    } else if (checkFilterToDo.isSelected()) {
        filteredTasks.setPredicate(t -> !t.getState());
    } else {
        filteredTasks.setPredicate(t -> true);
    }
}
```

**Décision :**
- La touche ENTER pour toggler les tâches filtrées a été abandonnée.

### 2.3 Cellules personnalisées (`ListCell`) et CheckBox

**Problèmes rencontrés :**
- CheckBox perdant le binding avec `updateItem`.
- Focus des CheckBox bloquant KeyEvents.
- Gestion de l’état via clic uniquement.

**Solution :**
- Débinding complet avant re-binder.
- `checkBox.setFocusTraversable(false)`.

```java
@Override
protected void updateItem(Task task, boolean empty) {
    super.updateItem(task, empty);
    if (boundTask != null) {
        label.textProperty().unbind();
        checkBox.selectedProperty().unbindBidirectional(boundTask.stateProperty());
        boundTask = null;
    }
    if (empty || task == null) {
        setGraphic(null);
    } else {
        boundTask = task;
        label.textProperty().bind(task.nameProperty());
        checkBox.selectedProperty().bindBidirectional(task.stateProperty());
        setGraphic(cell);
    }
}
```

**Squelette générique de cellule personnalisée :**
```java
public class CustomCell<T> extends ListCell<T> {
    private final HBox container = new HBox();
    private final Label label = new Label();
    private final Node customNode;
    private T boundItem;

    public CustomCell(Node customNode) {
        this.customNode = customNode;
        container.getChildren().addAll(customNode, label);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setSpacing(10);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (boundItem != null) boundItem = null;
        if (empty || item == null) {
            setGraphic(null);
        } else {
            boundItem = item;
            label.setText(item.toString());
            setGraphic(container);
        }
    }
}
```

### 2.4 CSS JavaFX

**Problèmes rencontrés :**
- ListView et ListCell difficiles à styliser.
- CheckBox complexe.
- Hover et sélection non uniformes.

**Solutions :**
- Sélecteurs CSS précis.
- Coins arrondis, padding, couleurs.
- Hover avec translation pour effet visuel.

```css
.list-view { -fx-background-color: #FFF7E6; -fx-background-radius: 8; -fx-padding: 5; }
.list-cell { -fx-background-color: #FFF7E6; -fx-background-radius: 8; -fx-padding: 7; }
.list-cell:selected { -fx-background-color: #66999B; -fx-text-fill: white; }
.checkBox-task-cell .box { -fx-border-color: #66999B; -fx-background-color: white; -fx-min-width: 32px; -fx-min-height: 32px; }
```

### 2.5 Autres problèmes techniques

- **Suppression de tâches :** gérer la sélection `null`.  
- **Bindings Task :** `StringProperty` et `BooleanProperty`.  
- **Focus et KeyEvents :** CheckBox focusables bloquaient raccourcis clavier.  

---

## 3. Conclusion

- L’application est **fonctionnelle et stable**.  
- Certaines fonctionnalités (toggle ENTER sur filtrage) ont été abandonnées pour stabilité.  
- Base solide pour : JavaFX, ListCell personnalisées, bindings, FilteredList, CSS, barre de titre personnalisée.  

---

