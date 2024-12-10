package br.com.sankhya.frs.DAO;


import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class ConfiguracaoDAO {
    public static DynamicVO get(String empresa) throws Exception {
        String codEmp = empresa;
        JapeWrapper dao = JapeFactory.dao("AD_CFGINTFIN");
        DynamicVO configVO = dao.findOne("CODEMP = ?", codEmp);
        if (configVO == null) {
            throw new NullPointerException("@@CONFIGURACAODAO " + " Nenhuma configuração encontrada para a empresa: " + codEmp);
        }
        return configVO;
    }

}