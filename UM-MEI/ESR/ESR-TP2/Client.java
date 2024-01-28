/* ------------------
   Cliente
   usage: java Cliente
   adaptado dos originais pela equipa docente de ESR (nenhumas garantias)
   colocar o cliente primeiro a correr que o servidor dispara logo!
   ---------------------- */

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.List;

public class Client extends ONode{

  private InetAddress rp; // ip do RP
  private DatagramSocket RTPsocket; // socket rtp   
  
  //GUI
  //---------------------------------------
  JFrame f = new JFrame("Cliente ESRTV");
  JButton playButton = new JButton("Play");
  JButton stopButton = new JButton("Stop");
  JPanel mainPanel = new JPanel();
  JPanel buttonPanel = new JPanel();
  JPanel listPanel = new JPanel();
  JLabel iconLabel = new JLabel();
  DefaultListModel<String> model = new DefaultListModel<>();
  JList<String> videoList = new JList<>(model);
  ImageIcon icon;

  //RTP variables:
  //----------------
  DatagramPacket rcvdp; //UDP packet received from the server (to receive)
  
  Timer cTimer; //timer used to receive data from the UDP socket
  byte[] cBuf; //buffer used to store data received from the server 
  
  //--------------------------
  //Constructor
  //--------------------------
  public Client(InetAddress ip) throws SocketException {
    super(ip);

    //build GUI
    //--------------------------
 
    //Frame
    f.addWindowListener(new WindowAdapter() {
       public void windowClosing(WindowEvent e) {
	        System.exit(0);
       }
    });

    //Buttons
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(playButton);
    buttonPanel.add(stopButton);

    // handlers... (so dois)
    playButton.addActionListener(new playButtonListener());
    stopButton.addActionListener(new tearButtonListener());

    //Image display label
    iconLabel.setIcon(null);
    
    //frame layout
    mainPanel.setLayout(null);
    mainPanel.add(iconLabel);
    mainPanel.add(buttonPanel);

    JScrollPane listScrollPane = new JScrollPane(videoList);

    // Ensure the video display label (assumed to be iconLabel) has a preferred size
    // Set this to the size of your video
    iconLabel.setIcon(null);

    // Set the layout for the buttonPanel to FlowLayout for centering the buttons
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(playButton);
    buttonPanel.add(stopButton);

    // Add action listeners...

    // Create the eastPanel and add the iconLabel and buttonPanel
    JPanel eastPanel = new JPanel(new BorderLayout());
    eastPanel.add(iconLabel, BorderLayout.CENTER); // This will be your video display area
    eastPanel.add(buttonPanel, BorderLayout.SOUTH); // This will contain your buttons, centered and at the bottom

    // Configure the mainPanel
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(eastPanel, BorderLayout.CENTER); // Make sure eastPanel takes up the center space
    mainPanel.add(listScrollPane, BorderLayout.WEST); // Assuming you want the list to be on the left side

    // Add mainPanel to frame and configure frame
    f.getContentPane().add(mainPanel);
    f.setSize(new Dimension(640, 480));
    f.setVisible(true);
    
    //init para a parte do cliente
    //--------------------------
    cTimer = new Timer(20, new clientTimerListener());
    cTimer.setInitialDelay(0);
    cTimer.setCoalesce(true);
    cBuf = new byte[15000]; //allocate enough memory for the buffer used to receive data from the server

   /* try {
      // socket e video
      RTPsocket.setSoTimeout(5000); // setimeout to 5s
    } catch (SocketException e) {
        System.out.println("Cliente: erro no socket: " + e.getMessage());
    }
    TODO codigo para timeout socket
    */

  }

  public InetAddress getRp() {
    return rp;
  }

  public void setRp(InetAddress rp) {
    this.rp = rp;
  }

  public DatagramSocket getRTPsocket() {
    return RTPsocket;
  }

  public void setRTPsocket(DatagramSocket rTPsocket) {
    RTPsocket = rTPsocket;
  }

  //------------------------------------
  //main
  //------------------------------------
  public static void main(String[] args) throws IOException {
      
    //args[0] -> ip para o socket
    //args[1] -> ip do vizinho  
    if(args.length!=2){
      System.out.println("[ERRO] O nodo tem de ter o próprio ip e um vizinho para ligar");
      System.exit(0);
    }

    InetAddress ip = InetAddress.getByName(args[0]);
    Client c = new Client(ip);
  
    // ip vizinho
    InetAddress ipp = InetAddress.getByName(args[1]);
    //adicionados os vizinhos à routing table
    c.getRt().addNeighbour(ipp);

    // começar a troca de pacotes na rede
    Thread MRPreceive = new Thread(new MRPreceive(c));
    MRPreceive.start();

    MRPpacket mrppacket = new MRPpacket("STRT", null, 0, LocalDateTime.now());
    //envio do pacote de ligação à rede
    Thread MRPsend = new Thread(new MRPsend(c.getMRPsocket(), ipp, mrppacket));
    MRPsend.start();

    c.videoList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent evt) {
          if (!evt.getValueIsAdjusting()) {
              String selectedVideo = c.videoList.getSelectedValue();
              if (selectedVideo != null && !selectedVideo.isEmpty()) {
                  // aciona os comandos dos botoes da interface
                  c.playButton.setActionCommand(selectedVideo);
                  c.stopButton.setActionCommand(selectedVideo);
  
                  c.playButton.setEnabled(true);
                  c.stopButton.setEnabled(true);
                  /*
                  Integer port = c.videos.get(selectedVideo);
                  try {
                    c.RTPsocket = new DatagramSocket(port);
                  } catch (SocketException e) {
                    e.printStackTrace();
                  }
                  
                  // video que queremos fazer stream
                  List<String> video = new ArrayList<>();
                  video.add(selectedVideo);
                  //pacote de pedido do video
                  MRPpacket startVPacket = new MRPpacket("STTV", c.getMRPsocket().getInetAddress(),c.getRp(),0,LocalDateTime.now(), video);
                  Thread MRPsend = new Thread(new MRPsend(c.getMRPsocket(), c.getRt().getTableLine(c.getRp()).getNextHop(), startVPacket));
                  MRPsend.start();
                  */
              } else {
                  c.playButton.setEnabled(false);
                  c.stopButton.setEnabled(false);
              }
          }
      }

    });

  }

  //------------------------------------
  //Handler for buttons
  //------------------------------------

  //Handler for Play button
  //-----------------------
  class playButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e){

        Integer port = videos.get(e.getActionCommand());
        try {
          RTPsocket = new DatagramSocket(port);
        } catch (SocketException ee) {
          ee.printStackTrace();
        }
        
        // video que queremos fazer stream
        List<String> video = new ArrayList<>();
        video.add(e.getActionCommand());
        //pacote de pedido do video
        MRPpacket startVPacket = new MRPpacket("STTV", getMRPsocket().getInetAddress(),getRp(),0,LocalDateTime.now(), video);
        Thread MRPsend = new Thread(new MRPsend(getMRPsocket(), getRt().getTableLine(getRp()).getNextHop(), startVPacket));
        MRPsend.start();

        String v = e.getActionCommand();
        System.out.println("Play Button pressed for video " + v); 

        System.out.println("Play Button pressed !"); 
            //start the timers ... 
            cTimer.start();
          }
  }

  //Handler for tear button
  //-----------------------
  class tearButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e){

      String video = e.getActionCommand();
      System.out.println("Teardown Button pressed for video " + video);  

      System.out.println("Teardown Button pressed !");  
      //stop the timer
      cTimer.stop();
      RTPsocket.close();
      List<String> v = new ArrayList<>();
      v.add(video);
      MRPpacket mrppacket = new MRPpacket("STPV", getMRPsocket().getInetAddress(), getRp(),0,LocalDateTime.now(), v);
      Thread MRPsend = new Thread(new MRPsend(getMRPsocket(), getRt().getTableLine(getRp()).getNextHop(), mrppacket));
      MRPsend.start();
      iconLabel.setIcon(null);
    }
  }

  //------------------------------------
  //Handler for timer (para cliente)
  //------------------------------------
  
  class clientTimerListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {

      //Construct a DatagramPacket to receive data from the UDP socket
      rcvdp = new DatagramPacket(cBuf, cBuf.length);

      try{
        //receive the DP from the socket:
        RTPsocket.receive(rcvdp);

          
        //create an RTPpacket object from the DP
        RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

        //print important header fields of the RTP packet received: 
        //System.out.println("Got RTP packet with SeqNum # "+rtp_packet.getsequencenumber()+" TimeStamp "+rtp_packet.gettimestamp()+" ms, of type "+rtp_packet.getpayloadtype());
        
        //print header bitstream:
        rtp_packet.printheader();

        //get the payload bitstream from the RTPpacket object
        int payload_length = rtp_packet.getpayload_length();
        byte [] payload = new byte[payload_length];
        rtp_packet.getpayload(payload);

        //get an Image object from the payload bitstream
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.createImage(payload, 0, payload_length);
        
        //display the image as an ImageIcon object
        icon = new ImageIcon(image);
        iconLabel.setIcon(icon);
      }
      catch (InterruptedIOException iioe){
        System.out.println("Nothing to read"); 
      }
      catch (IOException ioe) {
         System.out.println("Exception caught: "+ioe);
      }
    }
  }
}

