/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package inpo.ngopilurr;
import java.util.Scanner;
/**
 *
 * @author dwife
 */
public class INPONGOPILURR {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        Scanner input = new Scanner(System.in);

        System.out.print("Masukkan nama: ");
        String nama = input.nextLine();

        System.out.print("Masukkan umur: ");
        int umur = input.nextInt();

        System.out.println("\n=== HASIL ===");
        System.out.println("Nama  : " + nama);
        System.out.println("Umur  : " + umur + " tahun");

        if (umur >= 18) {
            System.out.println("Status: Dewasa");
        } else {
            System.out.println("Status: Belum dewasa");
        }

        input.close();
    }
    
}
