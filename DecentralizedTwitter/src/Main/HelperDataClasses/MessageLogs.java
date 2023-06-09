package Main.HelperDataClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class MessageLogs {

    public class MessageLog {
        private String from_ip;
        private int from_port;
        private String destination_ip;
        private int destination_port;
        private String timeStamp;

        public MessageLog(String from_ip, int from_port, String destination_ip, int destination_port) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String creationTime = dtf.format(LocalDateTime.now());
            this.timeStamp = creationTime;
        }

        public String getTimeStamp() {
            return this.timeStamp;
        }

        

    }

    private final Vector<MessageLog> messages = new Vector<MessageLog>();
    private final String default_ip;
    private final int default_port;

    public MessageLogs(String default_ip, int default_port){
        this.default_ip = default_ip;
        this.default_port = default_port;
    }

    public void addLog(String destination_ip, int destination_port){
        this.messages.add(new MessageLog(default_ip, default_port, destination_ip, destination_port));
    }

    public void addLog(String from_ip, int from_port, String destination_ip, int destination_port){
        this.messages.add(new MessageLog(from_ip, from_port, destination_ip, destination_port));
    }

}
