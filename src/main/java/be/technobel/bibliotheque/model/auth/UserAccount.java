package be.technobel.bibliotheque.model.auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.util.Arrays;

public class UserAccount implements Cloneable {
    private long id = 0;
    private String login;
    private byte[] secretHash;
    private byte[] secretSalt;
    private UserRole role;
    private String lastName;
    private String firstName;
    private LocalDate birthDate;
    private Address address;

    public long getId() {
        return id;
    }

    public UserAccount setId(long id) {
        if(id < 0) throw new IllegalArgumentException();
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public UserAccount setLogin(String login) {
        if(login == null || login.isBlank()) throw new IllegalArgumentException();
        this.login = login;
        return this;
    }

    public byte[] getSecretHash() {
        return secretHash.clone();
    }

    public byte[] getSecretSalt() {
        return secretSalt.clone();
    }

    public UserAccount setPasswordHash(byte[] secretHash, byte[] secretSalt) {
        if(secretHash == null || secretSalt == null) throw new IllegalArgumentException();
        if(secretHash.length != 16 || secretSalt.length != 16) throw new IllegalArgumentException();
        this.secretHash = secretHash;
        this.secretSalt = secretSalt;
        return this;
    }

    public UserRole getRole() {
        return role;
    }

    public UserAccount setRole(UserRole role) {
        if(role == null) throw new IllegalArgumentException();
        this.role = role;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserAccount setLastName(String lastName) {
        if(lastName == null) throw new IllegalArgumentException();
        this.lastName = lastName;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserAccount setFirstName(String firstName) {
        if(firstName == null) throw new IllegalArgumentException();
        this.firstName = firstName;
        return this;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public UserAccount setBirthDate(LocalDate birthDate) {
        if(birthDate == null) throw new IllegalArgumentException();
        this.birthDate = birthDate;
        return this;
    }

    public Address getAddress() {
        return address;
    }

    public UserAccount setAddress(Address address) {
        if(address == null) throw new IllegalArgumentException();
        this.address = address;
        return this;
    }

    public UserAccount(long id, String login, byte[] secretHash, byte[] secretSalt, UserRole role,
                       String lastName, String firstName, LocalDate birthDate, Address address) {
        if(login == null || secretHash == null || secretSalt == null || role == null ||
        lastName == null || firstName == null || birthDate == null || address == null)
            throw new IllegalArgumentException();
        if(id < 0 || login.isBlank() || secretHash.length != 16 || secretSalt.length != 16)
            throw new IllegalArgumentException();
        this.id = id;
        this.login = login;
        this.secretHash = secretHash;
        this.secretSalt = secretSalt;
        this.role = role;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.address = address;
    }

    public UserAccount(long id, String login, String password, UserRole role,
                       String lastName, String firstName, LocalDate birthDate, Address address) {
        if(login == null || role == null || lastName == null || firstName == null || birthDate == null || address == null)
            throw new IllegalArgumentException();
        if(id < 0 || login.isBlank()) throw new IllegalArgumentException();
        this.setPassword(password);
        this.id = id;
        this.login = login;
        this.role = role;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.address = address;
    }

    public UserAccount(String login, String password, UserRole role,
                       String lastName, String firstName, LocalDate birthDate, Address address) {
        if(login == null || role == null || lastName == null || firstName == null || birthDate == null || address == null)
            throw new IllegalArgumentException();
        if(login.isBlank()) throw new IllegalArgumentException();
        this.setPassword(password);
        this.login = login;
        this.role = role;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.address = address;
    }

    public UserAccount setPassword(String password) {
        if(password == null || password.isBlank()) throw new IllegalArgumentException();
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            this.secretHash = factory.generateSecret(spec).getEncoded();
            this.secretSalt = salt;
            return this;
        }
        catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyPassword(String password) {
        if(password == null || password.isBlank()) throw new IllegalArgumentException();
        byte[] salt = this.secretSalt;
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Arrays.equals(hash, this.secretHash);
        }
        catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserAccount clone() {
        try {
            UserAccount clone = (UserAccount)super.clone();
            clone.secretHash = clone.secretHash.clone();
            clone.secretSalt = clone.secretSalt.clone();
            clone.address = clone.address.clone();
            return clone;
        }
        catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
