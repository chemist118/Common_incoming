import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class Task extends Mission implements Serializable {
    private LocalDate date;
    ArrayList<SubTask> subtasks;
    ArrayList<String> tags;
    String listOfTags = "";
    Boolean haveDate;

    public Boolean getHaveDate() {
        return haveDate;
    }

    public void setHaveDate(Boolean haveDate) {
        this.haveDate = haveDate;
        date = haveDate ? LocalDate.now() : LocalDate.MAX; // неФП
    }

    public Task(String header, String description, ArrayList<SubTask> subtasks,
                ArrayList<String> tags, Boolean done, Boolean haveDate, LocalDate date) {
        super(header, description, done);
        this.subtasks = Optional.ofNullable(subtasks).orElse(new ArrayList<>());  // ФП
        this.date = date;
        this.tags = Optional.ofNullable(tags).orElse(new ArrayList<>());  // ФП
        this.tags.forEach(s -> this.listOfTags += s + ","); //  ФП
        this.haveDate = haveDate;
    }

    public String getListOfTags() {
        return listOfTags;
    }

    public void setListOfTags(String listOfTags) {
        tags.clear();
        Stream.of(listOfTags.split(","))  // ФП
                .filter(x -> isUnicAndNotEmptyTag(x.trim()))
                .forEach(x -> tags.add(x.trim()));
        this.listOfTags = "";
        this.tags.forEach(tag -> this.listOfTags += tag + ","); // ФП
    }

    public DatePicker getDate() {
        DatePicker picker = new DatePicker(date);
        restrictDatePicker(picker, LocalDate.now(), LocalDate.MAX);  // установка ограничения даты
        picker.setEditable(false);
        picker.setOnAction(actionEvent -> {
            picker.setValue(this.date =
                    haveDate ? Optional.ofNullable(picker.getValue()).orElse(this.date) : LocalDate.MAX); // ФП неФП
            //picker.setVisible(haveDate);
        });
        return picker;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public void setDone(Boolean done) {
        super.setDone(done);
        if (done)  //  неФП
            subtasks.forEach(x -> x.setDone(true));  // ФП
    }

    private boolean isUnicAndNotEmptyTag(String tag) {
        return tags.stream().noneMatch(x -> x.equalsIgnoreCase(tag)) && !tag.isEmpty();  // ФП
    }

    private void restrictDatePicker(DatePicker datePicker, LocalDate minDate, LocalDate maxDate) {
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(minDate)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        } else if (item.isAfter(maxDate)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
    }
}