package nl.kuijp.horizoncontrol;

public class KeySender {

   private String vncHost;
   private int vncPort;

   public KeySender(String vncHost, int vncPort) {
      this.vncHost = vncHost;
      this.vncPort = vncPort;
   }

   public void sendKey(final Integer key) throws Exception {
      try(HorizonConnection con = new HorizonConnection(vncHost, vncPort)) {
         con.sendKey(key);
      }
   }
}
