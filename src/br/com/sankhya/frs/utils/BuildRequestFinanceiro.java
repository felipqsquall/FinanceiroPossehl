package br.com.sankhya.frs.utils;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import java.math.BigDecimal;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BuildRequestFinanceiro {
    public static String build(BigDecimal nuFin) throws Exception {

        JapeWrapper daoFinanceiro = JapeFactory.dao("AD_VGFINTSHU");
        DynamicVO financeiroVO = daoFinanceiro.findByPK(nuFin);

        String partner_reference_id = financeiroVO.asString("CODPARC");
        String document_number = financeiroVO.asString("DOCUMENT_NUMBER");
        String document_type = financeiroVO.asString("DOCUMENT_TYPE");
        String document_date = financeiroVO.asString("DOCUMENT_DATE");
        String current_amount = financeiroVO.asString("CURRENT_AMOUNT");
        String original_amount = financeiroVO.asString("ORIGINAL_AMOUNT");
        String net_due_date = financeiroVO.asString("NET_DUE_DATE");
        String discount_date = financeiroVO.asString("DISCOUNT_DATE");
        String balanced_at_date = financeiroVO.asString("BALANCED_AT_DATE");
        String current_amount_document_currency = financeiroVO.asString("CURRENT_AMOUNT_DOCUMENT_CURR");
        String original_amount_document_currency = financeiroVO.asString("ORIGINAL_AMOUNT_DOCUMENT_CURR");
        String current_dunning_level = financeiroVO.asString("CURRENT_DUNNING_LEVEL");
        String disputed = financeiroVO.asString("DISPUTED");
        String dunning_block = financeiroVO.asString("DUNNING_BLOCK");
        String document_currency = financeiroVO.asString("DOCUMENT_CURRENCY");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode accountingdocuments = mapper.createObjectNode();

        accountingdocuments.put("partner_reference_id", partner_reference_id);
        accountingdocuments.put("document_reference_id", nuFin);
        accountingdocuments.put("document_number", document_number);
        accountingdocuments.put("document_type", document_type.split(" ")[0]);
        accountingdocuments.put("document_date", document_date.split(" ")[0]);
        accountingdocuments.put("current_amount", current_amount);
        accountingdocuments.put("original_amount", original_amount);
        accountingdocuments.put("net_due_date", net_due_date.split(" ")[0]);

        if (discount_date != null) {
            accountingdocuments.put("discount_date", discount_date.split(" ")[0]);
        }
        if (balanced_at_date != null) {
            accountingdocuments.put("balanced_at_date", balanced_at_date.split(" ")[0]);
        }
        if (current_amount_document_currency != null) {
            accountingdocuments.put("current_amount_document_currency", current_amount_document_currency);
        }
        if (original_amount_document_currency != null) {
            accountingdocuments.put("original_amount_document_currency", original_amount_document_currency);
        }
        if (current_dunning_level != null) {
            accountingdocuments.put("current_dunning_level", current_dunning_level);
        }
        if (disputed != null) {
            accountingdocuments.put("disputed", disputed);
        }
        if (dunning_block != null) {
            accountingdocuments.put("dunning_block", dunning_block);
        }
        if (document_currency != null) {
            accountingdocuments.put("document_currency", document_currency);
        }

        ObjectNode additional_fields = mapper.createObjectNode();
        String additionalFields = financeiroVO.asString("ADDITIONAL_FIELDS");
        additional_fields.put("additional_field_1", additionalFields);
        accountingdocuments.set("additional_fields", additional_fields);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(accountingdocuments);
        return new JSONObject(json).toString();
    }
}
