package br.ucsal.chat.implement;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import br.ucsal.chat.interfaces.ChatClientInterface;
import br.ucsal.chat.interfaces.ChatServerInterface;

/**
 * Implementação do Servidor de Chat RMI.
 * Gerencia a lista de clientes conectados e realiza o broadcast das mensagens.
 */
public class ChatServerImpl extends UnicastRemoteObject implements ChatServerInterface {

    // Lista thread-safe para armazenar as referências remotas dos clientes conectados
    private final List<ChatClientInterface> clients = new ArrayList<>();

    public ChatServerImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized void registrarCliente(ChatClientInterface client, String username) throws RemoteException {
        clients.add(client);
        broadcastSystemMessage(username + " entrou no chat!");
    }

    @Override
    public synchronized void removerCliente(ChatClientInterface client, String username) throws RemoteException {
        clients.remove(client);
        broadcastSystemMessage(username + " saiu do chat.");
    }

    @Override
    public synchronized void enviarMensagem(String username, String message) throws RemoteException {
        String formattedMessage = username + ": " + message;
        deliverToAll(formattedMessage);
    }

    private void broadcastSystemMessage(String text) {
        deliverToAll("[SISTEMA]: " + text);
    }

    private void deliverToAll(String msg) {
        List<ChatClientInterface> disconnectedClients = new ArrayList<>();

        for (ChatClientInterface client : new ArrayList<>(clients)) {
            try {
                client.receberMensagem(msg);
            } catch (RemoteException e) {
                // Se der erro ao enviar, assume que o cliente caiu e marca para remoção
                disconnectedClients.add(client);
            }
        }

        // Limpa conexões inativas
        clients.removeAll(disconnectedClients);
    }
    
    public static void main(String[] args) {
        try {
            java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.createRegistry(1099);
            ChatServerImpl server = new ChatServerImpl();
            registry.rebind("ChatService", server);
            System.out.println("=== SERVIDOR RMI PRONTO E AGUARDANDO CONEXÕES NA PORTA 1099 ===");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

}