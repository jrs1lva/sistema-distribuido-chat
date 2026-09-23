package br.ucsal.chat.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface Remota do Cliente.
 * Define os métodos que o Servidor pode invocar no Cliente (Callback).
 */
public interface ChatClientInterface extends Remote {

    /**
     * Recebe uma mensagem vinda do servidor e a exibe no console.
     */
    void receberMensagem(String mensagem) throws RemoteException;
}