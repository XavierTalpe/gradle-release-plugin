package be.xvrt;

import org.gradle.Person;

public class PersonCatalog {

    private Person person;

    public PersonCatalog() {
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

}
