
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;

public class Client extends JFrame {//A JFrame is a top-level container in Java Swing, 
    //used to create a window for a graphical user interface (GUI).
    //It's part of the Swing library,provides tools to create GUIs
    Socket socket;
    BufferedReader br;   //for reading 
    PrintWriter out;   
    //Declaring Components for the UI window
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea= new JTextArea();  //this packages automatically takes the input from the user
    private JTextField messageInput = new JTextField();//this too
    private Font font = new Font("Roboto",Font.PLAIN,20);
    
    //constructor declarition
    public Client(){
        try {
             System.out.println("Sending request to server");
 
             socket=new Socket("192.168.56.1",7777);
             System.out.println("Connection done.");

             br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
             //calling a GUI function
            createGUI();
            handleEvents();
            //calling start and write function which are declared and defined below
            startReading();
            // startWriting();  //no need for the writing method as the GUI will automatically write to the window
        } catch (Exception e) {
        }
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

private void createGUI(){  //here this refers to the frame
    this.setTitle("Client Messanger[END]");
    this.setSize(500,500);
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //exits the window on cross symbol

    // coding done for Components before making the Window visible 
    heading.setFont(font);
    messageInput.setFont(font);
    messageArea.setFont(font);

    heading.setHorizontalAlignment(SwingConstants.CENTER); 
    heading.setBorder( BorderFactory.createEmptyBorder(20,20,20,20));
    heading.setIcon(new ImageIcon("chatappLOgo.jpg"));
    heading.setHorizontalTextPosition(SwingConstants.CENTER);
    heading.setVerticalTextPosition(SwingConstants.BOTTOM);
    messageArea.setEditable(false); //once message sent cannot be edited 
    //setting the layout for frame
    this.setLayout(new BorderLayout());
    //adding components to the frame
    JScrollPane jScrollPane = new JScrollPane(messageArea);
    jScrollPane.setAutoscrolls(true);
    this.add(heading, BorderLayout.NORTH);
    this.add(jScrollPane, BorderLayout.CENTER);
    this.add(messageInput, BorderLayout.SOUTH);

    this.setVisible(true);  //makes the window visible
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
                        System.out.println("Server Terminated the chat");
                        JOptionPane.showMessageDialog(null,"Server Terminated the chat" );
                        messageInput.setEnabled(false);//to make the input part diasable
                        
                        socket.close();
                        break;
                    }
                    //System.out.println("Server :"+msg );
                    messageArea.append("Server :"+msg +"\n");
                }
                } catch (Exception e) {
                    // e.printStackTrace();
                    System.out.println("Connection Closed");
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
                        
                        //for terminating the due to exit of the server side
                        out.println(Content);  //out is the instance / variable of BufferReader class
                        out.flush();
                        if(Content.equals("Exit")){
                            socket.close();
                            break;

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r2).start();

    }

    public static void main(String[] args) {
        System.out.println("This is the client side , Client server started");
        new Client();
    }
}
