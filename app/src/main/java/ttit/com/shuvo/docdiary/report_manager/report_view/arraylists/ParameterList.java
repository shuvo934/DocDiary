package ttit.com.shuvo.docdiary.report_manager.report_view.arraylists;

public class ParameterList {
    private String id;
    private String name;

    public ParameterList(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
