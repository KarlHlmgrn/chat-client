package client;

import java.io.*;

public class ClientMessageReceiver implements Runnable {
    private BufferedReader in;

    public ClientMessageReceiver(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        while(true) {
            try {
                String accountName = in.readLine();
                String message = in.readLine();
                // if(accountName.equals("heartbeat")) {
                //     if(System.currentTimeMillis() - Long.valueOf(message) > 10000) {
                //         ChatClient.messages.put("Ping is high!", String.valueOf(System.currentTimeMillis() - Long.valueOf(message)) + "ms");
                //         ChatClient.updateConsole();
                //     }
                // } else {
                ChatClient.messageAccountNames.add(accountName);
                ChatClient.messages.add(message);
                ChatClient.updateConsole();
                // }
            } catch (IOException e) {
                try {
                    ChatClient.serverError();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}
