import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
//import java.util.Map;

public class MRPreceive implements Runnable {

    public static int MAX = 1024;
    public byte[] buffer = new byte[MAX];
    private ONode n;

    public MRPreceive(ONode n){
        this.n = n;
    }

    public  void triagePacket(MRPpacket p, InetAddress sender) throws UnknownHostException, SocketException{
        switch (p.getType()) {
            // pacote do RP para o Server para saber se está ativo e se sim conteúdo
            case "SUUP":
                // dá update à routing table do server
                // envia ao RP um pacote com a lista de conteúdo disponivel
                triageSUUP(p, sender);
                break;
            // pacote do Server para o RP
            case "YUUP":
                //adiciona o servidor às sources
                //se tiver videos novos acrescenta-os aos videos e atribui uma porta.
                triageYUUP(p, sender);
                break;
            case "GETV":
                // server começa a enviar o video pedido
                triageGETV(p, sender);
                break;
            case "STRT":
                //#STRT ONODE#
                //guarda na routing table
                //calcula a latencia desde o ultimo nodo e adiciona ao totaltime
                //reenvia para os vizinhos
                //#STRT RP#
                //guarda na routing table o melhor next hop
                //envia ACKK de volta à source
                triageSTRT(p, sender);                
                break;
            case "STOP":
                //#STOP ONODE#
                //tira da routing table
                //reenvia para os vizinhos
                //#STOP RP#
                //tira da routing table
                triageSTOP(p, sender);
                break;
            case "STTV":
                //#STTV ONODE#
                //testa se tem o video pela porta se tiver só adiciona o source ao nexthops e continua na mesma o startv para cima
                // senao reencaminha o startv para cima e abre o scket com a porta certa
                //#STTV RP#
                //vai buscar a porta do video
                //se ja estiver nos nexthops a porta adiciona o ip da source no nexthop correto
                //senão pede o video ao servidor e abre o socket e mete a source no nexthops correto
                triageSTTV(p, sender);
                break;
            case "STPV":
                //#STPV ONODE#
                //
                //
                //#STPV RP#
                //
                triageSTPV(p, sender);
                break; 
            case "ACKK":
                //#ACKK ONODE#
                //
                //
                //#ACKK RP#
                //
                triageACKK(p, sender);
                break;                                                                
            default:
                System.out.println("Header de pacote desconhecido");
        }
    }

    //retorna latência entre nodos
    public long duration(LocalDateTime dt){
        Duration duration = Duration.between(dt, LocalDateTime.now());
        long mili = duration.toMillis();
        return mili;
    }

    // dá update à routing table do server
    // envia ao RP um pacote com a lista de conteúdo disponivel
    public  void triageSUUP(MRPpacket p, InetAddress sender){
        if(n instanceof Server){
            n.getRt().updateRoutingTable(p.getSource(), sender, duration(p.getTime()) + p.getTotalTime());
            System.out.println(n.getRt().toString());
            Server s = (Server) n;
            MRPpacket mrp = new MRPpacket("YUUP", LocalDateTime.now(), s.getContent());
            Thread MRPsend = new Thread(new MRPsend(n.getMRPsocket(), n.getRt().getTableLine(p.getSource()).getNextHop(), mrp));
            MRPsend.start();
        }
    }

    //adiciona o servidor às sources
    //se tiver videos novos acrescenta-os aos videos e atribui uma porta.
    public  void triageYUUP(MRPpacket p, InetAddress sender) throws SocketException{
        if(n instanceof RP){
            RP rp = (RP) n;
            // acrescenta a source ao RP
            rp.getSources().add(new Source(p.getSource(), duration(p.getTime()), p.getFilenames(), true));
            // para cada video testa se está na lista de videos
            for(String video : p.getFilenames()){
                if(!(rp.getVideosPorts().containsKey(video))){
                    //se n estiver acrescenta e adiciona porta
                    Integer port = rp.getPortCounter();
                    rp.setVideosPorts(video, port);
                    // adiciona o socket aberto à lista de sockets
                    DatagramSocket sock = new DatagramSocket(port);
                    rp.getRTPsockets().put(video,sock);
                    // incrementa a proxima porta a ser utilizada
                    rp.setPortCounter(rp.getPortCounter()+1);
                }
            }
            //System.out.println(rp.getSources().toString());
            //for (Map.Entry<String, Integer> entry : rp.getVideosPorts().entrySet()) {
                //System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            //}
        }
    }

    // server começa a enviar o video pedido
    public  void triageGETV(MRPpacket p, InetAddress sender) throws SocketException{
        if(n instanceof Server){
            DatagramSocket sock = new DatagramSocket(p.getPort());
            n.setRTPsocket(p.getFilenames().get(0), sock);
            Thread VideoForwarder = new Thread(new VideoForwarder(p.getSource(),p.getFilenames().get(0),sock));
            VideoForwarder.start();
        }
    }

    public  void triageSTRT(MRPpacket p, InetAddress sender){
        //STRT ONODE
        // guarda na routing table
        // calcula a latencia desde o ultimo nodo e adiciona ao totaltime
        // reenvia para os vizinhos
        if((!(n instanceof RP)) && (!(n instanceof Server)) && (!(n instanceof Client))){
            n.getRt().updateRoutingTable(p.getSource(), sender, duration(p.getTime()) + p.getTotalTime());
            System.out.println(n.getRt().toString());
            MRPpacket mrp = new MRPpacket("STRT", p.getSource(), p.getTotalTime()+duration(p.getTime()),LocalDateTime.now());
            for(InetAddress neighbour : n.getRt().getNeighbours()){
                if(!neighbour.equals(sender)){
                    Thread MRPsend = new Thread(new MRPsend(n.getMRPsocket(), neighbour, mrp));
                    MRPsend.start();
                }
            }
            

        }
        // STRT RP
        // guarda na routing table o melhor next hop
        // envia ACKK de volta à source
        if(n instanceof RP){
            n.getRt().updateRoutingTable(p.getSource(), sender, duration(p.getTime()) + p.getTotalTime());
            System.out.println(n.getRt().toString());
            RP rp = (RP) n;
            MRPpacket mrp = new MRPpacket("ACKK", null, p.getSource(), 0, LocalDateTime.now(), rp.getVideos(), rp.getPorts());
            Thread MRPsend = new Thread(new MRPsend(n.getMRPsocket(), n.getRt().getTableLine(p.getSource()).getNextHop(), mrp));
            MRPsend.start();
        }
    }

    //TODO P
    public  void triageSTOP(MRPpacket p, InetAddress sender){
        //#STOP ONODE#
        //tira da routing table
        //reenvia para os vizinhos
        //TODO
        if((!(n instanceof RP)) && (!(n instanceof Server)) && (!(n instanceof Client))){

        }
        //#STOP RP#
        //tira da routing table
        //TODO
        if(n instanceof RP){

        }
    }

    public  void triageSTTV(MRPpacket p, InetAddress sender) throws SocketException{
        if (n instanceof RP) {
            //vai buscar a porta do video
            //se ja estiver nos nexthops a porta adiciona o ip da source no nexthop correto
            //senão pede o video ao servidor e abre o socket e mete a source no nexthops correto
            RP rp = (RP) n;
            String video = p.getFilenames().get(0);
            Integer port = rp.getVideosPorts().get(video);
            //se já está a transmitir o video
            if(rp.getRt().getNextHopClients().containsKey(port)){
                rp.getRt().add2NextHops(port, rp.getRt().getTableLine(p.getSource()).getNextHop(), p.getSource());
                System.out.println(rp.getRt().toStringNextHops());
            } else {
                //adicionar na rt nexthops correto
                rp.getRt().add2NextHops(port, rp.getRt().getTableLine(p.getSource()).getNextHop(), p.getSource());
                System.out.println(rp.getRt().toStringNextHops());
                //abrir socket
                //DatagramSocket rtp = new DatagramSocket(port,rp.getMRPsocket().getInetAddress());
                //System.out.println("vou abrir rtp socket na porta:" + port);
                //System.out.println("video: " + video);
                //System.out.println("porta: " + rtp.getLocalPort());
                //rp.setRTPsocket(video, rtp);
                DatagramSocket sock = rp.getRTPsocket(video);
                Thread RTPreceive = new Thread(new RTPreceive(sock, rp.getRt()));
                RTPreceive.start();
                //mandar getv ao servidor
                InetAddress ipServer = rp.getBestServer(video);
                rp.getGetting().put(video,ipServer);
                MRPpacket getV = new MRPpacket("GETV", LocalDateTime.now(),p.getFilenames(),port);
                Thread MRPsend = new Thread(new MRPsend(n.getMRPsocket(), ipServer, getV));
                MRPsend.start();
            }
        } else if (!(n instanceof Server) && (!(n instanceof Client))) {
            //testa se tem o video pela porta se tiver só adiciona o source ao nexthops e continua na mesma o startv para cima
            // senao reencaminha o startv para cima e abre o scket com a porta certa
            String video = p.getFilenames().get(0);
            Integer port = n.getVideosPorts().get(video);
            //se já está a transmitir o video
            if(n.getRt().getNextHopClients().containsKey(port)){
                n.getRt().add2NextHops(port, n.getRt().getTableLine(p.getSource()).getNextHop(), p.getSource());
                System.out.println(n.getRt().toStringNextHops());
                MRPpacket startVPacket = new MRPpacket("STTV", p.getSource(), p.getDest(), p.getTotalTime()+duration(p.getTime()),LocalDateTime.now(), p.getFilenames());
                //vai buscar o next hop do destino à tabela e envia o pacote
                Thread MRPsend = new Thread(new MRPsend(n.getMRPsocket(), n.getRt().getTableLine(p.getDest()).getNextHop(), startVPacket));
                MRPsend.start();
            } else {
                //adicionar na rt nexthops correto
                n.getRt().add2NextHops(port, n.getRt().getTableLine(p.getSource()).getNextHop(), p.getSource());
                System.out.println(n.getRt().toStringNextHops());
                //abrir socket
                //System.out.println(port);
                DatagramSocket rtp = n.getRTPsocket(video);
                //n.setRTPsocket(video, rtp);
                Thread RTPreceive = new Thread(new RTPreceive(rtp, n.getRt()));
                RTPreceive.start();
                MRPpacket startVPacket = new MRPpacket("STTV", p.getSource(), p.getDest(), p.getTotalTime()+duration(p.getTime()),LocalDateTime.now(), p.getFilenames());
                //vai buscar o next hop do destino à tabela e envia o pacote
                Thread MRPsend = new Thread(new MRPsend(n.getMRPsocket(), n.getRt().getTableLine(p.getDest()).getNextHop(), startVPacket));
                MRPsend.start();
            }
       }              
    }

    //TODO
    public  void triageSTPV(MRPpacket p, InetAddress sender){
        // instance of Superclass
        if((!(n instanceof RP)) && (!(n instanceof Server)) && (!(n instanceof Client)) ){
            // tirar o cliente dos nexthops
            String video = p.getFilenames().get(0);
            Integer port = n.getVideosPorts().get(video);       
            n.getRt().removeNextHops(port,n.getRt().getTableLine(p.getSource()).getNextHop(),p.getSource());
            System.out.println(n.getRt().toStringNextHops());
            //se n ficar lá ninguem fechar port
            /*if(!(n.getRt().getNextHopClients().containsKey(port))) {
                if(n.RTPsockets.containsKey(video)){
                    n.RTPsockets.get(video).close();
                    n.RTPsockets.remove(video);
                }
            }*/
            //mandar o stpv para cima
            MRPpacket stopVPacket = new MRPpacket("STPV", p.getSource(), p.getDest(), p.getTotalTime()+duration(p.getTime()),LocalDateTime.now(), p.getFilenames());
            //vai buscar o next hop do destino à tabela e envia o pacote
            Thread MRPsend = new Thread(new MRPsend(n.getMRPsocket(), n.getRt().getTableLine(p.getDest()).getNextHop(), stopVPacket));
            MRPsend.start();
        }   
        if(n instanceof RP){
            //se n ficar ninguem dizer ao servidor para parar de mandar video
            // tirar o cliente dos nexthops
            String video = p.getFilenames().get(0);
            Integer port = n.getVideosPorts().get(video);       
            n.getRt().removeNextHops(port,n.getRt().getTableLine(p.getSource()).getNextHop(),p.getSource());
            System.out.println(n.getRt().toStringNextHops());
            //se n ficar lá ninguem fechar port
            if(!(n.getRt().getNextHopClients().containsKey(port))) {
                /*if(!(n.RTPsockets.get(video).isClosed())){
                n.RTPsockets.get(video).close(); 
                }
                n.RTPsockets.remove(video);*/
                //manda pacote para o servidor parar de transmitir
                RP rp = (RP) n; 
                InetAddress ipServer = rp.getGetting().get(video);
                MRPpacket stopVPacket = new MRPpacket("STPV", p.getSource(), ipServer, p.getTotalTime()+duration(p.getTime()),LocalDateTime.now(), p.getFilenames());
                //vai buscar o next hop do destino à tabela e envia o pacote
                Thread MRPsend = new Thread(new MRPsend(n.getMRPsocket(), ipServer, stopVPacket));
                MRPsend.start();
            }
        }
        if(n instanceof Server){
            //TODO fecha o tasco daquele socket
            n.getRTPsockets().get(p.getFilenames().get(0)).close();
            n.getRTPsockets().remove(p.getFilenames().get(0));
        }
    }

    public  void triageACKK(MRPpacket p, InetAddress sender){
        // instance of Superclass
        if((!(n instanceof RP)) && (!(n instanceof Server)) && (!(n instanceof Client)) ){
            if(!(n.getRt().getRoutingTable().containsKey(p.getSource()))){
            n.getRt().updateRoutingTable(p.getSource(), sender, duration(p.getTime()) + p.getTotalTime());
            System.out.println(n.getRt().toString());
            }
            for(int i = 0; i< p.getFilenames().size();i++){
                String video = p.getFilenames().get(i);
                Integer port = p.getPorts().get(i);
                n.videos.put(video, port);
                try {// abre sockets dos videos
                if(!(n.RTPsockets.containsKey(video))){
                    DatagramSocket sock = new DatagramSocket(port);
                    n.RTPsockets.put(video, sock);
                }
                } catch (SocketException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(n.getRt().getRoutingTable().containsKey(p.getDest())){
                MRPpacket mrp = new MRPpacket("ACKK", p.getSource(),p.getDest() ,p.getTotalTime()+duration(p.getTime()), LocalDateTime.now(), p.getFilenames(), p.getPorts());
                Thread MRPsend = new Thread(new MRPsend(n.getMRPsocket(), n.getRt().getTableLine(p.getDest()).getNextHop(), mrp));
                MRPsend.start();
            }

        }
        // instance of client
        if( n instanceof Client ){
            n.getRt().updateRoutingTable(p.getSource(), sender, duration(p.getTime()) + p.getTotalTime());
            System.out.println(n.getRt().toString());
            Client c = (Client) n;
            c.setRp(p.getSource());

            for(int i = 0; i< p.getFilenames().size();i++){
                c.videos.put(p.getFilenames().get(i), p.getPorts().get(i));
            }
            //atualizar jlist
            for(String s : c.videos.keySet()){
                if(!(c.model.contains(s))){
                    c.model.addElement(s);
                }
            }
            //System.out.println("chegas aqui?");
            c.f.validate();
            c.f.repaint();
        }     
    }

    @Override
	public  void run() {
        MRPpacket mrpPacket = new MRPpacket();
        try{
            while(true){
                DatagramPacket packet = new DatagramPacket(buffer,MAX);

                n.getMRPsocket().receive(packet);
                try {
                    mrpPacket = mrpPacket.fromBytes(packet.getData());
                    if(mrpPacket.getSource() == null){
                        mrpPacket.setSource(packet.getAddress());
                    }

                    System.out.println("S:" + packet.getAddress().getHostAddress() + " " + mrpPacket.toString());

                    // Verificar os tipos e efetuar as ações necessárias para cada um deles
                    triagePacket(mrpPacket, packet.getAddress());
                    
                 } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
	}
}