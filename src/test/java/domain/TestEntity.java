package domain;

import javax.persistence.Entity;

@Entity
public class TestEntity extends BaseEntity {
    // This entity has the only purpose to represent a concrete class
    // extending BaseEntity

    private String field;

    public TestEntity() {}

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
