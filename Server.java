import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;

public class Server extends JFrame{

    //below are the networking classes used to for connecting two networks and the variables to be taken as there objects
    ServerSocket server;
    Socket socket;  //for client side 
    BufferedReader br;   //for reading 
    PrintWriter out;     //for writing

    //declaring the window components
    private JLabel heading = new JLabel("Server Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Italic",Font.PLAIN,20);
    public Server() {  //server class constructor which can be directly get called once the object of the class is created in the main method
        try
        {
            ServerSocket server = new ServerSocket(7777); ////we have to specify the port where we have to connect the serverr and client
            System.out.println("server is ready to accept the connection ");
            System.out.println("Waiting...");
            socket=server.accept();  //here the stream accepted is passed to the BufferReader in the below code
            
            //creating the oobject of the BUfferReader and PrintWriter class
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            //calling start and write function which are declared and defined below
            createGUI();
            handleEvents();
            startReading();
            // startWriting();
        }catch(Exception e){
            e.printStackTrace();  //this gives the exception that has occured 
        }
        }
        private void createGUI(){
            this.setTitle("Server Messanger[END]");
            this.setSize(500,500);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            heading.setFont(font);
            messageInput.setFont(font);
            messageArea.setFont(font);

            heading.setHorizontalAlignment(SwingConstants.CENTER); 
            heading.setBorder( BorderFactory.createEmptyBorder(20,20,20,20));
            heading.setIcon(new ImageIcon("chatappLOgo.jpg"));
            heading.setHorizontalTextPosition(SwingConstants.CENTER);
            heading.setVerticalTextPosition(SwingConstants.BOTTOM);
            messageArea.setEditable(false); 

            this.setLayout(new BorderLayout());

            JScrollPane jScrollPane = new JScrollPane(messageArea);
            jScrollPane.setAutoscrolls(true);
            this.add(heading, BorderLayout.NORTH);
            this.add(jScrollPane, BorderLayout.CENTER);
            this.add(messageInput, BorderLayout.SOUTH);

            this.setVisible(true);
        }
        private void handleEvents(){
            messageInput.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                   
                    if(e.getKeyCode()==10){  //this key code belongs to enter key when we tab enter
                        // watch on 28th minute of this video for more details
                        String contentToSend = messageInput.getText();
                        messageArea.append("Me:"+contentToSend+"\n");
                        out.println(contentToSend);
                        out.flush();
                        messageInput.setText("");
                        messageInput.requestFocus();
                     }
                }
                
            });
        }
        
        public void startReading(){
            //thread for reading
            Runnable r1 = new Runnable() {
                public void run() {
                    System.out.println("Reading Started");
                    try {
                        while(!socket.isClosed()) {
                             
                             String msg = br.readLine();
                            if(msg.equals("Exit")){
                            System.out.println("Client Terminated the chat");
                            JOptionPane.showMessageDialog(null,"Client Terminated the chat" );
                            messageInput.setEnabled(false);
                            socket.close();
                            break;
                        }
                        //System.out.println("Client :"+msg );
                        messageArea.append("Server :"+msg +"\n");
                    
                    }
                    } catch (Exception e) {
                        // e.printStackTrace();
                        System.out.println("Connection closed");
                        }
                                   
                }
            };
            new Thread(r1).start();  //creating object of Thread class and passing the 
                                     //runnable class reference to it
        }
        public void startWriting(){
            //thread for writing
            Runnable r2 = new Runnable() {
                public void run() {
                    System.out.println("Writer started");
                    try {
                        while(!socket.isClosed()){
                            BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                            String Content= br1.readLine();  
                            out.println(Content);  //out is the instance / variable of BufferReader class
                            out.flush();
                            if(Content.equals("Exit")){
                                socket.close();
                                break;
    
                            }
                        }
                    } catch (Exception e) {
                        // e.printStackTrace();
                        System.out.println("Connection Closed");

                    }
                }
            };
            new Thread(r2).start();

        }

    
    public static void main(String[] args) {
        System.out.println("This is the server side..server started");
        new Server();

    }
}