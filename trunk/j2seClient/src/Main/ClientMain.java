package Main;

import java.io.IOException;

import socket.IMessageListener;
import socket.Message;
import socket.MobileClient;

public class ClientMain {

    
    public static String mName = "test1";
    public static void main(String[] args) {
        MobileClient.getInstance().connect("127.0.0.1", 12345);
        if (MobileClient.getInstance().isConnected()) {
            Message message = new Message((byte) 1);
            try {
                message.writer().writeUTF(mName);
                message.writer().writeUTF("t");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MobileClient.getInstance().sendMessage(message);
            message.cleanup();
        }

        MobileClient.getInstance().setHandler(new IMessageListener() {

            @Override
            public void onMessage(Message message) {
                try {

                    switch (message.command) {
                        case 1:
                            byte result = message.reader().readByte();
                            switch (result) {
                                case 1:
                                    System.out.println("Dang nhap thanh cong");
                                    Message m = new Message((byte) 8);
                                    m.writer().writeByte(1);

                                    MobileClient.getInstance().sendMessage(m);
                                    m.cleanup();
                                    break;
                                case 2:
                                    MobileClient.getInstance().close();
                                    System.out.println("Da dang nhap roi");
                                    break;
                                case 3:
                                    System.out.println("Sai pass hoac username");
                                    break;
                            }
                            break;
                        case 8:
                            byte gameID = message.reader().readByte();
                            System.out.println("set game type success!!!!!!!!!!!!!! game id: " + gameID); 
                            {
                            Message m = new Message((byte) 3);
                            MobileClient.getInstance().sendMessage(m);
                            m.cleanup();
                        }
                        break;
                        case 3://get board list
                            int total = message.reader().readInt();
                            System.out.println("-----------Get board list------------");
                            for (int i = 0; i < total; i++) {
                                int id = message.reader().readInt();
                                byte max = message.reader().readByte();
                                byte current = message.reader().readByte();
                                System.out.println("board ID: " + id + " - max: " + max + " - current: " + current);
                            }
                            {
                            Message m = new Message((byte) 4);
                            m.writer().writeInt(0);
                            MobileClient.getInstance().sendMessage(m);
                            m.cleanup();
                            }
                            break;
                        case 4:
                        {
                            int board = message.reader().readInt();
                            byte r = message.reader().readByte();
                            
                            System.out.println("Join board : " + board + (r == 1 ? " success." : "fail."));
                            Message m = new Message((byte) 21);
//                            m.writer().writeUTF("Toi la: " + mName);
                            MobileClient.getInstance().sendMessage(m);
                            m.cleanup();
                        }
                            break;
                        case 21://lay maps
                        {
                            byte count = message.reader().readByte();
                            for (int i = 0; i < count; i++) {
                                System.out.println("map ID: " + message.reader().readByte());
                                System.out.println("map Name: " + message.reader().readUTF());
                            }
                            Message m = new Message((byte) 16);
//                            m.writer().writeUTF("Toi la: " + mName);
                            m.writer().writeByte(0);
                            MobileClient.getInstance().sendMessage(m);
                            m.cleanup();
                        }
                            break;
                        case 16:
                        {
                            int row = message.reader().readInt();
                            int col = message.reader().readInt();
                            System.out.println("row: " + row + " ; col: " + col);
                            byte[][] map = new byte[row][col];
                            for (int i = 0; i < row; i++) {
                                for (int j = 0; j < col; j++) {
                                    map[i][j] = message.reader().readByte();
                                    System.out.println("Map allow: " + map[i][j]);
                                }
                            }
                        }
                            break;
                        case 7://exit board.
                        {
                            
                        }
                            break;
                        case 9:
                            String hehe = message.reader().readUTF();
                            System.out.println("Chat in board: " + hehe);
                            break;
                        case 10://on set user info
                            System.out.println("user id: " + message.reader().readInt());
                            System.out.println("user name: " + message.reader().readUTF());
                            System.out.println("user money: " + message.reader().readLong());
                            System.out.println("user level: " + message.reader().readInt());
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisconnected() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onConnectionFail() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onConnectOK() {
                // TODO Auto-generated method stub
            }
        });
    }
}
