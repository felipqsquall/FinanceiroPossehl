package br.com.sankhya.frs.botao;

import br.com.sankhya.frs.DAO.FilaDAO;
import br.com.sankhya.frs.utils.BuildRequestFinanceiro;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import java.math.BigDecimal;
import java.util.Collection;


public class AdicionaFinFila implements AcaoRotinaJava {

    public void doAction(ContextoAcao context) throws Exception {
        EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        Registro[] registros = context.getLinhas();
        BigDecimal nuFin = null;
        String codEmp = "";
        String codParc = "";
        String method = "";
        for (int i = 0; i < registros.length; i++) {
            Registro linha = registros[i];
            String queryPk = "CODEMP=" + new BigDecimal((String) context.getParam("CODEMP"));
            FinderWrapper f = new FinderWrapper("AD_CFGCRE", queryPk);
            nuFin = (BigDecimal) linha.getCampo("NUFIN");
            codEmp = (String) context.getParam("CODEMP");
            codParc = (String) linha.getCampo("CODPARC");
            method = (String) linha.getCampo("METHOD");
            @SuppressWarnings("unchecked")
            Collection<PersistentLocalEntity> rPLES = dwf.findByDynamicFinder(f);
            for (PersistentLocalEntity rPLE : rPLES) {
                EntityVO rEVO = rPLE.getValueObject();
                @SuppressWarnings("unused")
                DynamicVO rVO = (DynamicVO) rEVO;
                String payload = BuildRequestFinanceiro.build((BigDecimal) linha.getCampo("NUFIN"));
                FilaDAO.add(nuFin, codEmp, codParc, payload, method);
                context.setMensagemRetorno("Financeiro Integrado!");
            }
            if (rPLES.size() == 0)
                context.setMensagemRetorno("Empresa não existe na integração");
        }
    }
}