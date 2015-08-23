package socket;

public interface IMobileClient {

	public boolean isConnected();

	public void setHandler(IMessageListener messageHandler);

	public int connect(String host, int port);

	public void sendMessage(Message message);

	public void close();
}
