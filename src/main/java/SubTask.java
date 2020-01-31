import java.io.Serializable;

public class SubTask extends Mission implements Serializable {

    public SubTask(String header, String description, Boolean done) {
        super(header, description, done);
    }
}
