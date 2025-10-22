import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws InterruptedException { // throws exception required for threads

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the server hostname: ");
        String hostname = scanner.nextLine();

        int portNumber = getPortNumber(scanner);
        int commandNumber = getClientInputForCommand(scanner);
        int numClientRequests = getNumberOfClientRequests(scanner);

        int numThreads = numClientRequests;
        Thread[] threads = new Thread[numThreads];
        double[] turnaroundTimes = new double[numThreads]; // store each TAT in array
        String[] serverResponses = new String[numClientRequests]; // store server responses

        scanner.close();

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

    /****** Helper Functions *******/

    public static int getPortNumber(Scanner s) {
        System.out.println("Enter the server port number: ");

        Scanner _scanner = s;
        int _portNumber = _scanner.nextInt();

        if (_portNumber < 1025 || _portNumber > 4998) {
            System.out.println("Invalid Port");
            System.exit(1);
        }
        return _portNumber;
    }

    public static int getClientInputForCommand(Scanner s) {
        System.out.println("Select a command: ");
        System.out.println("1: date and time ");
        System.out.println("2: uptime ");
        System.out.println("3: memory use ");
        System.out.println("4: netstat ");
        System.out.println("5: current users ");
        System.out.println("6: running processes ");

        Scanner _scanner = s;
        int _commandNumber = _scanner.nextInt();

        if (_commandNumber < 1 || _commandNumber > 6) {
            System.out.println("Invalid Selection");
            System.exit(1);
        }
        return _commandNumber;
    }

    private static int getNumberOfClientRequests(Scanner s) {
        int[] _numRequestOptions = new int[] { 1, 5, 10, 15, 20, 25 };
        System.out.println("Enter the number of client requets to generate: ");
        System.out.println(Arrays.toString(_numRequestOptions));
        boolean isValidClientRequest = false;

        Scanner _scanner = s;
        int _numClientRequests = _scanner.nextInt();

        for (int option : _numRequestOptions) {
            if (option == _numClientRequests) {
                isValidClientRequest = true;
                break;
            }
        }

        if (!isValidClientRequest) {
            System.out.println("Invalid selection");
            System.exit(1);
        }
        return _numClientRequests;
    }

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
