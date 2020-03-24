package domain;

import javax.persistence.Entity;

@Entity
public class Structure extends BaseEntity {

    // TODO: many-to-one with Booking

    private String name;
    private String address;

    public Structure() {}

    public Structure(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
