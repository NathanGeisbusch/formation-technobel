import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/** Convert given object to json */
fun <T : Any> T.toJSON(skipNull: Boolean = false, skipPrivate: Boolean = false): Any {
	return when(this) {
		is Iterable<*> -> {
			val jsonArray = JSONArray()
			this.forEach { element ->
				jsonArray.put(element?.toJSON(skipNull, skipPrivate) ?: JSONObject.NULL)
			}
			jsonArray
		}
		is Map<*,*> -> {
			val jsonObject = JSONObject()
			this.forEach { (key, value) ->
				if(value != null || !skipNull) {
					jsonObject.put(key.toString(), value?.toJSON(skipNull, skipPrivate) ?: JSONObject.NULL)
				}
			}
			jsonObject
		}
		is String, is Char, is Boolean, is Int, is Long, is Short, is Byte, is Float, is Double -> { this }
		is LocalDate, is LocalDateTime -> toString()
		else -> {
			if(this.javaClass.isEnum) return toString()
			val jsonObject = JSONObject()
			(this::class as KClass<T>).memberProperties.forEach { prop ->
				val propName = prop.name
				val isAccessible = prop.isAccessible
				if(isAccessible) {
					try {
						val propValue = prop.get(this)
						if(!skipNull || propValue != null) {
							jsonObject.put(propName, propValue?.toJSON(skipNull, skipPrivate) ?: JSONObject.NULL)
						}
					}
					catch(_: Exception) {}
				}
				else {
					if(prop.visibility == KVisibility.PRIVATE && skipPrivate) return@forEach
					prop.isAccessible = true
					try {
						val propValue = prop.get(this)
						if(!skipNull || propValue != null) {
							jsonObject.put(propName, propValue?.toJSON(skipNull, skipPrivate) ?: JSONObject.NULL)
						}
					}
					catch(_: Exception) {}
					finally { prop.isAccessible = false }
				}
			}
			jsonObject
		}
	}
}