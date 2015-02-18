package be.xvrt;

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
