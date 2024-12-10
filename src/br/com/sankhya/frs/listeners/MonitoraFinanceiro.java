package br.com.sankhya.frs.listeners;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.frs.DAO.FilaDAO;
import br.com.sankhya.frs.utils.BuildRequestFinanceiro;
import br.com.sankhya.frs.utils.BuildRequestPut;
import br.com.sankhya.frs.utils.FinanceiroBuilder;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;


public class MonitoraFinanceiro implements EventoProgramavelJava {

    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {
        JapeWrapper daoFin = JapeFactory.dao(DynamicEntityNames.FINANCEIRO);
        DynamicVO financeiroVO = (DynamicVO) event.getVo();

        if (Objects.equals(financeiroVO.asString("ORIGEM"), "F") && financeiroVO.asBigDecimal("NURENEG") != null) {
            /*String payload = BuildRequestFinanceiro.build(financeiroVO.asBigDecimal("NUFIN"));*/

            String payload = FinanceiroBuilder
                    .fromTGFFIN(financeiroVO)
                    .buildToJson();
            if (payload == null) return;

            FilaDAO.add(
                    financeiroVO.asBigDecimal("NUFIN"),
                    financeiroVO.asBigDecimal("CODEMP").toString(),
                    financeiroVO.asBigDecimal("CODPARC").toString(),
                    payload,
                    "POST");
            return;
        };
    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {
        DynamicVO financeiroVO = (DynamicVO) event.getVo();
        BigDecimal nufin = financeiroVO.asBigDecimal("NUFIN");
        BigDecimal recdesp = financeiroVO.asBigDecimal("RECDESP");

        try {
            FinanceiroBuilder.Builder builder = FinanceiroBuilder.fromTGFFINPut(nufin, recdesp);
            if (builder == null) return;

            String payload = builder.buildToJson();

            FilaDAO.add(
                    nufin,
                    financeiroVO.asBigDecimal("CODEMP").toString(),
                    financeiroVO.asBigDecimal("CODPARC").toString(),
                    payload,
                    "PUT"
            );
        } catch (Exception e) {
            throw new Exception("Erro ao processar financeiro: " + e.getMessage());
        }
    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext tranCtx) throws Exception {

    }
}
