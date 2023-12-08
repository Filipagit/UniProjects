
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.net.*;

public class VideoForwarder implements Runnable {
    
    private String VideoFileName; //video file to request to the server
    private DatagramSocket RTPsocket; //socket to be used to send and receive UDP packet
    private InetAddress ClientIPAddr; //Client IP address

    // RTP variables
    DatagramPacket senddp; //UDP packet containing the video frames (to send)
    VideoStream video; //VideoStream object used to access video frames
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
    static int FRAME_PERIOD = 41; //Frame period of the video to stream, in ms
    static int VIDEO_LENGTH = 500; //length of the video in frames
    Timer timer; //timer used to send the images at the video frame rate
    byte[] sBuf; //buffer used to store the images to send to the client 
    int imagenb = 0;

    public VideoForwarder(InetAddress ClientIPAddr, String filename, DatagramSocket sock) throws SocketException {
        this.ClientIPAddr = ClientIPAddr;
        this.VideoFileName = filename;
        this.RTPsocket = sock;

        sBuf = new byte[15000]; //allocate memory for the sending buffer
        try {
          video = new VideoStream(VideoFileName); //init the VideoStream object:
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // envia um frame em cada thread
    @Override
    public  void run() {
        // init para a parte do servidor
        timer = new Timer();
        TimerTask videoTask = new TimerTask() {
            public void run() {
                    //if the current image nb is less than the length of the video
                    if (imagenb < VIDEO_LENGTH && !RTPsocket.isClosed()) {
                        //update current imagenb
                        imagenb++;
                        try {
                            //get next frame to send from the video, as well as its size
                            int image_length = video.getnextframe(sBuf);
                            //Builds an RTPpacket object containing the frame
                            RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, imagenb, imagenb*FRAME_PERIOD, sBuf, image_length);
                            //get to total length of the full rtp packet to send
                            int packet_length = rtp_packet.getlength();
                            //retrieve the packet bitstream and store it in an array of bytes
                            byte[] packet_bits = new byte[packet_length];
                            rtp_packet.getpacket(packet_bits);
                            //send the packet as a DatagramPacket over the UDP socket 
                            senddp = new DatagramPacket(packet_bits, packet_length, ClientIPAddr, RTPsocket.getLocalPort());
                            RTPsocket.send(senddp);
                            //System.out.println("Send frame #" + imagenb);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            timer.cancel(); // Cancel the timer on exception
                        }
                        // quando o video chega ao ultimo frame, usa o returnToBeginning() para recomeçar o envio do video
                        if(imagenb==VIDEO_LENGTH){
        
                            imagenb=0;
                            try {
                                video.returnToBeginning();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            
        };
        // Schedule the task for repeated fixed-delay execution
        timer.schedule(videoTask, 0, FRAME_PERIOD);
    }

}
