import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class GenericTest {
	final JsonFunctions<Object> json;

	GenericTest(JsonFunctions<Object> json) {
		this.json = json;
	}

	/**
	 * Asserts that the actual json matches the expected json (regardless of field order).
	 * @param expected Expected json string
	 * @param actual Actual json string
	 */
	void assertJsonEquals(String expected, String actual) {
		JSONAssert.assertEquals(expected, actual, false);
	}

	@Test
	void testPrimitives() {
		var values = Arrays.asList("", ' ', true, false, 1, 1L, (short)1, (byte)1, 1.2F, 1.2D);
		values.forEach(value -> assertEquals(
			value, json.toJSON(value, false, false)
		));
		values.forEach(value -> assertEquals(
			value.toString(), json.toString(value, false, false)
		));
	}

	@Test
	void testList() {
		var list = Arrays.asList("apple", "banana", "cherry", null);
		String expected = "[\"apple\",\"banana\",\"cherry\",null]";
		String actual1 = json.toString(list, false, false);
		String actual2 = json.toString(list, true, false);
		assertJsonEquals(expected, actual1);
		assertJsonEquals(expected, actual2);
	}

	@Test
	void testNestedList() {
		var nestedList = List.of(
			List.of(1, 2, 3),
			List.of(4, 5, 6)
		);
		String expected = "[[1,2,3],[4,5,6]]";
		String actual = json.toString(nestedList, false, false);
		assertJsonEquals(expected, actual);
	}

	@Test
	void testSet() {
		var set = new LinkedHashSet<>(List.of("apple", "banana", "cherry"));
		String expected = "[\"apple\",\"banana\",\"cherry\"]";
		String actual = json.toString(set, false, false);
		assertJsonEquals(expected, actual);
	}

	@Test
	void testNestedSet() {
		var nestedSet = new LinkedHashSet<>(List.of(
			new LinkedHashSet<>(List.of(1, 2, 3)),
			new LinkedHashSet<>(List.of(4, 5, 6))
		));
		String expected = "[[1,2,3],[4,5,6]]";
		String actual = json.toString(nestedSet, false, false);
		assertJsonEquals(expected, actual);
	}

	@Test
	void testQueue() {
		var queue = new LinkedList<>(List.of("apple", "banana", "cherry"));
		String expected = "[\"apple\",\"banana\",\"cherry\"]";
		String actual = json.toString(queue, false, false);
		assertJsonEquals(expected, actual);
	}

	@Test
	void testNestedQueue() {
		var nestedQueue = new LinkedList<>(List.of(
			new LinkedList<>(List.of(1, 2, 3)),
			new LinkedList<>(List.of(4, 5, 6))
		));
		String expected = "[[1,2,3],[4,5,6]]";
		String actual = json.toString(nestedQueue, false, false);
		assertJsonEquals(expected, actual);
	}

	@Test
	void testMap() {
		var map = new LinkedHashMap<String, Integer>();
		map.put("apple", 1);
		map.put("banana", 2);
		map.put("cherry", 3);
		String expected = "{\"apple\":1,\"banana\":2,\"cherry\":3}";
		String actual = json.toString(map, false, false);
		assertJsonEquals(expected, actual);
	}

	@Test
	void testNestedMap() {
		var nestedMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		var innerMap = new LinkedHashMap<String, String>();
		innerMap.put("key1", "value1");
		innerMap.put("key2", null);
		nestedMap.put("nested", innerMap);
		String expected = "{\"nested\":{\"key1\":\"value1\",\"key2\":null}}";
		String actual = json.toString(nestedMap, false, false);
		assertJsonEquals(expected, actual);
	}

	@Test
	void testRecord() {
		record Person(String name, Integer age, List<Person> persons) {}
		var persons = List.of(
			new Person("Alice", 30, List.of(
				new Person("Bob", null, List.of())
			))
		);
		String expected = "[{\"name\":\"Alice\",\"age\":30,\"persons\":[{\"name\":\"Bob\",\"age\":null,\"persons\":[]}]}]";
		String expected_skipNull = "[{\"name\":\"Alice\",\"age\":30,\"persons\":[{\"name\":\"Bob\",\"persons\":[]}]}]";
		String actual = json.toString(persons, false, false);
		String actual_skipNull = json.toString(persons, true, false);
		assertJsonEquals(expected, actual);
		assertJsonEquals(expected_skipNull, actual_skipNull);
	}

	@Test
	void testRecordAndEnumAndDates() {
		enum Day {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY}
		record Moment(LocalDate date, LocalDateTime time, Day day) {}
		var moment = new Moment(LocalDate.EPOCH, LocalDateTime.MAX, Day.MONDAY);
		String expected = "{\"date\":\"%s\",\"time\":\"%s\",\"day\":\"MONDAY\"}"
			.formatted(LocalDate.EPOCH.toString(), LocalDateTime.MAX.toString());
		String actual = json.toString(moment, false, false);
		assertJsonEquals(expected, actual);
	}

	@Test
	void testComplexObject() {
		class IdClass {
			protected final long id = 47;
		}
		class Address {
			String street;
			String city;
			Address(String street, String city) {
				this.street = street;
				this.city = city;
			}
		}
		class Employee extends IdClass {
			String name;
			private final int age;
			protected Address address;
			Employee(String name, int age, Address address) {
				this.name = name;
				this.age = age;
				this.address = address;
			}
		}
		var address = new Address("123 Main St", "City");
		var employee = new Employee("John Doe", 25, address);
		String expected = "{\"id\":47,\"name\":\"%s\",\"age\":%s,\"address\":{\"street\":\"%s\",\"city\":\"%s\"}}"
			.formatted(employee.name, employee.age, address.street, address.city);
		String expected_skipPrivate = "{\"id\":47,\"name\":\"%s\",\"address\":{\"street\":\"%s\",\"city\":\"%s\"}}"
			.formatted(employee.name, address.street, address.city);
		String actual = json.toString(employee, false, false);
		String actual_skipPrivate = json.toString(employee, true, true);
		assertJsonEquals(expected, actual);
		assertJsonEquals(expected_skipPrivate, actual_skipPrivate);
	}
}

