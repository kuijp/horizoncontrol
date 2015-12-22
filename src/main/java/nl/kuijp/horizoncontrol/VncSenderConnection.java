package nl.kuijp.horizoncontrol;

import java.io.Closeable;
import java.io.IOException;

public class VncSenderConnection implements Closeable {

   RfbProto rfb;
   String host = "localhost";
   int port = 5900;

   public VncSenderConnection(String host, int port) {
      this.host = host;
      this.port = port;
   }

   public void open() throws Exception {
      connectAndAuthenticate();
      sendInit();
   }

   public void close() throws IOException {
         sendClose();
   }

   public void sendKey(final Integer key) throws Exception {
      rfb.eventBufLen = 0;
      rfb.writeKeyEvent(key, true);
      rfb.writeKeyEvent(key, false);
      rfb.os.write(rfb.eventBuf, 0, rfb.eventBufLen);
      rfb.eventBufLen = 0;
   }

   void sendInit() throws IOException {
      rfb.os.write(0);
   }

   void sendClose() throws IOException {
      rfb.os.flush();
      rfb.writeVersionMsg();
      rfb.close();
   }

   void connectAndAuthenticate() throws Exception {
      showConnectionStatus("Connecting to " + host + ", port " + port + "...");
      rfb = new RfbProto(host, port);
      showConnectionStatus("Connected to server");

      rfb.readVersionMsg();
      showConnectionStatus("RFB server supports protocol version " + rfb.serverMajor + "." + rfb.serverMinor);

      rfb.writeVersionMsg();
      showConnectionStatus("Using RFB protocol version " + rfb.clientMajor + "." + rfb.clientMinor);

      int secType = rfb.negotiateSecurity();
      int authType;
      if (secType == RfbProto.SecTypeTight) {
         showConnectionStatus("Enabling TightVNC protocol extensions");
         rfb.setupTunneling();
         authType = rfb.negotiateAuthenticationTight();
      } else {
         authType = secType;
      }

      switch (authType) {
          case RfbProto.AuthNone:
             showConnectionStatus("No authentication needed");
             rfb.authenticateNone();
             break;
          case RfbProto.AuthVNC:
             showConnectionStatus("Performing standard VNC authentication");
             System.err.println("Server requires a password");
             System.exit(-1);
             break;
          default:
             throw new Exception("Unknown authentication scheme " + authType);
      }
   }

   private void showConnectionStatus(String msg) {
      System.out.println(msg);
   }
}
