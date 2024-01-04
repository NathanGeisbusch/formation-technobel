package be.technobel.bibliotheque.model.auth;

public class Library implements Cloneable {
    private long id = 0;
    private String name;
    private Address address;

    public long getId() {
        return id;
    }

    public Library setId(long id) {
        if(id < 0) throw new IllegalArgumentException();
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Library setName(String name) {
        if(name == null) throw new IllegalArgumentException();
        this.name = name;
        return this;
    }

    public Address getAddress() {
        return address;
    }

    public Library setAddress(Address address) {
        if(address == null) throw new IllegalArgumentException();
        this.address = address;
        return this;
    }

    public Library(long id, String name, Address address) {
        if(name == null || address == null || id < 0) throw new IllegalArgumentException();
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public Library(String name, Address address) {
        if(name == null || address == null) throw new IllegalArgumentException();
        this.name = name;
        this.address = address;
    }

    @Override
    public Library clone() {
        try {
            Library clone = (Library)super.clone();
            clone.address = clone.address.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
