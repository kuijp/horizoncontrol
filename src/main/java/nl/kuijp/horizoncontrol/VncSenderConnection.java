package nl.kuijp.horizoncontrol;

import java.io.Closeable;
import java.io.IOException;

class VncSenderConnection implements Closeable {

   private RfbProto rfb;

   VncSenderConnection(String host, int port) throws Exception {
      rfb = new RfbProto(host, port);
   }

   public void sendKey(final Integer key) throws Exception {
      rfb.writeKeyDown(key);
      rfb.writeKeyUp(key);
      rfb.writeBuffer();
   }

   public void close() throws IOException {
      rfb.close();
   }
}
