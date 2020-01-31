import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Controller {
    @FXML
    private AnchorPane mainForm;
    @FXML
    public TableView<Task> tableTasks;
    @FXML
    private ToolBar btsBar;
    @FXML
    private Button btClear;
    @FXML
    private Button btSave;
    @FXML
    private Button btOpenFile;
    @FXML
    private Button btSearch;
    @FXML
    private Button btRemove;
    @FXML
    private Button btAdd;
    @FXML
    private TableView<SubTask> tableSubTasks;
    @FXML
    private ToolBar btsSubBar;
    @FXML
    private Button btSubRemove;
    @FXML
    private Button btSubAdd;
    @FXML
    private Pane panelSearch;
    @FXML
    private RadioButton rbEnded;
    @FXML
    private RadioButton rbNotEnded;
    @FXML
    private RadioButton rbAll;
    @FXML
    private TextField tbTags;
    @FXML
    private TextField tbDescription;
    @FXML
    private Button btSearchOK;
    @FXML
    private CheckBox cbFireTasks;
    @FXML
    private Button btDown;
    @FXML
    private Button btUP;
    @FXML
    private ComboBox<String> cbSpecialSearch;

    TasksDAO Data = UI.Data;

    public Boolean isUniqueSubTask(String newHeader, ArrayList<SubTask> subTasks) {
        return subTasks.stream().noneMatch(x -> x.header.equals(newHeader));  // ФП
    }

    @FXML
    void initialize() {
        tableTasks.setEditable(true);

        TableColumn<Task, String> columnTask = new TableColumn<>("Задача");
        columnTask.setCellValueFactory(new PropertyValueFactory<>("header"));
        columnTask.setCellFactory(TextFieldTableCell.forTableColumn());
        columnTask.setMinWidth(200);

        columnTask.setOnEditCommit((TableColumn.CellEditEvent<Task, String> event) -> {
            TablePosition<Task, String> pos = event.getTablePosition();
            String newTask = event.getNewValue().trim();
            int row = pos.getRow();
            Task task = event.getTableView().getItems().get(row);
            task.setHeader(newTask.isEmpty() ? event.getOldValue() : newTask); // неФП
            tableTasks.refresh();
        });

        TableColumn<Task, String> columnDescription = new TableColumn<>("Описание");
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        columnDescription.setMinWidth(400);

        columnDescription.setOnEditCommit((TableColumn.CellEditEvent<Task, String> event) -> {
            TablePosition<Task, String> pos = event.getTablePosition();
            String newDescription = event.getNewValue().trim();
            int row = pos.getRow();
            Task task = event.getTableView().getItems().get(row);
            task.setDescription(newDescription);
        });

        TableColumn<Task, String> columnTags = new TableColumn<>("Теги");
        columnTags.setCellValueFactory(new PropertyValueFactory<>("listOfTags"));
        columnTags.setCellFactory(TextFieldTableCell.forTableColumn());
        columnTags.setMinWidth(200);

        columnTags.setOnEditCommit((TableColumn.CellEditEvent<Task, String> event) -> {
            TablePosition<Task, String> pos = event.getTablePosition();
            String newTagList = event.getNewValue().trim();
            int row = pos.getRow();
            Task task = event.getTableView().getItems().get(row);
            task.setListOfTags(newTagList);
            tableTasks.refresh();
        });

        TableColumn<Task, DatePicker> columnDate = new TableColumn<>("Срок выполнения");
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnDate.setEditable(true);
        columnDate.setPrefWidth(75);
        columnDate.setMinWidth(200);
        columnDate.setComparator((o1, o2) -> {
            if (o1.getValue().isBefore(o2.getValue())) return -1;  //  неФП
            if (o1.getValue().equals(o2.getValue())) return 0;
            return 1;
        });
        tableTasks.getSortOrder().add(columnDate);

        TableColumn<Task, Boolean> columnHaveDate = new TableColumn<>("Deadline");
        columnHaveDate.setCellValueFactory(param -> {
            Task task = param.getValue();

            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(task.getHaveDate());

            booleanProp.addListener((observable, oldValue, newValue) -> {
                task.setHaveDate(newValue);
                tableTasks.setItems(FXCollections.observableArrayList(Data.getAll()));
                tableTasks.refresh();
            });
            return booleanProp;
        });

        columnHaveDate.setCellFactory(p -> {
            CheckBoxTableCell<Task, Boolean> cell = new CheckBoxTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        columnHaveDate.setMinWidth(100);

        TableColumn<Task, Boolean> columnDone = new TableColumn<>("Задача выполнена?");
        columnDone.setCellValueFactory(param -> {
            Task task = param.getValue();

            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(task.getDone());

            booleanProp.addListener((observable, oldValue, newValue) -> {
                task.setDone(newValue);
                tableSubTasks.refresh();
            });
            return booleanProp;
        });

        columnDone.setCellFactory(p -> {
            CheckBoxTableCell<Task, Boolean> cell = new CheckBoxTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        columnDone.setMinWidth(160);

        tableTasks.getColumns().addAll(columnTask, columnDescription, columnDone, columnTags, columnHaveDate, columnDate);
        tableTasks.setItems(FXCollections.observableArrayList(Data.getAll()));

        btAdd.setOnAction(actionEvent -> {
            Task temp = new Task("Задача " + (Data.getAll().size() + 1),
                    "", null, null, false, false, LocalDate.MAX);
            Data.Add(temp);
            tableTasks.getItems().add(temp);
        });

        btRemove.setOnAction(actionEvent -> {
            tableSubTasks.getItems().clear();
            int SelectedIndex = tableTasks.getSelectionModel().getSelectedIndex();
            if (SelectedIndex == -1) return;  //  неФП
            Task SelectedTask = tableTasks.getItems().get(SelectedIndex);
            Data.Remove(SelectedTask);
            tableTasks.getItems().remove(SelectedIndex);
            SelectedIndex = tableTasks.getSelectionModel().getSelectedIndex();
            if (SelectedIndex == -1) return;  //  неФП
            tableSubTasks.setItems(FXCollections.observableArrayList(Data.getTask(SelectedIndex).subtasks));

        });

        btSearch.setOnAction(actionEvent -> panelSearch.setVisible(!panelSearch.isVisible()));

        btSearchOK.setOnAction(actionEvent -> {
            tableSubTasks.getItems().clear();
            boolean isNotEnded = false;
            panelSearch.setVisible(false);
            btSearch.setDisable(true);
            btClear.setDisable(false);
            btAdd.setDisable(true);

            if (rbNotEnded.isSelected()) { // неФП
                isNotEnded = true;
            }

            tableTasks.setItems(FXCollections.observableArrayList(
                    Data.filter(
                            rbNotEnded.isSelected(),
                            rbEnded.isSelected(),
                            rbAll.isSelected(),
                            cbFireTasks.isSelected() && cbFireTasks.isVisible(),
                            tbTags.getText(),
                            tbDescription.getText(),
                            cbSpecialSearch.getSelectionModel().getSelectedIndex())));
            if (isNotEnded) tableTasks.getSortOrder().addAll(columnDate, columnTask);  // неФП
        });

        btClear.setOnAction(actionEvent -> {
            btSearch.setDisable(false);
            btClear.setDisable(true);
            btAdd.setDisable(false);
            tableTasks.setItems(FXCollections.observableArrayList(Data.getAll()));
        });

        TableColumn<SubTask, String> columnSubTask = new TableColumn<>("Подзадача");
        columnSubTask.setCellValueFactory(new PropertyValueFactory<>("header"));
        columnSubTask.setCellFactory(TextFieldTableCell.forTableColumn());
        columnSubTask.setMinWidth(200);

        columnSubTask.setOnEditCommit((TableColumn.CellEditEvent<SubTask, String> event) -> {
            TablePosition<SubTask, String> pos = event.getTablePosition();
            String newSubTask = event.getNewValue().trim();
            int row = pos.getRow();
            SubTask subtask = event.getTableView().getItems().get(row);

            int i = 1;
            String t = newSubTask;
            Task temp = tableTasks.getSelectionModel().getSelectedItem();
            while (!isUniqueSubTask(t, temp.subtasks)) {  // неФП
                t = newSubTask + '(' + i + ')';
                i++;
            }
            newSubTask = t;

            newSubTask = newSubTask.isEmpty() ? event.getOldValue() : newSubTask;
            subtask.setHeader(newSubTask);

            tableSubTasks.refresh();
        });

        TableColumn<SubTask, String> columnSubDescription = new TableColumn<>("Описание");
        columnSubDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnSubDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        columnSubDescription.setMinWidth(400);

        columnSubDescription.setOnEditCommit((TableColumn.CellEditEvent<SubTask, String> event) -> {
            TablePosition<SubTask, String> pos = event.getTablePosition();
            String newSubDescription = event.getNewValue().trim();
            int row = pos.getRow();
            SubTask subtask = event.getTableView().getItems().get(row);
            Task temp = tableTasks.getSelectionModel().getSelectedItem();
            subtask.setDescription(
                    !newSubDescription.isEmpty() && isUniqueSubTask(newSubDescription, temp.subtasks) ?
                            newSubDescription : event.getOldValue()
            );
            tableSubTasks.refresh();
        });

        TableColumn<SubTask, Boolean> columnSubDone = new TableColumn<>("Подзадача выполнена?");
        columnSubDone.setMinWidth(200);
        columnSubDone.setCellValueFactory(param -> {
            SubTask subtask = param.getValue();

            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(subtask.getDone());

            booleanProp.addListener((observable, oldValue, newValue) -> {
                if (!newValue) Data.getTask(tableTasks.getSelectionModel().getSelectedIndex()).setDone(false); // неФП
                tableTasks.refresh();
                subtask.setDone(newValue);
                tableSubTasks.refresh();
            });
            return booleanProp;
        });

        columnSubDone.setCellFactory(p -> {
            CheckBoxTableCell<SubTask, Boolean> cell = new CheckBoxTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        tableTasks.setOnMouseClicked(mouseEvent -> {
            if (tableTasks.getSelectionModel().getSelectedIndex() == -1) return; // неФП
            tableSubTasks.getColumns().setAll(columnSubTask, columnSubDescription, columnSubDone);
            tableSubTasks.setItems(FXCollections.observableArrayList(tableTasks.getSelectionModel().getSelectedItem().subtasks));

        });

        btSubRemove.setOnAction(actionEvent -> {
            int SelectedIndex = tableSubTasks.getSelectionModel().getSelectedIndex();
            if (SelectedIndex == -1) return; // неФП
            tableTasks.getSelectionModel().getSelectedItem().subtasks.remove(
                    tableSubTasks.getSelectionModel().getSelectedItem());
            tableSubTasks.getItems().remove(SelectedIndex);

        });

        btSubAdd.setOnAction(actionEvent -> {
            int SelectedIndex = tableTasks.getSelectionModel().getSelectedIndex();
            if (SelectedIndex == -1) return; // неФП
            Task tempTask = Data.getTask(SelectedIndex);
            tempTask.setDone(false);
            tableTasks.refresh();
            String unicname = "Подзадача " + (tempTask.subtasks.size() + 1);
            int i = 1;
            String t = unicname;
            while (!isUniqueSubTask(t, tempTask.subtasks)) {  // неФП
                t = unicname + '(' + i + ')';
                i++;
            }
            unicname = t;
            SubTask tempSub = new SubTask(unicname, "", false);
            tempTask.subtasks.add(tempSub);
            tableSubTasks.getItems().add(tempSub);
        });

        btUP.setOnAction(actionEvent -> {  // подзадачу вверх
            int SelectedIndex = tableSubTasks.getSelectionModel().getSelectedIndex();
            if (SelectedIndex == -1 || SelectedIndex == 0) return; // неФП
            ArrayList<SubTask> SubInfo = tableTasks.getSelectionModel().getSelectedItem().subtasks;
            SubTask temp = SubInfo.get(SelectedIndex - 1);
            SubInfo.set(SelectedIndex - 1, SubInfo.get(SelectedIndex));
            SubInfo.set(SelectedIndex, temp);
            tableSubTasks.getItems().setAll(SubInfo);

        });

        btDown.setOnAction(actionEvent -> {  // подзадачу вниз
            int SelectedIndex = tableSubTasks.getSelectionModel().getSelectedIndex();
            ArrayList<SubTask> SubInfo = tableTasks.getSelectionModel().getSelectedItem().subtasks;
            if (SelectedIndex == -1 || SelectedIndex == SubInfo.size() - 1) return; // неФП
            SubTask temp = SubInfo.get(SelectedIndex + 1);
            SubInfo.set(SelectedIndex + 1, SubInfo.get(SelectedIndex));
            SubInfo.set(SelectedIndex, temp);
            tableSubTasks.getItems().setAll(SubInfo);

        });

        btSave.setOnAction(actionEvent -> {

            FileChooser fileChooser = new FileChooser(); //Класс работы с диалогом выборки и сохранения
            fileChooser.setTitle("Save Document"); //Заголовок диалога
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("Сохранение (*.save)", "*.save");//Расширение
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(btAdd.getScene().getWindow());//Указываем текущую сцену
            Data.Save(file);
        });

        rbAll.setOnAction(actionEvent -> {
            rbAll.setSelected(true);
            rbEnded.setSelected(false);
            rbNotEnded.setSelected(false);
            cbFireTasks.setVisible(false);
        });

        rbEnded.setOnAction(actionEvent -> {
            rbAll.setSelected(false);
            rbEnded.setSelected(true);
            rbNotEnded.setSelected(false);
            cbFireTasks.setVisible(false);
        });

        rbNotEnded.setOnAction(actionEvent -> {
            rbAll.setSelected(false);
            rbEnded.setSelected(false);
            rbNotEnded.setSelected(true);
            cbFireTasks.setVisible(true);
        });

        cbSpecialSearch.setOnAction(actionEvent -> {
            int SelectedIndex = cbSpecialSearch.getSelectionModel().getSelectedIndex();
            switch (SelectedIndex) {
                case 2: {
                    tbTags.setDisable(false);
                    rbAll.setSelected(false);
                    rbEnded.setSelected(false);
                    rbNotEnded.setSelected(true);
                    cbFireTasks.setVisible(true);
                    rbAll.setDisable(true);
                    rbEnded.setDisable(true);
                    rbNotEnded.setDisable(true);
                    cbFireTasks.setDisable(true);
                    break;
                }
                case 3: {
                    enableSearch(false);
                    tbDescription.setText("");
                    tbTags.setText("");
                    rbAll.setSelected(false);
                    rbEnded.setSelected(false);
                    rbNotEnded.setSelected(true);
                    cbFireTasks.setVisible(true);
                    cbFireTasks.setSelected(false);
                    break;
                }
                case 5: {
                    tbTags.setDisable(true);
                    tbTags.setText("");
                }
                default:
                    enableSearch(true);
            }
        });

        btOpenFile.setOnAction(actionEvent -> {
            tableSubTasks.getItems().clear();
            FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
            fileChooser.setTitle("Открыть Документ");//Заголовок диалога
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("Сохранения (*.save)", "*.save");//Расширение
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(btAdd.getScene().getWindow());//Указываем текущую сцену
            Data.Load(file);
            tableTasks.setItems(FXCollections.observableArrayList(Data.getAll()));
        });
    }

    void enableSearch(boolean b) {
        tbTags.setEditable(b);
        tbDescription.setEditable(b);
        rbAll.setDisable(!b);
        rbEnded.setDisable(!b);
        rbNotEnded.setDisable(!b);
        cbFireTasks.setDisable(!b);
    }
}
