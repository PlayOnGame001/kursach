package testentity;

import org.junit.jupiter.api.Test;
import org.station.entity.Person;

import static org.junit.jupiter.api.Assertions.*;

class PersonImpl extends Person {
    public PersonImpl() {
        super();
    }

    public PersonImpl(String serviceStationName, String fullName, String phoneNumber) {
        super(serviceStationName, fullName, phoneNumber);
    }
}

public class TestPerson {

    @Test
    public void testConstructorAndGetters() {
        PersonImpl person = new PersonImpl("СТО Хмельницький", "Олег Майстер", "0990001122");

        assertEquals("СТО Хмельницький", person.getServiceStationName());
        assertEquals("Олег Майстер", person.getFullName());
        assertEquals("0990001122", person.getPhoneNumber());
    }

    @Test
    public void testSetters() {
        PersonImpl person = new PersonImpl();
        person.setId(12L);
        person.setServiceStationName("СТО Київ");
        person.setFullName("Світлана Ремонт");
        person.setPhoneNumber("0671234567");

        assertEquals(12L, person.getId());
        assertEquals("СТО Київ", person.getServiceStationName());
        assertEquals("Світлана Ремонт", person.getFullName());
        assertEquals("0671234567", person.getPhoneNumber());
    }
}
