package twistrating.views;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Twist extends Model {

    @Id
    @Constraints.Min(10)
    public String id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String imageUrl;

    @Constraints.Required
    public String charId;

    public int likeCount;

    public int neutralCount;

    public int dislikeCount;

    public static Finder<String, Twist> find = new Finder<>(String.class, Twist.class);
}