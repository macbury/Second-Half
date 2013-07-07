package com.macbury.secondhalf.p2p;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.macbury.secondhalf.App;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ShardClient extends DefaultHandler implements Runnable {
  public static final String SERVERIP     = "5.35.245.219";
  public static final int SERVERPORT      = 50000;
  private static final String TAG         = "ShardClient";
  private static final String SESSION_TAG = "session";
  static final String WIFILOCK            = "OPTION_PERM_WIFILOCK";
  
  private WifiManager.WifiLock wifilock;
  private Socket socket;
  private PrintWriter out;
  private Thread connectionThread;
  private ShardClientInterface delegate;
  private Node currentNode;
  private boolean mRun;
  private XMLReader parser;
  private Context mContext;
  
  public ShardClient(Context ctx) {
    mContext = ctx;
  }
  
  public void connect() {
    if (connectionThread != null) {
      throw new RuntimeException("Already connected!");
    }
    acquireWifiLock(mContext);
    connectionThread = new Thread(this);
    connectionThread.start();
  }
  
  public void send(Node node) {
    send(NodeTransformer.nodeToXml(node));
  }
  
  public void send(String s) {
    Log.v(TAG, "Sending: "+ s);
    out.println(s);
    out.flush();
  }
  
  public void disconnect() {
    mRun = false;
    send("</session>");
  }
  
  @Override
  public void run() {
    mRun = true;
    
    try {
      Log.i(TAG, "Connecting: " + SERVERIP);
      InetAddress serverAddr = InetAddress.getByName(SERVERIP);
      try {
        this.socket = new Socket(serverAddr, SERVERPORT);
        out         = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        
        SAXParserFactory saxPF  = SAXParserFactory.newInstance();
        SAXParser saxP          = saxPF.newSAXParser();
        this.parser             = saxP.getXMLReader();
        
        parser.setContentHandler(this);
        delegate.onConnect();
        parser.parse(new InputSource(socket.getInputStream()));
      } catch (IOException e) {
        mRun = false;
      } catch (ParserConfigurationException e) {
        mRun = false;
        e.printStackTrace();
      } catch (SAXException e) {
        mRun = false;
        e.printStackTrace();
      } finally {
        mRun = false;
        if (socket != null) { socket.close(); }
      }
    } catch (UnknownHostException e) {
      mRun = false;
    } catch (IOException e) {
      mRun = false;
    }
    
    Log.i(TAG, "Disconnected...");
    mRun = false;
    connectionThread = null;
    releaseWifilock();
    delegate.onDisconnect();
  }
  
  @Override
  public void endDocument() throws SAXException {
    mRun = false;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    Node node = null;
    
    if (qName == SESSION_TAG) {
      node = null;
    } else if (qName.equals(Action.ACTION_TAG)) {
      Action action = new Action();
      action.setId(attributes.getValue("id"));
      action.setType(attributes.getValue("type"));
      node = action;
    } else if (qName.equals(Response.TAG)) {
      Response response = new Response();
      response.setId(attributes.getValue("id"));
      response.setForType(attributes.getValue("for"));
      response.setStatus(attributes.getValue("status"));
      node = response;
    } else {
      node = new Node(qName);
    }
    
    if (currentNode != null) {
      currentNode.addChild(node);
    }
    
    currentNode = node;
  }
  
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (currentNode != null) {
      currentNode.setValue(currentNode.getValue() + new String(ch, start, length));
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName == SESSION_TAG) {
      Log.i(TAG, "Recived session tag, closing connection");
      try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
    } else if (currentNode != null) {
      if (currentNode.getParent() != null) {
        currentNode = currentNode.getParent();
      } else if (currentNode != null) {
        if (Response.class.isInstance(currentNode)) {
          delegate.onResponse((Response) currentNode);
        } else if (Action.class.isInstance(currentNode)) {
          delegate.onAction((Action)currentNode);
        }
        currentNode = null;
      }
    }
  }
  
  public ShardClientInterface getDelegate() {
    return delegate;
  }

  public void setDelegate(ShardClientInterface delegate) {
    this.delegate = delegate;
  }

  public void acquireWifiLock(Context ctx) {
    WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    releaseWifilock();
    wifilock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, WIFILOCK);
    wifilock.setReferenceCounted(true);
    wifilock.acquire();
    Log.d(TAG, "WifiLock " + WIFILOCK + " aquired (FULL_MODE)");
    Log.d(TAG, "Checking if Wifilock is held:" + wifilock.isHeld()); 
  }
  
  public void releaseWifilock() {
    Log.d(TAG, "releaseWifilock called");
    if ((wifilock != null) && (wifilock.isHeld()))
    {
      wifilock.release();
      Log.d(TAG, "Wifilock " + WIFILOCK + " released");
    }
  }
  
  public boolean holdsWifiLock() {
    Log.d(TAG, "holdsWifilock called");
    if (wifilock != null) {
      return (wifilock.isHeld());
    }
    return false;
  }
  
  public interface ShardClientInterface {
    public void onResponse(Response response);
    public void onAction(Action action);
    public void onDisconnect();
    public void onConnect();
  }
}
