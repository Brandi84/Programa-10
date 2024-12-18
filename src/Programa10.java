import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Programa10 {
    private static final String NOMBRE_ARCHIVO = "registro_contrasenas.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> contrasenas = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(3);

        System.out.println("Bienvenido al validador de contraseñas.");
        System.out.println("Ingrese las contraseñas que desea validar.");
        System.out.println("Escriba 'salir' para terminar:");
        while (true) {
            System.out.print("Ingrese una contraseña: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("salir")) {
                break;
            }
            contrasenas.add(input);
        }
        contrasenas.forEach(contrasena -> executor.execute(() -> {
            boolean esValida = esContrasenaValida(contrasena);
            String resultado = "La contraseña '" + contrasena + "' " +
                    (esValida ? "es válida." : "no cumple con los requisitos.");
            System.out.println(resultado);
            guardarEnArchivo(contrasena, esValida);
        }));

        executor.shutdown();
        System.out.println("Validación en proceso. Por favor, espere...");
        scanner.close();
    }

    private static boolean esContrasenaValida(String contrasena) {
        if (contrasena.length() < 8) return false; // Longitud mínima
        if (!Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(contrasena).find()) return false; // Carácter especial
        if (Pattern.compile("[A-Z]").matcher(contrasena).results().count() < 2) return false; // Mayúsculas
        if (Pattern.compile("[a-z]").matcher(contrasena).results().count() < 3) return false; // Minúsculas
        if (!Pattern.compile("[0-9]").matcher(contrasena).find()) return false; // Número
        return true;
    }

    private static synchronized void guardarEnArchivo(String contrasena, boolean esValida) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO, true))) {
            writer.write("Contraseña: " + contrasena + " - " + (esValida ? "Válida" : "Inválida"));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }
}