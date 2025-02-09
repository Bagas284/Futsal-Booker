/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

/**
 *
 * @author Bagas Aldianata
 */
public class Outdoor extends Lapangan {
    private String tipe_lapangan = "Rumput Non Sintetis (Outdoor)";

    // Konstruktor menerima idLapangan dan hargaPerJam
    public Outdoor(int idLapangan, String nama_lapangan, String status, double hargaPerJam) {
        super(idLapangan, nama_lapangan, status, hargaPerJam); // Panggil konstruktor superclass
    }

    public void setTipe_lapangan(String tipe_lapangan) {
        this.tipe_lapangan = tipe_lapangan;
    }

    public String getTipe_lapangan() {
        return tipe_lapangan;
    }

    @Override
    public void display() {
        super.display(); // Panggil display dari Lapangan
        System.out.println("Tipe lapangan: " + tipe_lapangan);
    }
}
