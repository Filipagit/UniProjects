import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class RTPreceive implements Runnable{

    private DatagramSocket socket; //socket rtp
    private RoutingTable rt; //routing table do nodo

    public static int MAX = 15000; // numero maximo de bytes enviados em cada pacote rtp
    public byte[] buffer = new byte[MAX]; // buffer com os bytes de cada frame em cada pacote rtp

    public RTPreceive(DatagramSocket sock, RoutingTable rt) throws SocketException{
        this.socket = sock;
        this.rt = rt;
    }

    @Override
	public  void run() {

        try{
            while(rt.getNextHopClients().containsKey(socket.getLocalPort())){
               
                DatagramPacket packet = new DatagramPacket(buffer,MAX);
                //recebe pacote rtp
                socket.receive(packet);
                //reenvia o pacote
                Thread RTPsend = new Thread(new RTPsend(socket, rt, packet));
                RTPsend.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
		
	}

}
