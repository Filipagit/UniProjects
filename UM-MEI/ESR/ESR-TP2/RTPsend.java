import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.stream.Collectors;

public class RTPsend implements Runnable{

    private DatagramSocket socket; // socket rtp
    private RoutingTable rt; // routing table do nodo
    private DatagramPacket p; // pacote a enviar

    public RTPsend(DatagramSocket socket, RoutingTable rt, DatagramPacket p) throws SocketException{
        this.socket = socket;
        this.rt = rt;
        this.p =p;
    }

    public  void sendRTPNextHop(DatagramPacket p) throws IOException, ClassNotFoundException{
        //lista de next hops para 1 certo vídeo/port
        List<InetAddress> ips = rt.getNextHopClients().get(p.getPort()).keySet().stream().collect(Collectors.toList());
        // envio do pacote para os next hops
        //System.out.println("A lista tem size: " + ips.size());
        for(InetAddress ip : ips){
            //System.out.println("|" + ip + "|" + socket.getLocalPort() + "|");
            p.setAddress(ip);
            socket.send(p);
        }
    }

    @Override
	public  void run() {
            try {
                sendRTPNextHop(p);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
	}

}