import java.net.InetAddress;
import java.util.List;

//classe que contém a info do servidor no RP
public class Source {
    
    private InetAddress source; // endereço do servidor
    private float metric; // metrica para saber qual o melhor servidor
    private List<String> content; // lista de conteudo do servidor 
    private boolean active; // estado do servidor, se se encontra ativo ou não

    public Source(InetAddress source, float metric, List<String> content, boolean active) {
        this.source = source;
        this.metric = metric;
        this.content = content;
        this.active = active;
    }

    public InetAddress getSource() {
        return source;
    }

    public void setSource(InetAddress source) {
        this.source = source;
    }

    public float getMetric() {
        return metric;
    }

    public void setMetric(float metric) {
        this.metric = metric;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
    return "Source{" +
           "source=" + source +
           ", metric=" + metric +
           ", content=" + content +
           ", active=" + active +
           '}';
    }


}
