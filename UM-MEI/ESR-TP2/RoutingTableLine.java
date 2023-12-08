import java.net.InetAddress;

public class RoutingTableLine {
    
    private InetAddress nextHop;
    private long time;

    // Construtor RoutingTableLine 
    public RoutingTableLine(InetAddress nextHop, long time){
        this.nextHop = nextHop;
        this.time = time;
    }

        public RoutingTableLine(InetAddress nextHop){
        this.nextHop = nextHop;
        this.time = 0;
    }

    // Getter NextHop
    public InetAddress getNextHop(){
        return this.nextHop;
    }

    // Getter time
    public long getTime(){
        return this.time;
    }

    // Update à linha da tabela se o tempo até ao nodo for menor que o que está na tabela    
    public  void updateRoutingTableLine(InetAddress nextHop, long time){
        if(this.time==0 || (this.time>0 && this.time>time)){
            this.time = time;
            this.nextHop = nextHop;
        }
    }
}
