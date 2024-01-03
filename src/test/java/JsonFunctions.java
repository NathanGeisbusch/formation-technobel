public interface JsonFunctions<T> {
	Object toJSON(T obj, boolean skipNull, boolean skipPrivate);
	String toString(T obj, boolean skipNull, boolean skipPrivate);
}
