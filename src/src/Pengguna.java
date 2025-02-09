/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

/**
 *
 * @author ASUS
 */
public class Pengguna {
	private int idPengguna;
	private String namaPengguna;
	private String email;
	private String password;
	private String role; 

	
	public Pengguna(int idPengguna, String namaPengguna, String email, String password, String role) {
		this.idPengguna = idPengguna;
		this.namaPengguna = namaPengguna;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getIdPengguna() {
		return idPengguna;
	}

	public void setIdPengguna(int idPengguna) {
		this.idPengguna = idPengguna;
	}

	public String getNamaPengguna() {
		return namaPengguna;
	}

	public void setNamaPengguna(String namaPengguna) {
		this.namaPengguna = namaPengguna;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
