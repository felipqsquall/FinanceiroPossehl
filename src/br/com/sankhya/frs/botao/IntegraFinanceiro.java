package br.com.sankhya.frs.botao;

import java.math.BigDecimal;
import java.util.Map;

import br.com.sankhya.frs.service.CremerAPIBkp;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;

public class IntegraFinanceiro implements AcaoRotinaJava {

    public void doAction(ContextoAcao context) throws Exception {
        Registro[] registros = context.getLinhas();
        JapeWrapper vwTgffin = JapeFactory.dao("Financeiro");
        DynamicVO vwTgffinVO = null;
        Map<String, String> retorno = null;
        for (int i = 0; i < registros.length; i++) {
            Registro linha = registros[i];
            vwTgffinVO = vwTgffin.findByPK(linha.getCampo("NUFIN"));
            String integrado1 = (vwTgffinVO.asString("AD_INTEGRADO1") == null) ? new String("N")
                    : vwTgffinVO.asString("AD_INTEGRADO1");
			/*String integrado2 = (vwTgffinVO.asString("AD_INTEGRADO2") == null) ? new String("N")
					: vwTgffinVO.asString("AD_INTEGRADO2");*/
            if (linha.getCampo("CODEMP").equals(new BigDecimal("1")) && integrado1.equals(new String("N"))) {
                retorno = CremerAPIBkp.sendFinanceiro(linha.getCampo("REQUEST").toString(),
                        ((BigDecimal) linha.getCampo("CODEMP")).toString(),
                        ((BigDecimal) linha.getCampo("CODPARC")).toString(),
                        ((BigDecimal) linha.getCampo("NUFIN")).toString());
                linha.setCampo("RESPONSE", retorno);
                if(Integer.parseInt(retorno.get("status")) > 299) {
                    linha.setCampo("STATUS", new String("E"));
                }
                linha.setCampo("STATUS", new String("C"));
                JapeWrapper financeiroDAO = JapeFactory.dao("Financeiro");
                ((FluidUpdateVO) financeiroDAO.prepareToUpdateByPK(new Object[] { linha.getCampo("NUFIN") })
                        .set("AD_INTEGRADO1", new String("S"))).update();
                context.setMensagemRetorno("Financeiro Integrado!");
            }
            if (linha.getCampo("CODEMP").equals(new BigDecimal("1")) && integrado1.equals(new String("S"))) {
                retorno = CremerAPIBkp.refreshFinanceiro(linha.getCampo("REQUEST").toString(),
                        ((BigDecimal) linha.getCampo("CODEMP")).toString(),
                        ((BigDecimal) linha.getCampo("CODPARC")).toString(),
                        ((BigDecimal) linha.getCampo("NUFIN")).toString());
                linha.setCampo("RESPONSE", retorno);
                if(Integer.parseInt(retorno.get("status")) > 299) {
                    linha.setCampo("STATUS", new String("E"));
                }
                linha.setCampo("STATUS", new String("C"));
                JapeWrapper financeiroDAO = JapeFactory.dao("Financeiro");
                ((FluidUpdateVO) financeiroDAO.prepareToUpdateByPK(new Object[] { linha.getCampo("NUFIN") })
                        .set("AD_INTEGRADO1", new String("S"))).update();
                context.setMensagemRetorno("Financeiro Atualizado!");
            }
            if (linha.getCampo("CODEMP").equals(new BigDecimal("200"))/* && integrado2.equals(new String("N"))*/) {
                retorno = CremerAPIBkp.sendFinanceiro(linha.getCampo("REQUEST").toString(),
                        ((BigDecimal) linha.getCampo("CODEMP")).toString(),
                        ((BigDecimal) linha.getCampo("CODPARC")).toString(),
                        ((BigDecimal) linha.getCampo("NUFIN")).toString());
                linha.setCampo("RESPONSE", retorno);
                if(Integer.parseInt(retorno.get("status")) > 299) {
                    linha.setCampo("STATUS", new String("E"));
                }
                linha.setCampo("STATUS", new String("C"));
                JapeWrapper financeiroDAO = JapeFactory.dao("Financeiro");
                ((FluidUpdateVO) financeiroDAO.prepareToUpdateByPK(linha.getCampo("NUFIN"))
                        .set("AD_INTEGRADO1", new String("S"))).update();
                //.set("AD_INTEGRADO2", new String("S"))).update();
                context.setMensagemRetorno("Financeiro Integrado!");
            }
            if (linha.getCampo("CODEMP").equals(new BigDecimal("200"))/* && integrado2.equals(new String("S"))*/) {
                retorno = CremerAPIBkp.refreshFinanceiro(linha.getCampo("REQUEST").toString(),
                        ((BigDecimal) linha.getCampo("CODEMP")).toString(),
                        ((BigDecimal) linha.getCampo("CODPARC")).toString(),
                        ((BigDecimal) linha.getCampo("NUFIN")).toString());
                linha.setCampo("RESPONSE", retorno);
                if(Integer.parseInt(retorno.get("status")) > 299) {
                    linha.setCampo("STATUS", new String("E"));
                }
                linha.setCampo("STATUS", new String("C"));
                JapeWrapper financeiroDAO = JapeFactory.dao("Financeiro");
                ((FluidUpdateVO) financeiroDAO.prepareToUpdateByPK(linha.getCampo("NUFIN"))
                        .set("AD_INTEGRADO1", new String("S"))).update();
                //.set("AD_INTEGRADO2", new String("S"))).update();
                context.setMensagemRetorno("Financeiro Atualizado!");
            }
        }
    }
}