import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLI {

    public void run() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        while (true) {
            System.out.println("Ingrese un comando:");
            input = reader.readLine();

            switch (input) {
                case "register":
                    // Pendiente llamada a AccountManager para registrar
                    break;
                case "login":
                    // Pendiente llamada  a AccountManager para iniciar sesión
                    break;
                case "logout":
                    // Pendiente llamada a AccountManager para cerrar sesión
                    break;
                case "delete":
                    // Pendieente llamada a AccountManager para eliminar cuenta
                    break;
                case "contacts":
                    // Pendiente llamada a CommunicationManager para mostrar contactos
                    break;
                case "exit":
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Comando desconocido.");
            }
        }
    }
}
