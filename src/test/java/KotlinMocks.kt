enum class MockEnum {VAL1,VAL2,VAL3}

open class Superclass {
	protected val id = 42
}

data class MockDataClass(
	val value: Byte = 1,
	private val hidden: Short = -1,
)

class MockClass(
	val data: MockDataClass = MockDataClass(),
	val enum: MockEnum = MockEnum.VAL2,
	val nullable: Int? = null,
	private val secret: Long = -1,
	internal val map: Map<String,String?> = mapOf(
		"f1" to "v1",
		"f2" to null,
	),
) : Superclass()
