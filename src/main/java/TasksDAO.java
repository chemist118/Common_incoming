import java.io.File;
import java.util.ArrayList;

public interface TasksDAO {
    void Load(File file); // Загрузка сохранения
    void Save(File file); // Сохранение в файл
    ArrayList<Task> getAll(); // Получить список всех задач
    Task getTask(int index); // Получить задачу по ндексу
    void Add(Task task); // Добавить задачу
    void Remove(Task task); // Удалить задачу
    ArrayList<Task> filter(
            Boolean isNotEnded, // Выбраны незавершенные задачи
            Boolean isEnded, // Выбраны завершенные задачи
            Boolean isAll, // Выбраны все задачи
            Boolean FireTasks, // Выбраны просроченные задачи и задачи на ближайшую неделю
            String tags, // список тегов через запятую
            String Description, // строка в описании задачи
            int numOfSpecialFilter // код специального фильтра
    ); // Отфильтровать задачи
}
