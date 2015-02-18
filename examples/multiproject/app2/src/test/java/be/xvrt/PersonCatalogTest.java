package be.xvrt;

import static org.junit.Assert.assertEquals;

import org.gradle.Person;
import org.junit.Test;

public class PersonCatalogTest {

    @Test
    public void canConstructAPersonWithAName() {
        Person person = new Person("Larry");

        PersonCatalog personCatalog = new PersonCatalog();
        personCatalog.setPerson(person);

        assertEquals(person, personCatalog.getPerson());
    }

}
