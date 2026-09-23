package br.ucsal.chat.implement;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import br.ucsal.chat.interfaces.ChatClientInterface;
import br.ucsal.chat.interfaces.ChatServerInterface;

/**
 * Implementação do Cliente de Chat RMI.
 * Atua como cliente (envia dados ao servidor) e como objeto remoto (recebe callbacks).
 */
public class ChatClientImpl extends UnicastRemoteObject implements ChatClientInterface {

    public ChatClientImpl() throws RemoteException {
        super();
    }

    /**
     * Método de Callback: O servidor chama este método para entregar uma mensagem.
     */
    @Override
    public void receberMensagem(String mensagem) throws RemoteException {
        System.out.println(mensagem);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("=== CLIENTE DE CHAT RMI ===");
            System.out.print("Digite seu nome / apelido: ");
            String nome = scanner.nextLine().trim();

            System.out.print("Digite o IP do servidor (pressione ENTER para 'localhost'): ");
            String ipServidor = scanner.nextLine().trim();

            if (ipServidor.isEmpty()) {
                ipServidor = "localhost";
            }

            System.out.println("Conectando ao servidor em " + ipServidor + ":1099...");

            // 1. Obtém a referência do RMI Registry no IP e porta informados
            Registry registry = LocateRegistry.getRegistry(ipServidor, 1099);

            // 2. Localiza o serviço do servidor registrado sob o nome "ChatService"
            ChatServerInterface servidor = (ChatServerInterface) registry.lookup("ChatService");

            // 3. Instancia o próprio cliente como objeto remoto para receber callbacks
            ChatClientImpl cliente = new ChatClientImpl();

            // 4. Registra este cliente no servidor
            servidor.registrarCliente(cliente, nome);

            System.out.println("\n--- Conectado com sucesso ao Chat! ---");
            System.out.println("Digite suas mensagens e pressione ENTER. Para sair, digite 'sair'.\n");

            // 5. Loop para leitura de dados digitados no terminal
            while (true) {
                String texto = scanner.nextLine();
                if (texto.equalsIgnoreCase("sair")) {
                    servidor.removerCliente(cliente, nome);
                    System.out.println("Você saiu do chat.");
                    System.exit(0);
                } else if (!texto.trim().isEmpty()) {
                    servidor.enviarMensagem(nome, texto);
                }
            }

        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}