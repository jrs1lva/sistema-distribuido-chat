package br.ucsal.chat.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServerInterface extends Remote {

    void registrarCliente(ChatClientInterface cliente, String nome) throws RemoteException;

    void removerCliente(ChatClientInterface cliente, String nome) throws RemoteException;

    void enviarMensagem(String nomeRemetente, String texto) throws RemoteException;
}