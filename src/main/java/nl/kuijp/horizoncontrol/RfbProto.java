package nl.kuijp.horizoncontrol;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

class RfbProto {

    OutputStream os;
    int serverMajor, serverMinor;
    int clientMajor, clientMinor;
    byte[] eventBuf = new byte[72];
    int eventBufLen;

    // Security types
    final static int SecTypeInvalid = 0, SecTypeNone = 1, SecTypeVncAuth = 2,
            SecTypeTight = 16;

    // Supported authentication types
    final static int AuthNone = 1, AuthVNC = 2;

    private final static String versionMsg_3_3 = "RFB 003.003\n",
            versionMsg_3_7 = "RFB 003.007\n", versionMsg_3_8 = "RFB 003.008\n";

    // Vendor signatures: standard VNC/RealVNC, TridiaVNC, and TightVNC
    private final static String StandardVendor = "STDV";

    // Supported tunneling types
    private final static int NoTunneling = 0;
    private final static String SigAuthNone = "NOAUTH__", SigAuthVNC = "VNCAUTH_";

    // VNC authentication results
    private final static int VncAuthOK = 0, VncAuthFailed = 1, VncAuthTooMany = 2;

    // Standard client-to-server messages
    private final static int KeyboardEvent = 4;

    private String host;
    private int port;
    private Socket sock;
    private SessionRecorder rec;
    private DataInputStream is;
    private CapsContainer tunnelCaps, authCaps;

    public RfbProto(String h, int p) throws IOException {
        host = h;
        port = p;

        sock = new Socket(host, port);
        is = new DataInputStream(new BufferedInputStream(sock.getInputStream(),
                16384));
        os = sock.getOutputStream();
    }

    public synchronized void close() {
        try {
            sock.close();
            if (rec != null) {
                rec.close();
                rec = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readVersionMsg() throws Exception {
        byte[] b = new byte[12];
        readFully(b);
        if ((b[0] != 'R') || (b[1] != 'F') || (b[2] != 'B') || (b[3] != ' ')
                || (b[4] < '0') || (b[4] > '9') || (b[5] < '0') || (b[5] > '9')
                || (b[6] < '0') || (b[6] > '9') || (b[7] != '.')
                || (b[8] < '0') || (b[8] > '9') || (b[9] < '0') || (b[9] > '9')
                || (b[10] < '0') || (b[10] > '9') || (b[11] != '\n')) {
            throw new Exception("Host " + host + " port " + port
                    + " is not an RFB server");
        }
        serverMajor = (b[4] - '0') * 100 + (b[5] - '0') * 10 + (b[6] - '0');
        serverMinor = (b[8] - '0') * 100 + (b[9] - '0') * 10 + (b[10] - '0');
        if (serverMajor < 3) {
            throw new Exception(
                    "RFB server does not support protocol version 3");
        }
    }

    public void writeVersionMsg() throws IOException {
        clientMajor = 3;
        if (serverMajor > 3 || serverMinor >= 8) {
            clientMinor = 8;
            os.write(versionMsg_3_8.getBytes());
        } else if (serverMinor >= 7) {
            clientMinor = 7;
            os.write(versionMsg_3_7.getBytes());
        } else {
            clientMinor = 3;
            os.write(versionMsg_3_3.getBytes());
        }
        initCapabilities();
    }

    public int negotiateSecurity() throws Exception {
        return (clientMinor >= 7) ? selectSecurityType() : readSecurityType();
    }

    private int readSecurityType() throws Exception {
        int secType = readU32();
        switch (secType) {
            case SecTypeInvalid:
                readConnFailedReason();
                return SecTypeInvalid; // should never be executed
            case SecTypeNone:
            case SecTypeVncAuth:
                return secType;
            default:
                throw new Exception("Unknown security type from RFB server: "
                        + secType);
        }
    }

    private int selectSecurityType() throws Exception {
        int secType = SecTypeInvalid;

        // Read the list of secutiry types.
        int nSecTypes = readU8();
        if (nSecTypes == 0) {
            readConnFailedReason();
            return SecTypeInvalid; // should never be executed
        }
        byte[] secTypes = new byte[nSecTypes];
        readFully(secTypes);

        // Find out if the server supports TightVNC protocol extensions
        for (int i = 0; i < nSecTypes; i++) {
            if (secTypes[i] == SecTypeTight) {
                os.write(SecTypeTight);
                return SecTypeTight;
            }
        }
        for (int i = 0; i < nSecTypes; i++) {
            if (secTypes[i] == SecTypeNone || secTypes[i] == SecTypeVncAuth) {
                secType = secTypes[i];
                break;
            }
        }
        if (secType == SecTypeInvalid) {
            throw new Exception("Server did not offer supported security type");
        } else {
            os.write(secType);
        }
        return secType;
    }

    private void readConnFailedReason() throws Exception {
        int reasonLen = readU32();
        byte[] reason = new byte[reasonLen];
        readFully(reason);
        throw new Exception(new String(reason));
    }

    public void authenticateNone() throws Exception {
        if (clientMinor >= 8)
            readSecurityResult("No authentication");
    }

    private void readSecurityResult(String authType) throws Exception {
        int securityResult = readU32();
        switch (securityResult) {
            case VncAuthOK:
                break;
            case VncAuthFailed:
                if (clientMinor >= 8)
                    readConnFailedReason();
                throw new Exception(authType + ": failed");
            case VncAuthTooMany:
                throw new Exception(authType + ": failed, too many tries");
            default:
                throw new Exception(authType + ": unknown result "
                        + securityResult);
        }
    }

    private void initCapabilities() {
        tunnelCaps = new CapsContainer();
        authCaps = new CapsContainer();
        // Supported authentication methods
        authCaps.add(AuthNone, StandardVendor, SigAuthNone, "No authentication");
        authCaps.add(AuthVNC, StandardVendor, SigAuthVNC,
                "Standard VNC password authentication");
    }

    public void setupTunneling() throws IOException {
        int nTunnelTypes = readU32();
        if (nTunnelTypes != 0) {
            readCapabilityList(tunnelCaps, nTunnelTypes);

            // We don't support tunneling yet.
            writeInt(NoTunneling);
        }
    }

    public int negotiateAuthenticationTight() throws Exception {
        int nAuthTypes = readU32();
        if (nAuthTypes == 0)
            return AuthNone;

        readCapabilityList(authCaps, nAuthTypes);
        for (int i = 0; i < authCaps.numEnabled(); i++) {
            int authType = authCaps.getByOrder(i);
            if (authType == AuthNone || authType == AuthVNC) {
                writeInt(authType);
                return authType;
            }
        }
        throw new Exception("No suitable authentication scheme found");
    }

    private void writeInt(int value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) ((value >> 24) & 0xff);
        b[1] = (byte) ((value >> 16) & 0xff);
        b[2] = (byte) ((value >> 8) & 0xff);
        b[3] = (byte) (value & 0xff);
        os.write(b);
    }

    private void readCapabilityList(CapsContainer caps, int count) throws IOException {
        int code;
        byte[] vendor = new byte[4];
        byte[] name = new byte[8];
        for (int i = 0; i < count; i++) {
            code = readU32();
            readFully(vendor);
            readFully(name);
            caps.enable(new CapabilityInfo(code, vendor, name));
        }
    }

    public void writeKeyEvent(int keysym, boolean down) {
        eventBuf[eventBufLen++] = (byte) KeyboardEvent;
        eventBuf[eventBufLen++] = (byte) (down ? 1 : 0);
        eventBuf[eventBufLen++] = (byte) 0;
        eventBuf[eventBufLen++] = (byte) 0;
        eventBuf[eventBufLen++] = (byte) ((keysym >> 24) & 0xff);
        eventBuf[eventBufLen++] = (byte) ((keysym >> 16) & 0xff);
        eventBuf[eventBufLen++] = (byte) ((keysym >> 8) & 0xff);
        eventBuf[eventBufLen++] = (byte) (keysym & 0xff);
    }

    private void readFully(byte b[]) throws IOException {
        readFully(b, 0, b.length);
    }

    private void readFully(byte b[], int off, int len) throws IOException {
        is.readFully(b, off, len);
    }

    private int readU8() throws IOException {
        return is.readUnsignedByte();
    }

    private int readU32() throws IOException {
        return is.readInt();
    }
}
