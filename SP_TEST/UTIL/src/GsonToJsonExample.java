import com.google.gson.Gson;

public class GsonToJsonExample {
	
	public static void main(String[] args) {
		Person person = new Person("홍길동", 25);
		Gson gson = new Gson();
		String json = gson.toJson(person);
		System.out.println(json); // {"name":"홍길동","age":25}
	}

	static class Person {
		String name;
		int age;

		Person(String name, int age) {
			this.name = name;
			this.age = age;
		}
	}
}
