import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) throws InterruptedException {

        final String HOST_NAME = "localhost";
        final int PORT_NUM = 6868;
        final String[] commands = {
                "date and time", "uptime", "memory use",
                "netstat", "current users", "running processes"
        };

        int numThreads = commands.length;
        Thread[] threads = new Thread[numThreads];
        double[] turnaroundTimes = new double[numThreads]; // store each TAT in array to do math later

        System.out.println("Connecting to server " + HOST_NAME + " on port " + PORT_NUM);

        // Create a thread for each command
        for (int i = 0; i < numThreads; i++) { // creates a new thread object for each command
            int index = i;
            threads[i] = new Thread(() -> {
                try {
                    double startTime = System.currentTimeMillis();

                    Socket socket = new Socket(HOST_NAME, PORT_NUM);
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                    output.println(commands[index]);

                    double endTime = System.currentTimeMillis();
                    turnaroundTimes[index] = endTime - startTime;

                    socket.close();

                } catch (IOException e) {
                    System.out.println("Command \"" + commands[index] + "\" failed: " + e.getMessage());
                    turnaroundTimes[index] = -1;
                }
            });
            threads[i].start(); // now run the actual thread
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            t.join();
        }

        // Print results
        long totalTime = 0;
        System.out.println("\n===== SUMMARY =====");
        for (int i = 0; i < numThreads; i++) {
            System.out.println("Command \"" + commands[i] + "\" Turn-around Time: " + turnaroundTimes[i] + " ms");
            if (turnaroundTimes[i] > 0)
                totalTime += turnaroundTimes[i];
        }

        double averageTime = totalTime / numThreads;
        System.out.println("Total Turn-around Time: " + totalTime + " ms");
        System.out.println("Average Turn-around Time: " + averageTime + " ms");
    }
}
