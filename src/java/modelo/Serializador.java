package modelo;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class Serializador {
    public static void guardarTicket(Ticket ticket) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("tickets.dat", true))) {
            out.writeObject(ticket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

