package br.feevale;

/**
 * Interface que define as possíveis mensagens a serem enviadas enter servidor e cliente
 * @author Paulo Diovani Gonçalves <paulo@diovani.com>
 *
 */
public interface IMensagemChat {

    public Boolean enviaHandshake(String nomeUsuario);
    public Boolean enviaMensagem(String mensagem);
    public void chamaAtencao();

    public void informaDesconexao();

    public Boolean solicitaEnvioArquivo(String nomeArquivo, Integer tamanho);
    public void aceitaEnvioArquivo(Boolean aceita, Integer porta);

    public Boolean solicitaEnvioFoto(String nomeArquivo, Integer tamanho);
    public void aceitaEnvioFoto(Integer porta);
}
