import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws InterruptedException { // throws exception required for threads

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the server hostname: ");
        String hostname = scanner.nextLine();

        System.out.println("Enter the server port number: ");
        int portNumber = scanner.nextInt();

        System.out.println("Select a command: ");
        System.out.println("1: date and time ");
        System.out.println("2: uptime ");
        System.out.println("3: memory use ");
        System.out.println("4: netstat ");
        System.out.println("5: current users ");
        System.out.println("6: running processes ");
        int commandNumber = scanner.nextInt();

        System.out.println("Enter the number of client requets to generate: ");
        int numClientRequests = scanner.nextInt();

        int numThreads = numClientRequests;
        Thread[] threads = new Thread[numThreads];
        double[] turnaroundTimes = new double[numThreads]; // store each TAT in array
        String[] serverResponses = new String[numClientRequests]; // store server responses

        System.out.println("Connecting to server " + hostname + " on port " + portNumber);

        // Create a thread for each command
        for (int i = 0; i < numClientRequests; i++) { // creates a new thread object for each client request
            int index = i;
            threads[i] = new Thread(() -> {
                try {
                    double startTime = System.currentTimeMillis();

                    Socket socket = new Socket(hostname, portNumber);
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                    output.println(getCommandToSendToServerFromClientInput(commandNumber));

                    // Read server response
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = input.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                    serverResponses[index] = response.toString().trim();

                    double endTime = System.currentTimeMillis();
                    turnaroundTimes[index] = endTime - startTime;

                    socket.close();

                } catch (IOException e) {
                    System.out.println("Command \"" + getCommandToSendToServerFromClientInput(commandNumber)
                            + "\" failed: " + e.getMessage());
                }
            });
            threads[i].start(); // now run the actual thread
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            t.join();
        }

        double totalTurnaroundTime = 0;
        System.out.println("\n===== SUMMARY =====");
        for (int i = 0; i < numClientRequests; i++) {
            System.out.println("Request " + (i + 1) + " Turn-around Time: " + turnaroundTimes[i] + " ms");
            System.out.println("Server Response:\n" + serverResponses[i]);
            System.out.println("-------------------------");
            if (turnaroundTimes[i] > 0) {
                totalTurnaroundTime += turnaroundTimes[i];
            }
        }

        double averageTurnaroundTime = totalTurnaroundTime / numClientRequests;
        System.out.println("Total Turn-around Time: " + totalTurnaroundTime + " ms");
        System.out.println("Average Turn-around Time: " + averageTurnaroundTime + " ms");

    }

    // Map client input to server command
    private static String getCommandToSendToServerFromClientInput(int comanndNum) {
        String output = "Invalid Command";
        switch (comanndNum) {
            case 1 -> output = "date_time";
            case 2 -> output = "uptime";
            case 3 -> output = "memory_use";
            case 4 -> output = "netstat";
            case 5 -> output = "current_users";
            case 6 -> output = "running_processes";
            default -> {
            }
        }
        return output;
    }

}
