package br.feevale;

import org.json.JSONObject;

/**
 * Interface que define os possíveis tratamentos e respostas às mensagens
 * @author diovani
 *
 */
public interface IMonitorChat {

    public void trataHandshake(JSONObject data);
    public void trataMensagem(JSONObject data);
    public void trataAtencao(JSONObject data);

    public void trataDesconexao(JSONObject data);

    public void trataSolicitacaoEnvioArquivo(JSONObject data);
    public void trataAceiteEnvioArquivo(JSONObject data);

    public void trataSolicitacaoEnvioFoto(JSONObject data);
    public void trataAceiteEnvioFoto(JSONObject data);

}
