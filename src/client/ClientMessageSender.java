package client;

import java.io.*;

public class ClientMessageSender {

    public static void send(String message, PrintWriter out, BufferedReader in) throws IOException {
        out.println(message);
        // String response = in.readLine();
        // if(response.equals("received")) {
        //     ChatClient.messages.put("You", message);
        //     return true;
        // } else {
        //     return false;
        // }
    }
    
}
