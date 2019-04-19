package waterfall.model;

import javax.persistence.*;

@Entity
@Table(name = "gametype")
public class GameType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    private String type;

    public GameType() {

    }

    public GameType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof GameType))
            return false;

        GameType gameType = (GameType) obj;

        if (gameType.getId().equals(this.id) && gameType.getType().equals(this.type)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int constant = 11;
        int result = 1;

        result = result * constant + this.id.hashCode();
        result = result * constant + this.type.hashCode();

        return result;
    }

}
