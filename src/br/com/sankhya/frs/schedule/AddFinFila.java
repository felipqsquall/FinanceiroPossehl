package br.com.sankhya.frs.schedule;

import java.math.BigDecimal;
import java.util.Collection;

import br.com.sankhya.frs.utils.FinanceiroBuilder;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;
import br.com.sankhya.frs.DAO.FilaDAO;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;


public class AddFinFila implements ScheduledAction {

    @Override
    public void onTime(ScheduledActionContext context) {

        JapeWrapper daoFinanceiro = JapeFactory.dao("AD_VGFINTSHU");
        try {
            Collection<DynamicVO> financeiros = daoFinanceiro.find(
                    "NUFIN IN (SELECT NUFIN FROM TGFFIN WHERE NVL(AD_INTEGRADO1,'N') = 'N') AND CODEMP IN (1,200) AND NUFIN NOT IN (SELECT NUFIN FROM AD_INTFINCRE WHERE METHOD = 'POST')"
            );

            int i = 1;
            BigDecimal nuFin = null;
            String codEmp = "";
            String codParc = "";
            String method = "";
            for (DynamicVO financeiro : financeiros) {
                if (i > 10) {
                    return;
                }
                nuFin = financeiro.asBigDecimal("NUFIN");
                codEmp = financeiro.asString("CODEMP");
                codParc = financeiro.asString("CODPARC");
                method = financeiro.asString("METHOD");
               // String payload = BuildRequestFinanceiro.build(nuFin);
                String payload = FinanceiroBuilder
                        .fromNUFIN(nuFin)
                        .buildToJson();
                FilaDAO.add(nuFin, codEmp, codParc, payload, method);
                i++;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("@@ERRO AddFinFila");
            e.printStackTrace();
        }
    }
}
