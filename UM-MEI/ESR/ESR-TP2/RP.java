import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RP extends ONode{

    private List<Source> sources; //lista de servidores
    private int portCounter; // contador para o numero de porta dos sockets rtp
    private HashMap<String, InetAddress> getting;  

    public HashMap<String, InetAddress> getGetting() {
        return getting;
    }

    public void setGetting(HashMap<String, InetAddress> getting) {
        this.getting = getting;
    }

    public RP(InetAddress ip) throws SocketException{
        super(ip);
        this.sources = new ArrayList<Source>();
        this.portCounter = 3001;
        this.getting = new HashMap<>();  
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public int getPortCounter() {
        return portCounter;
    }

    public void setPortCounter(int portCounter) {
        this.portCounter = portCounter;
    }

    // vai buscar o melhor servidor para pedir o video
    public  InetAddress getBestServer(String video){
        InetAddress ip = null;
        float metric = Float.MAX_VALUE;
        for(Source s : sources){
            if(s.getMetric()< metric){
                ip = s.getSource();
                metric = s.getMetric();
            }
        }
        return ip;
    }

    public static void main(String[] args) throws IOException {

        //args[0] -> ip para o socket
        //args[1] ... [n] ips dos servidores de video  
        if(args.length<2){
            System.out.println("[ERRO] O nodo tem de ter pelo menos o próprio ip e um servidor de video");
            System.exit(0);
        }

        InetAddress ip = InetAddress.getByName(args[0]);
        ONode n = new RP(ip);

        // começar a troca de pacotes na rede
        Thread MRPreceive = new Thread(new MRPreceive(n));
        MRPreceive.start();
        
        // mandar o pacote SUUP aos servers
        for(int i = 1; i < args.length;i++){
        
            ip = InetAddress.getByName(args[i]);
            MRPpacket mrppacket = new MRPpacket("SUUP", LocalDateTime.now());
            Thread MRPsend = new Thread(new MRPsend(n.MRPsocket, ip, mrppacket));
            MRPsend.start();
            
        }      
    }
}