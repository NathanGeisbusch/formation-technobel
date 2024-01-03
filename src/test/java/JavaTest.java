import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JavaTest extends GenericTest {
	JavaTest() {
		super(new JsonFunctions<>() {
			@Override
			public Object toJSON(Object obj, boolean skipNull, boolean skipPrivate) {
				return Json.toJSON(obj, skipNull, skipPrivate);
			}

			@Override
			public String toString(Object obj, boolean skipNull, boolean skipPrivate) {
				return Json.toString(obj, skipNull, skipPrivate);
			}
		});
	}

	@Test
	void testNull() {
		assertEquals(JSONObject.NULL, json.toJSON(null, false, false));
		assertNull(json.toJSON(null, true, false));
		assertEquals("null", json.toString(null, false, false));
		assertEquals("null", json.toString(null, true, false));
	}

	@Test
	void testKotlin() {
		var mock = new MockClass();
		String expected = "{\"id\":42,\"data\":{\"value\":1,\"hidden\":-1},\"enum\":\"VAL2\",\"map\":{\"f1\":\"v1\",\"f2\":null},\"nullable\":null,\"secret\":-1}";
		String expected_skipNull = "{\"id\":42,\"data\":{\"value\":1,\"hidden\":-1},\"enum\":\"VAL2\",\"map\":{\"f1\":\"v1\"},\"secret\":-1}";
		String expected_skipPrivate = "{}";
		String expected_skipNullPrivate = "{}";
		String actual = json.toString(mock, false, false);
		String actual_skipNull = json.toString(mock, true, false);
		String actual_skipPrivate = json.toString(mock, false, true);
		String actual_skipNullPrivate = json.toString(mock, true, true);
		assertJsonEquals(expected, actual);
		assertJsonEquals(expected_skipNull, actual_skipNull);
		assertJsonEquals(expected_skipPrivate, actual_skipPrivate);
		assertJsonEquals(expected_skipNullPrivate, actual_skipNullPrivate);
	}
}
