package twistrating.models;

public class TwistData {
    public final String id;
    public final String name;
    public final String imageUrl;
    public final String charId;

    public TwistData(String id, String name, String imageUrl, String charId) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.charId = charId;
    }
}
