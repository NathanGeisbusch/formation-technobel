import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Json {
	/**
	 * Convertit un objet java en string json. Sérialise les champs null et privés par défaut.
	 * @param obj Objet à convertir en json
	 * @return La représentation json de l'objet sous forme de chaîne de caractères
	 */
	public static String toString(Object obj) {
		return toString(obj, false, false);
	}

	/**
	 * Convertit un objet java en objet json. Sérialise les champs null et privés par défaut.
	 * @param obj Objet à convertir en json
	 * @return JSONObject ou JSONArray ou JSONObject.NULL
	 * ou l'objet en paramètre si c'est un type primitif
	 * ou un string si l'objet est un LocalDate/LocalDateTime/enum
	 */
	public static Object toJSON(Object obj) {
		return toJSON(obj, false, false);
	}

	/**
	 * Convertit un objet java en string json. Sérialise les champs privés par défaut.
	 * @param obj Objet à convertir en json
	 * @param skipNull Si true, ne sérialise pas les champs ayant la valeur null
	 * @return La représentation json de l'objet sous forme de chaîne de caractères
	 */
	public static String toString(Object obj, boolean skipNull) {
		return toString(obj, skipNull, false);
	}

	/**
	 * Convertit un objet java en objet json. Sérialise les champs privés par défaut.
	 * @param obj Objet à convertir en json
	 * @param skipNull Si true, ne sérialise pas les champs ayant la valeur null
	 * @return JSONObject ou JSONArray ou JSONObject.NULL
	 * ou l'objet en paramètre si c'est un type primitif
	 * ou un string si l'objet est un LocalDate/LocalDateTime/enum
	 */
	public static Object toJSON(Object obj, boolean skipNull) {
		return toJSON(obj, skipNull, false);
	}

	/**
	 * Convertit un objet java en string json.
	 * @param obj Objet à convertir en json
	 * @param skipNull Si true, ne sérialise pas les champs ayant la valeur null
	 * @param skipPrivate Si true, ne sérialise pas les champs privés
	 * @return La représentation json de l'objet sous forme de chaîne de caractères
	 */
	public static String toString(Object obj, boolean skipNull, boolean skipPrivate) {
		var value = toJSON(obj, skipNull, skipPrivate);
		return value == null ? JSONObject.NULL.toString() : value.toString();
	}

	/**
	 * Convertit un objet java en objet json.
	 * @param obj Objet à convertir en json
	 * @param skipNull Si true, ne sérialise pas les champs ayant la valeur null
	 * @param skipPrivate Si true, ne sérialise pas les champs privés
	 * @return JSONObject ou JSONArray ou JSONObject.NULL
	 * ou l'objet en paramètre si c'est un type primitif
	 * ou un string si l'objet est un LocalDate/LocalDateTime/enum
	 */
	public static Object toJSON(Object obj, boolean skipNull, boolean skipPrivate) {
		if(obj instanceof Iterable<?> list) {
			JSONArray jsonArray = new JSONArray();
			for(var element : list) jsonArray.put(toJSON(element, skipNull, skipPrivate));
			return jsonArray;
		} else if(obj instanceof Map<?, ?> map) {
			JSONObject jsonObject = new JSONObject();
			for(var entry : map.entrySet()) {
				try {
					jsonObject.put(entry.getKey().toString(), toJSON(entry.getValue(), skipNull, skipPrivate));
				}
				catch(Exception ignored) {}
			}
			return jsonObject;
		} else if(obj == null) {
			return skipNull ? null : JSONObject.NULL;
		} else if(
			obj instanceof String ||
			obj instanceof Character ||
			obj instanceof Boolean ||
			obj instanceof Integer ||
			obj instanceof Long ||
			obj instanceof Short ||
			obj instanceof Byte ||
			obj instanceof Float ||
			obj instanceof Double) {
			return obj;
		} else if(
			obj instanceof LocalDate ||
			obj instanceof LocalDateTime ||
			obj.getClass().isEnum()) {
			return obj.toString();
		} else {
			var fields = new ArrayList<Field>();
			for(var c = obj.getClass(); c != null; c = c.getSuperclass()) {
				fields.addAll(List.of(c.getDeclaredFields()));
			}
			JSONObject jsonObject = new JSONObject();
			for(Field field : fields) {
				if(field.getName().startsWith("this$")) continue;
				var isAccessible = field.canAccess(obj);
				if(isAccessible) {
					try {
						Object value = field.get(obj);
						jsonObject.put(field.getName(), toJSON(value, skipNull, skipPrivate));
					}
					catch(Exception ignored) {}
				} else if(!skipPrivate) {
					field.setAccessible(true);
					try {
						Object value = field.get(obj);
						jsonObject.put(field.getName(), toJSON(value, skipNull, skipPrivate));
					}
					catch(Exception ignored) {}
					finally {
						field.setAccessible(false);
					}
				}
			}
			return jsonObject;
		}
	}
}
