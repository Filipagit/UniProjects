import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MRPsend implements Runnable {

    public RoutingTable routingTable = new RoutingTable();

    private DatagramSocket socket;
    private InetAddress ip;
    private MRPpacket mrppacket;

    public MRPsend(DatagramSocket socket, InetAddress ip, MRPpacket mrppacket) {
        this.socket = socket;
        this.ip = ip;
        this.mrppacket = mrppacket;
    }

    // envio do pacote mrp
    public  void sendMRP(DatagramSocket socket, InetAddress dest, MRPpacket mrpPacket) throws IOException{
        DatagramPacket packet = new DatagramPacket(mrppacket.convertToBytes(), mrppacket.convertToBytes().length);
        packet.setAddress(dest);
        packet.setPort(socket.getLocalPort());
        socket.send(packet);
    }

	@Override
	public  void run() {
            try {
                sendMRP(socket, ip, mrppacket);
                System.out.println("D:" + ip.getHostAddress() + " " + mrppacket.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
	}
}