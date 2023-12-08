import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ONode{

    protected DatagramSocket MRPsocket; //socket mrp
    protected HashMap<String,DatagramSocket> RTPsockets; //hashmap de sockets para os videos em que a key é o nome do video
    protected RoutingTable rt; // routing table do nodo
    protected LinkedHashMap<String,Integer> videos; // hashmap dos videos disponiveis com a respetiva porta

    public ONode(InetAddress ip) throws SocketException{
        this.MRPsocket = new DatagramSocket(3000, ip);
        this.RTPsockets = new HashMap<>();
        this.rt = new RoutingTable();
        this.videos = new LinkedHashMap<>();  
    }

    public RoutingTable getRt() {
        return rt;
    }

    public void setRt(RoutingTable rt) {
        this.rt = rt;
    }

    public DatagramSocket getMRPsocket() {
        return MRPsocket;
    }

    public void setMRPsocket(DatagramSocket mRPsocket) {
        MRPsocket = mRPsocket;
    }

    public DatagramSocket getRTPsocket(String video) {
        return this.RTPsockets.get(video);
    }

    public void setRTPsocket(String video, DatagramSocket RTPsocket) {
        this.RTPsockets.put(video, RTPsocket);
    }

    //TODO tentar mudar esta javardice do linkedhashmap a ir buscar videos e portas por ordem
    public List<String> getVideos(){
        List<String> v = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : videos.entrySet()) {
            v.add(entry.getKey());
        }
        return v;
    }

    //TODO tentar mudar esta javardice do linkedhashmap a ir buscar videos e portas por ordem
    public List<Integer> getPorts(){
        List<Integer> p = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : videos.entrySet()) {
            p.add(entry.getValue());
        }
        return p;
    }

    public LinkedHashMap<String,Integer> getVideosPorts(){
        return this.videos;
    }

    public void setVideosPorts( String video, Integer port){
        videos.put(video, port);
    }

    public HashMap<String, DatagramSocket> getRTPsockets() {
        return RTPsockets;
    }

    public static void main(String[] args) throws IOException {

        //args[0] -> ip para o socket
        //args[1] ... [n] ip dos vizinhos  
        if(args.length<2){
            System.out.println("[ERRO] O nodo tem de ter pelo menos o próprio ip e um vizinho");
            System.exit(0);
        }

        InetAddress ip = InetAddress.getByName(args[0]);
        ONode n = new ONode(ip);

        // começar a troca de pacotes na rede
        Thread MRPreceive = new Thread(new MRPreceive(n));
        MRPreceive.start();
        
        for(int i = 1 ; i < args.length;i++){
            // ip vizinho
            ip = InetAddress.getByName(args[i]);
            //adicionados os vizinhos à routing table
            n.getRt().addNeighbour(ip);

            //MRPpacket mrppacket = new MRPpacket("STRT", null, 0, LocalDateTime.now());
            // envio do pacote
            //Thread MRPsend = new Thread(new MRPsend(n.MRPsocket, ip, mrppacket));
            //MRPsend.start();

        }

    }

}
