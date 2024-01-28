import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server extends ONode {
    
    private List<String> content; // lista de videos do servidor

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public Server(InetAddress ip, List<String> content) throws SocketException {
        super(ip);
        this.content = new ArrayList<>(content);
    }

    public static void main(String[] args) throws IOException {
    
        //args[0] -> ip para o socket
        //args[1] ... [n] nome dos videos  
        if(args.length<2){
            System.out.println("[ERRO] O nodo tem de ter pelo menos o próprio ip e um video para partilhar");
            System.exit(0);
        }
        
        InetAddress ip = InetAddress.getByName(args[0]);
        List<String> videos = new ArrayList<>();
        for(int i=1;i<args.length;i++){
            videos.add(args[i]);
        }
        ONode s = new Server(ip, videos);

        // começar a troca de pacotes na rede
        Thread MRPreceive = new Thread(new MRPreceive(s));
        MRPreceive.start();

    }

}
