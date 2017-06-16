package es.migueluvieu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import es.migueluvieu.model.Persona;

/**
 * Hello world!
 *
 */
public class Main {

	public static void main(String[] args) {
		functionalInterfaces();

		opcional();

		streams();

		collectors();

		parallelStream();

		extensionesCollectionsList();
	}

	/**
	 * FUNCIONAL INTERFACES Un interface funcional es aquel interface que solo dispone de un método abstracto, puede
	 * contener n defaults y n static pero solo un abstract.
	 */
	private static void functionalInterfaces() {
		System.out.println("/**************** PRUEBAS SOBRE FUNCIONAL INTERFACES");

		/*
		 * INTERFACE FUNCTION<T,R> Reciben tipo T y devuelven tipo R
		 */

		// creamos una functionalInterface que la llamamos getNombre. Recibe Persona, devuelve String. La implementa una
		// lamba
		Function<Persona, String> getNombre = per -> per.getNombre();
		Persona person = new Persona();
		person.setNombre("Manolo");
		// con apply se ejecuta la function
		System.out.println(getNombre.apply(person));

		// A las functions se les pueden añadir más encadenadas. Por ejempo a getNombre podemos añadirle por ejemplo el
		// método AndThen que le concatena otra funcion. En este caso añadimos un lambda directamente que pasa a
		// mayúsuclas
		getNombre.andThen(nom -> nom.toUpperCase());

		// podemos utilizar reference methods, que veremos más adelante pero esto podría quedar más compacto
		getNombre.andThen(String::toUpperCase);

		// podemos crear otra functional interface que añadimos a el nombre una cadena
		Function<String, String> getOtroNombre = x -> x + "_añadido";
		System.out.println(getNombre.andThen(getOtroNombre).andThen(String::toUpperCase).apply(person));
		// consola MANOLO_AÑADIDO

		// Si los paámetros de la function de e/S son iguales, podemos utilizar UnaryOperator<T> que indicaentrada y
		// salida de tipo T. En este caso Function<String, String> al tener misma e/s podemos utilizar
		// UnaryOperator<String>, es equivalente
		UnaryOperator<String> getOtroNombreUnary = x -> x + "_añadido";

		// Podemos utilizar los methos references tambien para crear en este caso una nueva persona (Persona::new))
		Persona otra = getNombre.andThen(getOtroNombre).andThen(String::toLowerCase).andThen(Persona::new)
				.apply(person);
		System.out.println(otra); // consola Persona [nombre=manolo_añadido, pais=null, edad=0]

		// También se pueden componer funciones con método compose:
		UnaryOperator<String> minusculas = l -> l.toLowerCase();

		// se pone a la inversa, se ejecuta de derecha a izquierda, primero getNombre y luego minúsculas
		Function<Persona, String> nombreEnMinusculas = minusculas.compose(getNombre);
		System.out.println(nombreEnMinusculas.apply(person)); // manolo

		// y añadir un AndThen
		System.out.println(nombreEnMinusculas.andThen(getOtroNombre).apply(person));// manolo_añadido

		/********* TIPOS ESPECIALES DE FUNCTIONS ********/

		/*
		 * CONSUMER -> solo/recibe, NO GENERA, en este caso consume
		 */
		// típico consumer, System.out::println, recibe pero no devuelve. Se utiliza método accept
		// reference
		Consumer<Persona> pintaNombre = x -> System.out.println(x);
		// equivalente con methos reference
		Consumer<Persona> pintaNombre2 = System.out::println;

		/*
		 * SUPPLIER es a la inversa, no consume/recibe nada, simplemente genera/devuelve
		 */
		// por ejemplo podemos emplear para devolver una persona con el constructor vacío
		Supplier<Persona> dameNuevaPersona = () -> new Persona();
		// equivalente con method reference
		Supplier<Persona> dameNuevaPersona2 = Persona::new;

		Persona per = dameNuevaPersona.get();
		per.setApellido("apellido");

		/*
		 * PREDICATE -> nos devuelve siempre un boolean true/false. Solo tiene una entrada, Predicate<T>
		 */
		// functional interface debe un resultado boolean. Se utiliza método test
		Predicate<String> cadenaCorta = cadena -> cadena.length() < 3;
		cadenaCorta.test("la");// true

		/*
		 * TIPOS PRIMITIVOS -> tiene sus propios functions, ya que no puede inferenciarse clase
		 */
		// Un UnaryOperator de int podría utilizarse el IntUnaryOperator, al igual con double o long
		IntUnaryOperator sumaInt = numero -> numero + 1;
		DoubleUnaryOperator sumaDecimal = numero -> numero + 1;
		LongUnaryOperator suma1Long = numero -> numero + 1;

		// si alguno la entrada es un tipo primitivo, hay interface funcionales especiales
		// si entrada int, está la intFunction, devuelve un string en este caso
		IntFunction<String> convertToString = (i -> i + " ");
		// equivalente
		IntFunction<String> convertToString2 = String::valueOf;

		// si la salida es un primitivo, recibe string y devuelve un int en este caso anteponemos tl To
		ToIntFunction<String> getSize = cadena -> cadena.length();
		// equivalente
		ToIntFunction<String> getSize2 = String::length;

		List<String> personas = Arrays.asList("asas", "aas");
		personas.sort((o1, o2) -> o1.length() - o2.length());

		IntFunction<Integer> dameEntero = Integer::new;
		dameEntero.apply(1);

		Function<String, Integer> dameEntero2 = Integer::new;
		dameEntero2.apply("2");

		/*
		 * FUNCTIONS Y OPERATOR BI* con el BI significa que la function o operator recibe dos parámetros
		 */
		// en este caso, reibe una PErsona y un String y genera un Integer
		BiFunction<Persona, String, Integer> getTamañoNombreRaro = (persona,
				cadena) -> (persona.getNombre().concat(cadena)).length();

		// Lo mismo para BiPredicate, recibe dos personas y devuelve boolean
		BiPredicate<Persona, Persona> mismoNombre = (p1, p2) -> p1.getNombre().equals(p2.getNombre());

		// Si todas las clases son del mismo tipo, utilizamos BinaryOperator. Recibimos dos Integer, devolvemos 1
		// integer
		BinaryOperator<Integer> sumando = (n1, n2) -> n1 + n2;
		sumando.apply(4, 5); // 9

		// Para primitivos, hay Binary especiales recibirían 2 tipos primitivos y devolvería el mismo tipo primitivo
		// En este caso recibiría dos int, devolvería 1 int

		IntBinaryOperator sumando2 = (n1, n2) -> n1 + n2;
		sumando2.applyAsInt(4, 5); // 9

		// Casos particulares, el compareCase no es un método static, va sobre
		// una instancia,. Lo que haría aquí sería recibir dos parámetros o1 y o2 y aplicaría
		// o1.compareToIgnoreCase(o2), después de esa comparación.
		personas.sort(String::compareToIgnoreCase);

		List<Integer> numeros = Arrays.asList(10, 5, 3, 8);
		numeros.sort((n1, n2) -> n1 - n2);
		Consumer<List<Integer>> pintaNumeros = n -> n.stream().forEach(System.out::println);
		pintaNumeros.accept(numeros);
	}

	/**
	 * OPTIONAL,OptionalInt, OptionalDouble y OptionalLong evitan que un método devuelva null
	 */
	private static void opcional() {
		System.out.println("/**************** PRUEBAS SOBRE OPTIONAL");
		// se crean para evitar los nullpointer.
		// Es un wrapper creamos un objeto Persona, lo podemos crear normal o así por ejemplo
		// con el of, NO DEBE SER NULL
		Optional<Persona> persona = Optional.of(new Persona("Juan"));
		// con empty crearíamos vacío
		Optional<Persona> personaVacia = Optional.empty();
		// con el ofNullable se podría crear con una referencia null
		Optional<Persona> personaAceptaVacio = Optional.ofNullable(new Persona("Juan"));
		Optional<Persona> personaNull = Optional.ofNullable(null);

		// Optional.isPresent nos indica si el Optional tiene o no valor
		System.out.println(persona.isPresent()); // true
		System.out.println(personaAceptaVacio.isPresent()); // true
		System.out.println(personaNull.isPresent()); // false

		// el método get obtiene el payload del optional
		if (persona.isPresent()) {
			System.out.println(persona.get()); // Persona [nombre=Juan, pais=null, edad=0]

		}

		// la foma más correcta de utilizar Optional es con el método map, el cual aplicado sobre optional si está
		// presente y devuelve un
		// optional vacío si no lo está, este devuelve un optional con juan
		System.out.println(persona.map(per -> "Hola " + per.getNombre())); // Optional[Hola Juan]
		// devuelve un optional vacío, no da error, se evita el nullpointer
		System.out.println(personaVacia.map(per -> "Hola " + per.getNombre())); // Optional.empty

		// para los empty podemos aplicar un valor por defecto para que salga en vez de un Optional.empty.
		// La diferencia es que siempre se devolverá el payload, es decir, no devolverá el optional. el método orElse
		// viene a ser una
		// función terminal. En este caso siempre devolverá una cadea
		System.out.println(personaVacia.map(per -> "Hola " + per.getNombre()).orElse(" Hola anónimo"));

		// ejemplo más completo, a partir de una persona, se obtiene su nombre, se concatena cadena y si está vacío
		// devuelvo Estoy Solo

		System.out.println(persona.map(Persona::getNombre).map("Hola "::concat).orElse("Estoy solo"));
		System.out.println(personaVacia.map(Persona::getNombre).map("Hola "::concat).orElse("Estoy solo"));

		// más completo
		List<Persona> personas = Arrays.asList(new Persona("PEPE"), new Persona("JUAN"), null);
		// pasamos a lista de optionals, con ofNullable para que se acepten nullables.
		// opt.map(Persona::getNombre).map("Hola "::concat).orElse("Estoy Solo") le llega el optional y con map
		// accedemos al nombre y concatenamos.
		// Si no hay nombre devolverá "Estoy Solo"
		// al final devuelve un stream de String (ya que el optional tiene el orElse, si no sería listado de optionals)
		personas.stream().map(Optional::ofNullable)
				.map(opt -> opt.map(Persona::getNombre).map("Hola "::concat).orElse("Estoy Solo"))
				.forEach(System.out::println);// Hola PEPE Hola JUAN Estoy Solo

		// lo mismo pero quitando el orElse -> ver que ahora devuelve un listado
		// de optionals
		personas.stream().map(Optional::ofNullable).map(opt -> opt.map(Persona::getNombre).map("Hola "::concat))
				.forEach(System.out::println);
		// Optional[Hola PEPE] Optional[Hola JUAN] Optional.empty

		// operador flatMap -> aplana los resultados, se ve con ejemplo claro.
		// tenemos en la clase Persona ahora un nuevo campo nombre2 que a su vez es un optional
		Persona persona2 = new Persona();
		persona2.setNombre2(Optional.of("Pepito"));
		// ahora tenemos un Optional<Persona>
		Optional<Persona> personaOpt = Optional.ofNullable(persona2);
		System.out.println(personaOpt.map(Persona::getNombre2)); // Optional[Optional[Pepito]]
		// esto es el resultado Optional[Optional[PEpito]], ya que persona es un optional, y el mismo getNombre2
		// devuelve otro optional
		// Para evitarlo, se puede "aplanar" con el operador flatMap
		System.out.println(personaOpt.flatMap(Persona::getNombre2)); // Optional[Pepito]
		// ahora el resultado es Optional[PEpito]

	}

	/**
	 * STREAMS Un stream es una secuencia de datos que son procesados en una aplicación (java.util.Stream). Las
	 * colecciones de Java ofrecen streams de los objetos que contienen (métodos stream y parallelStream).
	 */

	private static void streams() {
		System.out.println("/**************** PRUEBAS SOBRE STREAMS");

		List<Persona> personas = Arrays.asList(new Persona("Pepe"), new Persona("Manolo"), new Persona("Jose"));

		// a partir de un array de personas podemos crear un array de strings
		// con sus nombres de esta forma
		List<String> nombres = personas.stream().map(Persona::getNombre).collect(Collectors.toList());
		// equivalente pero con lambda en veZ de reference method
		nombres = personas.stream().map(it -> it.getNombre()).collect(Collectors.toList());
		// con el operador map se recibe un listado de objectos y se devuelve
		// otro listado de los mismo objetos o transformados, en este caso de
		// Personas a strings
		nombres.forEach(System.out::println);

		/*
		 * STREAM PIPELINE -> hooks de los streams. Creación del stream, operaciones intermedias .map, .flatMap,
		 * .filter, ...operaciones finales como .collect, .reduce,...
		 */

		// ***************** CREACIÓN *****************
		personas.stream(); // Collections stream
		String[] otras = {};
		Arrays.stream(otras); // Arrays.stream
		Stream<String> mas = Stream.of("Pepe", "Manolo"); // utilidades Stream

		// ***************** INTERMEDIAS: *****************
		// Convertir .map, Filtrar .filter u Ordenar . order

		// - CONVERTIR. map -> transforma los elementos del stream ejemplo claro, stream de persona a stream de strings.
		// Tiene el mismo número de elementos, están en el mismo orden
		nombres = personas.stream().map(it -> it.getNombre()).collect(Collectors.toList());

		// - FILTRAR. se usa filter. Acepta un Predicate, el cual si devuelve true, ese elemento se devuelve en el
		// stream resultante
		// lo normal es que tengan distinto número de elementos, pero serán del mismo tipo y están ordenados igualmente
		personas = personas.stream().filter(it -> it.getNombre().startsWith("P")).collect(Collectors.toList());
		System.out.println(personas.toString()); // [Persona [nombre=Pepe, pais=null, edad=0]]

		// - ORDENAR. sorted. Se le debe pasar un comparator o una lamba expression. Por defecto hará una ordenación
		// natural
		// el nuevo stream tiene mismo número que original, son del mismo tipo pero distinto orden
		// má adelante se ve el comparator de forma más precisa
		nombres.sort((o1, o2) -> o1.length() - o2.length());

		// ***************** TERMINALES *****************/
		// reduce. producen un resultado final sobre un stream

		// el primer elemento es el identity, el valor inicial. en cada iteracion el resultado de a +"\n"+b pasa a ser
		// a, y el siguiente de la lista será b.
		System.out.println(Stream.of("Pepe", "Manolo", "Jose").reduce("", (a, b) -> a + "_" + b)); // _Pepe_Manolo_Jose

		// se ve mejor con este sumatorio, donde la suma se va a acumulando y pasa a ser la a en cada it
		System.out.println(Stream.of(3, 6, 7).reduce(0, (a, b) -> a + b)); // 16

		// o este reduce que nos dice el número mayor
		System.out.println(Stream.of(3, 6, 7).reduce(0, (a, b) -> a > b ? a : b)); // 7

		// ojo, esto va generando un string, al final es inmutable
		Stream<String> nombres2 = Stream.of("Pepe", "Manolo", "Jose");
		System.out.println(nombres2.sorted().reduce("", String::concat)); // JoseManoloPepe
		// para mejora de rendimiento podemos aplicar el resultado sobre una estructura mutable, por ejemplo volcarlo
		// sobre un stringBuilder a través de un forEach
		StringBuilder result = new StringBuilder();
		Stream.of("Pepe", "Manolo", "Jose").forEach(s -> result.append(s));
		// equivalente con method reference
		Stream.of("Pepe", "Manolo", "Jose").forEach(result::append);

		// tambien se pueden aplicar los métodos
		// distinct: Devuelve un stream con elementos únicos (según sea la implementación de equals para un elemento del
		// stream).
		// limit(n): Devuelve un stream cuya máxima longitud es n.
		// skip(n): Devuelve un stream en el que se han descartado los primeros n números.

		// NOTA: Collectors.joining le debe llegar un stream de strings siempre!!!!!!
		Consumer<String> pinta = System.out::println;
		pinta.accept(Stream.of(1, 1, 2, 3, 4, 5).distinct().map(String::valueOf).collect(Collectors.joining("-")));
		// 1-2-3-4-5
		pinta.accept(Stream.of(1, 1, 2, 3, 4, 5).limit(3).map(String::valueOf).collect(Collectors.joining("-")));
		// 1-1-2
		pinta.accept(Stream.of(1, 1, 2, 3, 4, 5).skip(2).map(String::valueOf).collect(Collectors.joining("-")));
		// 2-3-4-5

		// Existen Stream especiales como por ejempo IntStream que es un stream exclusivo para primitivos in, o
		// LongStream, DoubleStream,.. creamos un array de integers grande, de 100, utilizando el IntStream.range.

		int[] numeros = IntStream.range(0, 100).toArray();
		System.out.println(numeros); // [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, ...100]
		// Si queremos por ejemplo un List<Integer> a partir del intStream,se debe obtener un Stream<Integer>. Para
		// convertir stream de primitivos a stream de objetos-> se utiliza mapToObj

		List<Integer> numerosInteger = IntStream.range(0, 100).mapToObj(Integer::new).collect(Collectors.toList());
		System.out.println(numerosInteger); // [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, ...100]
		// una equivalencia para el caso de pasar de int a Integer es añadir simplemente boxed en vez de mapToObj
		List<Integer> numerosInteger2 = IntStream.range(0, 100).boxed().collect(Collectors.toList());
		System.out.println(numerosInteger2); // [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, ...100]

		// más operaciones con intStream
		IntStream.iterate(0, i -> i + 2).limit(3); // > 0, 2, 4
		// itera de dos en dos y hasta tener los 3 primeros

		IntStream.range(1, 5).map(i -> i * i);// > 1, 4, 9, 16
		IntStream.range(1, 5).filter(i -> i % 2 == 0).allMatch(i -> i % 2 == 0);// true
		IntStream.range(1, 5).filter(i -> i % 2 == 0).noneMatch(i -> i % 2 != 0);// false
		IntStream.range(1, 5).max().getAsInt(); // > 4
		IntStream.range(1, 5).min().getAsInt(); // > 1
		IntStream.range(1, 5).reduce(1, (x, y) -> x * y); // > 24

	}

	/**
	 * COLLECTORS son estructuras modificables que reciben los elementos de un stream y construyen un resultado el
	 * operador collect sobre un stream es terminal.
	 */
	private static void collectors() {
		System.out.println("/**************** PRUEBAS SOBRE COLLECTORS");

		// Creamos un array de personas
		List<Persona> personas = Arrays.asList(new Persona("Jose", "España"), new Persona("Mike", "Inglaterra"),
				new Persona("Luigi", "Italia"), new Persona("Pepe", "España"), new Persona("Mario", "Italia"));

		// Se puede obtener una lista de los nombres de las personas. Al collect lelga un stream de strings y con
		// toList() crea el List<String>
		List<String> nombres = personas.stream().map(Persona::getNombre).collect(Collectors.toList());
		nombres.stream().forEach(System.out::println);

		
		// ***************** AGRUPACIONES *****************/
		
		// Podemos agrupar por un campo, por ejemplo agrupar las personas por país utilizando Collectors.groupingBy.
		// Según la api, devolverá un Map<claseDelCriterio,List<Class> resultante es decir, en este caso devolverá un
		// Map<String, List<Persona>>, la key para cada registro será el criterio (String) y el keySet la lista
		// personas de ese país

		Map<String, List<Persona>> personasAgrupadasPais = personas.stream()
				.collect(Collectors.groupingBy(Persona::getPais));
		// Pintaría: Inglaterra :: Mike, España :: Jose - Pepe, Italia :: Luigi - Mario

		for (String key : personasAgrupadasPais.keySet()) {
			// observar Collectors.joining, recoje un stream con los nombres (siempre debe recoger un stream de streams)
			// y el resultado será un string con todos concatenados con el separador indicado
			System.out.println(key + " :: " + personasAgrupadasPais.get(key).stream().map(Persona::getNombre)
					.collect(Collectors.joining(" - ")));
		}

		// también se podría aplicar una condición a cada agrupación como por ejemplo sacar la media de edad por cada
		// pais. Para obtener la media se utiliza el método Collectors.averagingInt, mirar api
		List<Persona> personasConEdadPais = Arrays.asList(new Persona("Jose", 45, "España"),
				new Persona("Mike", 56, "Inglaterra"), new Persona("Luigi", 5, "Italia"),
				new Persona("Pepe", 1, "España"), new Persona("Mario", 30, "Italia"));

		Map<String, Double> mediaEdadPorPais = personasConEdadPais.stream()
				.collect(Collectors.groupingBy(Persona::getPais, Collectors.averagingInt(Persona::getEdad)));
		System.out.println("Media de edad por pais");
		for (String key : mediaEdadPorPais.keySet()) {
			System.out.println(key + ": " + mediaEdadPorPais.get(key));
		}

		// se puede hacer fácilmente un sumatorio
		System.out.println("sumatorio: " + Stream.of(new Integer(3), new Integer(3), new Integer(3))
				.collect(Collectors.summingInt(Integer::intValue)));

		// la media
		IntStream.range(0, 10).boxed().collect(Collectors.averagingInt(Integer::intValue));

		
		
		// ***************** PARTICIONES *****************/

		// se pueden particionar por una condición, devolverá <Map<Boolean, List<Persona>>, ?>.
		// Por ejemplo aquí particionamos por edad, <10 años
		List<Persona> personasConEdad = Arrays.asList(new Persona("Jose", 45), new Persona("Mike", 56),
				new Persona("Luigi", 5), new Persona("Pepe", 1), new Persona("Mario", 30));

		Map<Boolean, List<Persona>> gruposPersonas = personasConEdad.stream()
				.collect(Collectors.partitioningBy(it -> it.getEdad() > 10));

		// las key serán directamente o true o false, no hay más.
		System.out.println("Mayores de 10 años : "
				+ gruposPersonas.get(true).stream().map(Persona::getNombre).collect(Collectors.joining(" - ")));

		System.out.println("Menores de 10 años : "
				+ gruposPersonas.get(false).stream().map(Persona::getNombre).collect(Collectors.joining(" - ")));

		/*
		 * Más ejemplos de la api List<String> list = people.stream().map(Person::getName).collect(Collectors.toList());
		 * 
		 * // Accumulate names into a TreeSet Set<String> set =
		 * people.stream().map(Person::getName).collect(Collectors.toCollection( TreeSet::new));
		 * 
		 * // Convert elements to strings and concatenate them, separated by commas String joined = things.stream()
		 * .map(Object::toString) .collect(Collectors.joining(", "));
		 * 
		 * // Compute sum of salaries of employee int total = employees.stream()
		 * .collect(Collectors.summingInt(Employee::getSalary)));
		 * 
		 * // Group employees by department Map<Department, List<Employee>> byDept = employees.stream()
		 * .collect(Collectors.groupingBy(Employee::getDepartment));
		 * 
		 * // Compute sum of salaries by department Map<Department, Integer> totalByDept = employees.stream()
		 * .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.summingInt(Employee::getSalary)));
		 * 
		 * // Partition students into passing and failing Map<Boolean, List<Student>> passingFailing = students.stream()
		 * .collect(Collectors.partitioningBy(s -> s.getGrade() >= PASS_THRESHOLD));
		 * 
		 */
	}

	/**
	 * PARALLEL STREAMS permiten ejecutar stream en paralelo siempre que sea posible
	 */
	private static void parallelStream() {
		System.out.println("/**************** PRUEBAS SOBRE PARALLEL STREAM");
		// permite ejecutar stream en paralelo siempre que sea posible

		// por ejemplo un filtrado/sumatorio de una lista grande viene al caso
		// ya que no importa el orden de los elementos, con parallelStream() internamente lanza distintos threads
		// que van filtrando/sumando partes la lista. De esta forma si tenemos una maquina con varios cores la tarea
		// puede terminar mucho antes ya que el trabajo esta siendo dividido

		// creamos un array de integers grande, de 100, utilizando el IntStream.range y lo pasamos a List<Integer>
		// Como tenemos un intStream, la manera de crear un Stream<Integer> es utilizando el mapToObj para así obtener
		// el listado final con el collect
		List<Integer> numeros = IntStream.range(0, 100).mapToObj(Integer::new).collect(Collectors.toList());

		// aplicamos un sumatorio, con stream va de forma secuencial, por consola se puede ver
		// NOTA: en cualquier momento se puede utilizar peek. Se suele utilizar para
		// pintar un stream en un momento dado. Recibe un consumer y no afecta al stream, es un consumer. Debe haber una
		// terminal si no no funciona
		System.out.println(numeros.stream().peek(System.out::print).reduce(0, (a, b) -> a + b));
		// por consola 0123456789101112131415161718192021222324252.....

		// si lo ejecutamos con parallel el rendimiento será mejor, tardará menos.
		// por consola 6287633164886532126689671 (se ve en consola que no es secuencial)
		System.out.println(numeros.parallelStream().peek(System.out::print).reduce(0, (a, b) -> a + b));

	}

	/**
	 * COLLECTIONS Y LIST extensiones en las API
	 */
	private static void extensionesCollectionsList() {
		System.out.println("/**************** PRUEBAS SOBRE COLLECTIONS Y LIST");
		// las interfaces pueden ampliar sus métodos simplemente añadiendo métodos default, de esta forma Collection y
		// List disponen de nuevos métodos

		// removeIf. recibe un Predicate que podemos ponerlo como una lambda al ser functionalReference
		// Curiosidad -> si inicializamos la lista así List<String> lista = Arrays.asList("Uno","Dos", "Tres", "Dos") ;
		// no funciona, peta, por ello debe inicializarse por ejemplo con ArrayList.
		List<String> col = new ArrayList<String>();
		/* col.add("Uno"); col.add("Dos"); col.add("Tres"); */
		// o lo que es lo mismo:
		col.addAll(Arrays.asList("Uno", "Dos", "Tres"));

		// borra los que tenga length>3
		col.removeIf(p -> p.length() > 3);
		System.out.println(col); // [Uno, Dos, Dos]

		List<String> lista = Arrays.asList("Uno", "Dos", "Tres", "Dos");
		// replaceAll (UnaryOperator <E> operator)
		lista.replaceAll(it -> it.equals("Dos") ? "2" : it);
		System.out.println(lista); // Consola [Uno, 2, Tres, 2]

		// para pintar los map de personas que vamos a ver por ejemplo podemos crearnos una
		// BiConsumer que pasaremos al forEach
		BiConsumer<String, Persona> pintar = (key, value) -> System.out.println(key + " - " + value);

		// Nos creamos un map de personas Map<String, Persona> cuya key sea el nombre ("nombre1", "nombre2") y el value
		// la persona, podemos poner p->p o Function.identity(), que representará a la persona
		Map<String, Persona> mapPersonas = IntStream.range(0, 5).mapToObj(i -> new Persona("nombre" + i))
				.collect(Collectors.toMap(Persona::getNombre, Function.identity()));

		// default void forEach(BiConsumer<? super K, ? super V> action)
		mapPersonas.forEach(pintar);

		// default void replaceAll( BiFunction<? super K, ? super V, ? extends V> function), añadimos _new a todos los
		// nombres
		mapPersonas.replaceAll((key, oldValue) -> {
			oldValue.setNombre(oldValue.getNombre().concat("_new"));
			return oldValue;
		});
		mapPersonas.forEach(pintar);

		// default V putIfAbsent(K key, V value) si no existe, se mete y si existe, se deja el que estaba, NO SE
		// ACTUALIZA
		System.out.println(mapPersonas.putIfAbsent("nombre1", new Persona("nombre_actualizado")));
		// se queda con el antiguo porque existe la clave

		// ***************** COMPARATORS *****************/

		// se pueden crear fácilmente comparator
		List<Persona> personasConEdadPais = Arrays.asList(new Persona("Jose", 45, "España"),
				new Persona("Mike", 45, "Inglaterra"), new Persona("Luigi Bambino", 30, "Italia"),
				new Persona("Pepe", 1, "España"), new Persona("Mario", 30, "Italia"));

		// compara las personas por edad en orden ascendente
		personasConEdadPais.sort(Comparator.comparingInt(Persona::getEdad));
		System.out.println(personasConEdadPais);
		// compara las personas por edad en orden inverso
		personasConEdadPais.sort(Comparator.comparingInt(Persona::getEdad).reversed());
		System.out.println(personasConEdadPais);

		// se pueden añadir distintos comparators en la clase para que quede más
		// legible, por Edad, por Nombre, por Pais...serán static
		personasConEdadPais.sort(Persona.porEdad());
		personasConEdadPais.sort(Persona.porNombre());

		// se pueden concatenar varios comparators para resolver aquellos que son iguales, por ejemplo
		// ordenamos por edad y si coincide se aplica una segunda ordenación, por nombre
		personasConEdadPais.sort(Persona.porEdad().thenComparing(Persona.porNombre()).reversed());
		System.out.println(personasConEdadPais);
		// [nombre=Jose, pais=España, edad=45],
		// [nombre=Mike, pais=Inglaterra, edad=45],
		// [nombre=Luigi Bambino, pais=Italia, edad=30],
		// [nombre=Mario, pais=Italia, edad=30],
		// [nombre=Pepe, pais=España, edad=1]]

	}
}
