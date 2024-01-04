# Implémentation d'une fonction pour convertir un objet en JSON

## Utilisation de l'implémentation Java

```java
public class Main {
    public static void main(String[] args) {
        var data = new Object() { final int value = 5; };
        String result = Json.toString(data);
        System.out.println(result);
    }
}
```

## Utilisation de l'implémentation Kotlin

```kotlin
fun main() {
    val data = object { val value = 5 }
    val result = data.toJSON().toString()
    println(result)
}
```
