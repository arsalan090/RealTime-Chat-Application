package group.chatting.application;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server implements Runnable {

    Socket socket;

    public static class MyLinkedList<T> {
        Node<T> head;

        public void add(T data) {
            Node<T> newNode = new Node<>(data);
            if (head == null) {
                head = newNode;
            } else {
                Node<T> temp = head;
                while (temp.next != null) {
                    temp = temp.next;
                }
                temp.next = newNode;
            }
        }

        private static class Node<T> {
            T data;
            Node<T> next;

            Node(T data) {
                this.data = data;
            }
        }
    }

    public static class MyQueue<T> {
        private Node<T> front;
        private Node<T> rear;

        public void offer(T data) {
            Node<T> newNode = new Node<>(data);
            if (rear == null) {
                front = rear = newNode;
            } else {
                rear.next = newNode;
                rear = newNode;
            }
        }

        public T poll() {
            if (front == null) {
                return null;
            }
            T data = front.data;
            front = front.next;
            if (front == null) {
                rear = null;
            }
            return data;
        }

        private static class Node<T> {
            T data;
            Node<T> next;

            Node(T data) {
                this.data = data;
            }
        }
    }

    public static MyLinkedList<BufferedWriter> clientWriters = new MyLinkedList<>();
     public static MyQueue<String> messageQueue = new MyQueue<>();

    public Server(Socket socket) {
        try {
            this.socket = socket;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            clientWriters.add(writer);

            while (true) {
                String data = reader.readLine().trim();
                System.out.println("Received " + data);

                messageQueue.offer(data);

                broadcastMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage() {
        String message = messageQueue.poll();
        if (message != null) {
            MyLinkedList.Node<BufferedWriter> temp = clientWriters.head;
            while (temp != null) {
                try {
                    temp.data.write(message);
                    temp.data.write("\r\n");
                    temp.data.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                temp = temp.next;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(2003);
        while (true) {
            Socket socket = serverSocket.accept();
            Server server = new Server(socket);
            Thread thread = new Thread(server);
            thread.start();
        }
    }
}
