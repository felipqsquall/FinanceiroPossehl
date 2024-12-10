package br.com.sankhya.frs.schedule;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.sankhya.frs.service.CremerAPIBkp;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.ws.ServiceContext;


public class ProcessaFila implements ScheduledAction {

    @Override
    public void onTime(ScheduledActionContext context) {
        Date dateobj = new Date();
        ServiceContext sc = new ServiceContext(null);
        sc.setAutentication(AuthenticationInfo.getCurrent());
        sc.makeCurrent();

        Map<String, String> retorno = null;
        JapeWrapper daoFinanceiro = JapeFactory.dao(DynamicEntityNames.FINANCEIRO);
        JapeWrapper daoIntegracao = JapeFactory.dao("AD_INTFINCRE");
        try {

            Collection<DynamicVO> integracoes = daoIntegracao.find(
                    "STATUS NOT IN ('C','E') AND NUFIN IN (SELECT NUFIN FROM TGFFIN WHERE NVL(AD_INTEGRADO1,'N') = 'N')"
            );
            int i = 1;
            BigDecimal id = null;
            String nuFin = null;
            String codEmp = null;
            String codParc = null;
            String request = null;
            String method = null;
            ObjectMapper objectMapper = new ObjectMapper();

            for (DynamicVO integracao : integracoes) {
                if (i > 10) {
                    return;
                }
                id = integracao.asBigDecimal("ID");
                nuFin = integracao.asBigDecimal("NUFIN").toString();
                codEmp = integracao.asBigDecimal("CODEMP").toString();
                codParc = integracao.asBigDecimal("CODPARC").toString();
                request = integracao.asString("REQUEST");
                method = integracao.asString("METHOD").toString();

                if ("POST".equalsIgnoreCase(method)) {
                    retorno = CremerAPIBkp.sendFinanceiro(request, codEmp, codParc, nuFin);
                } else if ("PUT".equalsIgnoreCase(method)) {
                    retorno = CremerAPIBkp.refreshFinanceiro(request, codEmp, codParc, nuFin);
                } else {
                    System.out.println("@@ERRO Método HTTP não suportado: " + method);
                }

                retorno = CremerAPIBkp.sendFinanceiro(request, codEmp, codParc, nuFin);
                JsonNode jsonNode = objectMapper.readTree(retorno.get("response"));
                if(jsonNode.has("code")) {
                    daoIntegracao.prepareToUpdateByPK(id)
                            .set("STATUS", "E")
                            .set("RESPONSE", retorno.get("response"))
                            .set("DTINTEGRACAO", new Timestamp(dateobj.getTime()))
                            .update();
                }
                else {
                    daoIntegracao.prepareToUpdateByPK(id)
                            .set("RESPONSE", retorno.get("response"))
                            .set("STATUS", "C")
                            .update();

                    daoFinanceiro.prepareToUpdateByPK(nuFin)
                            .set("AD_INTEGRADO1","S")
                            .update();
                }
                i++;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("@@ERRO ProcessaFila");
            e.printStackTrace();
        }
    }
}