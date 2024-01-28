import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

/*
+========+===============+==============+=======================================================+
| Método |    Origem     |   Destino    |                       Conteúdo                        |
+========+===============+==============+=======================================================+
| SUUP   | RP            | Server       | type, time                                            |
+--------+---------------+--------------+-------------------------------------------------------+
| YUUP   | Server        | RP           | type, time, filenames                                 |
+--------+---------------+--------------+-------------------------------------------------------+
| GETV   | RP            | Server       | type, time, filenames, port                           |
+--------+---------------+--------------+-------------------------------------------------------+
| STRT   | Client, ONode | vizinhos     | type, source, totaltime, time                         |
+--------+---------------+--------------+-------------------------------------------------------+
| STOP   | Client, ONode | RP           | type, source, dest                                    |
+--------+---------------+--------------+-------------------------------------------------------+
| STTV   | Client        | RP           | type, source, dest, totaltime, time, filenames        |
+--------+---------------+--------------+-------------------------------------------------------+
| STPV   | Client        | RP           | type, source, dest, totaltime, time, filenames        |
+--------+---------------+--------------+-------------------------------------------------------+
| ACKKK  | RP            | Client       | type, source, dest, totaltime, time, filenames, ports |
+--------+---------------+--------------+-------------------------------------------------------+
| GETI   | ONode         | Bootstrapper | type                                                  |
+--------+---------------+--------------+-------------------------------------------------------+
*/

public class MRPpacket implements Serializable{

    private String type;
    private InetAddress source;
    private InetAddress dest;
    private long totalTime;
    private LocalDateTime time;
    private List<String> filenames;
    private List<Integer> ports;
    private int port;

    //construtor vazio
    public MRPpacket() {}
    
    //construtor SUUP
    public MRPpacket(String type, LocalDateTime time) {
        this.type = type;     
        this.time = time;
    }

    //construtor YUUP
    public MRPpacket(String type, LocalDateTime time, List<String> filenames) {
        this.type = type;
        this.time = time;
        this.filenames = filenames;
    }   

    //construtor GETV
    public MRPpacket(String type, LocalDateTime time, List<String> filenames, int port) {
        this.type = type;
        this.time = time;
        this.filenames = filenames;
        this.port=port;
    }    

    //constructor STRT
    public MRPpacket(String type, InetAddress source, long totalTime, LocalDateTime time) {
        this.type = type;
        this.source = source;
        this.totalTime = totalTime;
        this.time = time;
	}
    //Cconstrutor STOP
    public MRPpacket(String type, InetAddress source, InetAddress dest) {
        this.type = type;
        this.source = source;
        this.dest = dest;
	}

    //construtor STTV, STPV
    public MRPpacket(String type, InetAddress source, InetAddress dest, long totalTime, LocalDateTime time, List<String> filenames) {
        this.type = type;
        this.source = source;
        this.dest = dest;
        this.totalTime = totalTime;
        this.time = time;
        this.filenames = filenames;
    }
    
    //construtor ACKK
    public MRPpacket(String type, InetAddress source, InetAddress dest, long totalTime, LocalDateTime time, List<String> filenames, List<Integer> ports) {
        this.type = type;
        this.source = source;
        this.dest = dest;
        this.totalTime = totalTime;
        this.time = time;
        this.filenames = filenames;
        this.ports = ports;
    }  
    
    //construtor GETI
    public MRPpacket(String type) {
        this.type = type;
    }   

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public InetAddress getSource() {
        return source;
    }

    public void setSource(InetAddress source) {
        this.source = source;
    }


    public InetAddress getDest() {
        return dest;
    }

    public void setDest(InetAddress dest) {
        this.dest = dest;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public List<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<Integer> getPorts() {
        return ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

	// Converte o pacote numa stream de bytes 
    public byte[] convertToBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(this);
        objectOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    // Reconstroi o pacote a partir de uma stream de bytes
    public MRPpacket fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return (MRPpacket) objectInputStream.readObject();
    }

    @Override
    public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[" + type + "] ");
    sb.append(source != null ? source.getHostAddress() + " " : " ");
    sb.append(dest != null ? dest.getHostAddress() + " " : " ");
    sb.append(totalTime != 0 ? totalTime + " " : " ");
    sb.append(time != null ? time + " " : " ");
    if(filenames != null){
        sb.append("files: ");
        for(String f : filenames){
            sb.append(f + " ");
        }
    }
    sb.append(port != 0 ? port + " " : " ");
    sb.append("ports: ");
    if(ports != null){
        for(int p : ports){
        sb.append(p + " ");
        }
    }
    return sb.toString();
    }
    

  
} 