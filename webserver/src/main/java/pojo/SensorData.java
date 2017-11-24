package pojo;

import java.util.Date;

public class SensorData {
    private Integer id;

    private Date time;

    private String name;

    private String type;

    private String boardip;

    private Integer value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getBoardip() {
        return boardip;
    }

    public void setBoardip(String boardip) {
        this.boardip = boardip == null ? null : boardip.trim();
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}