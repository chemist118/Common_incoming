import java.io.Serializable;

public abstract class Mission implements Serializable {
    String header, description;
    Boolean done;

    public Mission(String header, String description, Boolean done) {
        this.header = header;
        this.description = description;
        this.done = done;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }
}