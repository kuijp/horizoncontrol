package nl.kuijp.horizoncontrol;

public class VncSender {

   private String vncHost;
   private int vncPort;

   public VncSender(String vncHost, int vncPort) {
      this.vncHost = vncHost;
      this.vncPort = vncPort;
   }

   public void sendKey(final Integer key) throws Exception {
      try(VncSenderConnection con = new VncSenderConnection(vncHost, vncPort)) {
         con.sendKey(key);
      }
   }
}
