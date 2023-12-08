import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutingTable {
    
    private HashMap<InetAddress, RoutingTableLine> routingTable; // hashmap de linha de routing table para um nodo
    private ArrayList<InetAddress> neighbours; // vizinhos de um nodo
    //1ª key é a port, 2ª key é o next hop e o arraylist são os clientes finais
    private HashMap<Integer, HashMap<InetAddress, ArrayList<InetAddress>>> nextHopClients; // hashmap de clientes finais para um next hop numa dada porta (video)

    public RoutingTable(){
        this.routingTable = new HashMap<>();
        this.neighbours = new ArrayList<>();
        this.nextHopClients = new HashMap<>();
    }

    public RoutingTableLine getTableLine(InetAddress ip){
        return this.routingTable.get(ip);
    }

    public List<InetAddress> getNeighbours(){
        return this.neighbours;
    }


    public HashMap<InetAddress, RoutingTableLine> getRoutingTable() {
        return routingTable;
    }

    public HashMap<Integer, HashMap<InetAddress, ArrayList<InetAddress>>> getNextHopClients() {
        return nextHopClients;
    }

    public void setNextHopClients(HashMap<Integer, HashMap<InetAddress, ArrayList<InetAddress>>> nextHopClients) {
        this.nextHopClients = nextHopClients;
    }

    public void setRoutingTable(HashMap<InetAddress, RoutingTableLine> routingTable) {
        this.routingTable = routingTable;
    }
    
    //adicionar a linha da routing table para um dado nodo 
    public  void setRoutingTableLine(InetAddress ip, RoutingTableLine l){
        if(!this.routingTable.containsKey(ip)){            
            this.routingTable.put(ip, l); 
        }
    }
    
    // adicionar um vizinho ao nodo. insere na routing table e na lista de vizinhos
    public  void addNeighbour(InetAddress neighbour){
        RoutingTableLine line = new RoutingTableLine(neighbour);
        routingTable.put(neighbour, line);
        neighbours.add(neighbour);
    }

    //cria ou dá update a uma linha da routing table para um determinado nodo
    public  void updateRoutingTable(InetAddress ip, InetAddress nextHop, long time){
        if(this.routingTable.containsKey(ip)){
            this.getTableLine(ip).updateRoutingTableLine(nextHop, time);
        }
        else{
            this.routingTable.put(ip,new RoutingTableLine(nextHop,time));
        }
    }

    // adiciona um cliente, num determinado port(video) ao seu next hop
    public  void add2NextHops(Integer port, InetAddress nextHop, InetAddress dest){
        //se a porta ja estiver nos videos a transmitir
        if(this.nextHopClients.containsKey(port)){
            //se o nexthop ja estiver e só faltar meter o destino
            if(this.nextHopClients.get(port).containsKey(nextHop)){
                this.nextHopClients.get(port).get(nextHop).add(dest);
            }
            //se o nexthop ainda não estiver na rota de transmissao
            else{
                //System.out.println("vou acrescentar o nexthop");
                ArrayList<InetAddress> d = new ArrayList<>();
                d.add(dest);
                this.getNextHopClients().get(port).put(nextHop,d);
            }
        }
        else{
            //se ainda não estiver a transmitir o video
            ArrayList<InetAddress> d = new ArrayList<>();
            d.add(dest);
            HashMap<InetAddress,ArrayList<InetAddress>> nh = new HashMap<>();
            nh.put(nextHop,d);
            nextHopClients.put(port,nh);
        }
    }

    // remove um cliente, num determinado port(video) ao seu next hop
    public  void removeNextHops(Integer port, InetAddress nextHop, InetAddress dest){
        //se a porta ja estiver nos videos a transmitir
        if(this.nextHopClients.containsKey(port)){
            //se o nexthop ja estiver e tiver o destino remove o destino
            if(this.nextHopClients.get(port).containsKey(nextHop)){
                this.nextHopClients.get(port).get(nextHop).remove(dest);
                //se a lista de ips do next hop ficar vazio remover o nexthop do hashmap
                if(this.nextHopClients.get(port).get(nextHop).size()==0){
                    this.getNextHopClients().get(port).remove(nextHop);
                }
                //testar se a porta n tiver nexthops retirar porta
                if(this.nextHopClients.get(port).isEmpty()){
                    this.nextHopClients.remove(port);
                }
            }
        }
    }

    public String toStringNextHops() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------------\n");
        for (Map.Entry<Integer, HashMap<InetAddress, ArrayList<InetAddress>>> outerEntry : nextHopClients.entrySet()) {
            sb.append("  ")
            .append(outerEntry.getKey())
            .append(" => {\n");

            for (Map.Entry<InetAddress, ArrayList<InetAddress>> innerEntry : outerEntry.getValue().entrySet()) {
                sb.append("    ")
                .append(innerEntry.getKey())
                .append(" => ")
                .append(innerEntry.getValue())
                .append(",\n");
            }

            sb.append("  },\n");
        }

        sb.append("---------------------------------------");

        return sb.toString();
    }

    //TODO ver melhor este toString, para ficar com toda a info util(routing table, neighbours e next hops)
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("___________________________________________________________\n");
        sb.append(String.format("%-20s %-20s %-10s\n", "Destination IP", "Next Hop", "Time"));
        
        for (Map.Entry<InetAddress, RoutingTableLine> entry : routingTable.entrySet()) {
            InetAddress destinationIp = entry.getKey();
            RoutingTableLine line = entry.getValue();
            InetAddress nextHop = line.getNextHop();
            long time = line.getTime();

            sb.append(String.format("%-20s %-20s %-10d\n", destinationIp.getHostAddress(), nextHop.getHostAddress(), time));
        }
        return sb.toString();
    }
}
