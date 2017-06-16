package es.migueluvieu.model;

import java.util.Comparator;
import java.util.Optional;

public class Persona {
	private String nombre;
	private String apellido;
	private Optional<String> nombre2;
	private String pais;
	private int edad;

	@Override
	public String toString() {
		return "Persona [nombre=" + nombre + ", pais=" + pais + ", edad=" + edad + "]";
	}

	public Persona(String nombre) {
		super();
		this.nombre = nombre;
	}

	public Persona(String nombre, String pais) {
		super();
		this.nombre = nombre;
		this.pais = pais;
	}

	public Persona(String nombre, int edad) {
		super();
		this.nombre = nombre;
		this.edad = edad;
	}

	public Persona(String nombre, int edad, String pais) {
		super();
		this.nombre = nombre;
		this.pais = pais;
		this.edad = edad;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Persona() {
		super();
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public Optional<String> getNombre2() {
		return nombre2;
	}

	public void setNombre2(Optional<String> nombre2) {
		this.nombre2 = nombre2;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	// comparator de persona por edad
	public static Comparator<Persona> porEdad() {
		return Comparator.comparingInt(Persona::getEdad);
	}
    
	public static Comparator<Persona> porNombre() {
		return (p1, p2) -> p1.getNombre().length() - p2.getNombre().length();
	}
}
