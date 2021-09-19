package client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.StringBuilder;

public class ChatClient {
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    public static String accountName;
    public static ArrayList<String> messageAccountNames = new ArrayList<>();
    public static ArrayList<String> messages = new ArrayList<>();
    private static ExecutorService receiverThread = Executors.newSingleThreadExecutor();

    public static void clearScreen() throws IOException, InterruptedException {  
        if (System.getProperty("os.name").contains("Windows")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } else {
            Runtime.getRuntime().exec("clear");
        }
    }  

    public static void startConnection() throws IOException, InterruptedException {
        try {
            clientSocket = new Socket("127.0.0.1", 4444);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch(IOException e) {
            serverError();
        }
    }

    public static String createAccount(String accountName, String pincode) throws IOException {
        out.println("createAccount");
        out.println(accountName);
        out.println(pincode);
        String response = in.readLine();
        return response;
    }

    public static String logIn(String accountName, String pincode) throws IOException {
        out.println("logIn");
        out.println(accountName);
        out.println(pincode);
        String response = in.readLine();
        return response;
    }

    public static void updateConsole() throws IOException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i<messages.size(); i++) {
            stringBuilder.append(messageAccountNames.get(i) + "\n");
            stringBuilder.append(messages.get(i) + "\n\n");
        }
        clearScreen();
        System.out.println(stringBuilder);
        System.out.print("Send: ");

    }

    public static void serverError() throws IOException, InterruptedException {
        clearScreen();
        System.out.println("Can't connect to the server, shutting down...");
        System.exit(0);
        stopConnection();
    }

    public static void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        startConnection();
        boolean success = false;
        while(!success) {
            clearScreen();
            System.out.println("Do you want to log in or sign up? ");
            String choice = sc.nextLine();
            if(choice.equals("log in")) {
                clearScreen();
                System.out.println("Log in");
                System.out.print("\nUsername: ");
                accountName = sc.nextLine();
                char[] password = System.console().readPassword("\nPassword: ");
                clearScreen();
                String response = logIn(accountName, String.valueOf(password));
                if(response.equals("success")) {
                    success = true;
                    System.out.println("You have successfully logged in!");
                } else if(response.equals("create?")) {
                    System.out.println("An account is not associated with your username!\nDo you want to create an account with these credentials instead?");
                    choice = sc.nextLine();
                    clearScreen();
                    if(choice.equals("yes")){
                        out.println("yes");
                        response = in.readLine();
                        if(response.equals("success")) {
                            System.out.println("You have successfully created an account!");
                            success = true;
                        } else {System.out.println(response);}
                    }
                } else {System.out.println(response);}
            } else if(choice.equals("sign up")) {
                clearScreen();
                System.out.println("Sign up");
                System.out.print("\nChoose a username: ");
                accountName = sc.nextLine();
                char[] password = System.console().readPassword("\nChoose a password: ");
                clearScreen();
                String response = createAccount(accountName, String.valueOf(password));
                if(response.equals("success")) {
                    success = true;
                    System.out.println("You have successfully created an account!");
                }
                else {System.out.println(response);}
            }
            Thread.sleep(1500);
        }
        ClientMessageReceiver messageReceiver = new ClientMessageReceiver(in);
        receiverThread.execute(messageReceiver);
        while(true) {
            String message = sc.nextLine();
            ClientMessageSender.send(message, out, in);
            // if(!ClientMessageSender.send(message, client.out, client.in)) {
            //     System.out.println("Your message wasn't sent");
            //     Thread.sleep(1000);
            // }
        }
    }
}
