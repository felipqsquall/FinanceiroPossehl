package br.com.sankhya.frs.utils;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FinanceiroBuilder {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FinancialDocument {
        @JsonProperty("partner_reference_id")
        private String partnerReferenceId;

        @JsonProperty("document_reference_id")
        private BigDecimal documentReferenceId;

        @JsonProperty("document_number")
        private String documentNumber;

        @JsonProperty("document_type")
        private String documentType;

        @JsonProperty("document_date")
        private String documentDate;

        @JsonProperty("current_amount")
        private String currentAmount;

        @JsonProperty("original_amount")
        private String originalAmount;

        @JsonProperty("net_due_date")
        private String netDueDate;

        @JsonProperty("discount_date")
        private String discountDate;

        @JsonProperty("balanced_at_date")
        private String balancedAtDate;

        @JsonProperty("current_amount_document_currency")
        private String currentAmountDocumentCurrency;

        @JsonProperty("original_amount_document_currency")
        private String originalAmountDocumentCurrency;

        @JsonProperty("current_dunning_level")
        private String currentDunningLevel;

        @JsonProperty("disputed")
        private String disputed;

        @JsonProperty("dunning_block")
        private String dunningBlock;

        @JsonProperty("document_currency")
        private String documentCurrency;

        @JsonProperty("additional_fields")
        private Map<String, String> additionalFields;

        private FinancialDocument() {
            this.additionalFields = new HashMap<>();
        }

        public String getPartnerReferenceId() {
            return partnerReferenceId;
        }

        public BigDecimal getDocumentReferenceId() {
            return documentReferenceId;
        }

        public String getDocumentNumber() {
            return documentNumber;
        }

        public String getDocumentType() {
            return documentType;
        }

        public String getDocumentDate() {
            return documentDate;
        }

        public String getCurrentAmount() {
            return currentAmount;
        }

        public String getOriginalAmount() {
            return originalAmount;
        }

        public String getNetDueDate() {
            return netDueDate;
        }

        public String getDiscountDate() {
            return discountDate;
        }

        public String getBalancedAtDate() {
            return balancedAtDate;
        }

        public String getCurrentAmountDocumentCurrency() {
            return currentAmountDocumentCurrency;
        }

        public String getOriginalAmountDocumentCurrency() {
            return originalAmountDocumentCurrency;
        }

        public String getCurrentDunningLevel() {
            return currentDunningLevel;
        }

        public String getDisputed() {
            return disputed;
        }

        public String getDunningBlock() {
            return dunningBlock;
        }

        public String getDocumentCurrency() {
            return documentCurrency;
        }

        public Map<String, String> getAdditionalFields() {
            return additionalFields;
        }
    }

    public static class Builder {
        private FinancialDocument document;

        public Builder() {
            this.document = new FinancialDocument();
        }

        public Builder documentReferenceId(BigDecimal documentReferenceId) {
            document.documentReferenceId = documentReferenceId;
            return this;
        }

        public Builder partnerReferenceId(String partnerReferenceId) {
            document.partnerReferenceId = partnerReferenceId;
            return this;
        }

        public Builder documentNumber(String documentNumber) {
            document.documentNumber = documentNumber;
            return this;
        }

        public Builder documentType(String documentType) {
            document.documentType = documentType;
            return this;
        }

        public Builder documentDate(String documentDate) {
            document.documentDate = documentDate;
            return this;
        }

        public Builder currentAmount(String currentAmount) {
            document.currentAmount = currentAmount;
            return this;
        }

        public Builder originalAmount(String originalAmount) {
            document.originalAmount = originalAmount;
            return this;
        }

        public Builder netDueDate(String netDueDate) {
            document.netDueDate = netDueDate;
            return this;
        }

        public Builder discountDate(String discountDate) {
            document.discountDate = discountDate;
            return this;
        }

        public Builder balancedAtDate(String balancedAtDate) {
            document.balancedAtDate = balancedAtDate;
            return this;
        }

        public Builder currentAmountDocumentCurrency(String currentAmountDocumentCurrency) {
            document.currentAmountDocumentCurrency = currentAmountDocumentCurrency;
            return this;
        }

        public Builder originalAmountDocumentCurrency(String originalAmountDocumentCurrency) {
            document.originalAmountDocumentCurrency = originalAmountDocumentCurrency;
            return this;
        }

        public Builder currentDunningLevel(String currentDunningLevel) {
            document.currentDunningLevel = currentDunningLevel;
            return this;
        }

        public Builder disputed(String disputed) {
            document.disputed = disputed;
            return this;
        }

        public Builder dunningBlock(String dunningBlock) {
            document.dunningBlock = dunningBlock;
            return this;
        }

        public Builder documentCurrency(String documentCurrency) {
            document.documentCurrency = documentCurrency;
            return this;
        }

        public Builder additionalField(String key, String value) {
            document.additionalFields.put(key, value);
            return this;
        }

        public FinancialDocument build() {
            return document;
        }

        public String buildToJson() throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(document);
        }
    }

    public static Builder fromTGFFIN(DynamicVO financeiroVO) throws Exception {
        if (financeiroVO == null) {
            throw new IllegalArgumentException("DynamicVO não pode ser nulo");
        }

        JapeWrapper daoFinanceiro = JapeFactory.dao(DynamicEntityNames.FINANCEIRO);
        DynamicVO fullFinanceiroVO = daoFinanceiro.findByPK(financeiroVO.asBigDecimal("NUFIN"));

        if (fullFinanceiroVO == null) {
            throw new IllegalArgumentException("Não foi possível encontrar o registro financeiro com NUFIN: " + financeiroVO.asBigDecimal("NUFIN"));
        }

        return new Builder()
                .documentReferenceId(fullFinanceiroVO.asBigDecimal("NUFIN"))
                .documentNumber(buildDocumentNumber(fullFinanceiroVO))
                .documentType(determineDocumentType(fullFinanceiroVO))
                .documentDate(formatDate(fullFinanceiroVO.asTimestamp("DTNEG")))
                .currentAmount(calculateCurrentAmount(fullFinanceiroVO))
                .originalAmount(getSafeBigDecimalString(fullFinanceiroVO, "VLRDESDOB"))
                .netDueDate(formatDate(fullFinanceiroVO.asTimestamp("DTVENC")))
                .discountDate(formatDate(fullFinanceiroVO.asTimestamp("DHBAIXA")))
                .balancedAtDate(formatDate(fullFinanceiroVO.asTimestamp("DHBAIXA")))
                .currentAmountDocumentCurrency(getSafeBigDecimalString(fullFinanceiroVO, "VLRDESDOB"))
                .originalAmountDocumentCurrency(getSafeBigDecimalString(fullFinanceiroVO, "VLRDESDOB"))
                .currentDunningLevel("0")
                .disputed("FALSE")
                .dunningBlock("")
                .documentCurrency("BRL")
                .partnerReferenceId(getSafeBigDecimalString(fullFinanceiroVO, "CODPARC"))
                .additionalField("HISTORICO", getSafeString(fullFinanceiroVO, "HISTORICO"));
    }

    public static Builder fromNUFIN(BigDecimal nuFin) throws Exception {
        if (nuFin == null) {
            throw new IllegalArgumentException("NUFIN não pode ser nulo");
        }

        JapeWrapper daoFinanceiro = JapeFactory.dao(DynamicEntityNames.FINANCEIRO);
        DynamicVO financeiroVO = daoFinanceiro.findByPK(nuFin);

        if (financeiroVO == null) {
            throw new IllegalArgumentException("Não foi possível encontrar o registro financeiro com NUFIN: " + nuFin);
        }

        return fromTGFFIN(financeiroVO);
    }

    public static Builder fromTGFFINPut(BigDecimal nuFin, BigDecimal recDesp) throws Exception {
        if (nuFin == null) {
            throw new IllegalArgumentException("NUFIN não pode ser nulo");
        }

        JapeWrapper daoFinanceiro = JapeFactory.dao(DynamicEntityNames.FINANCEIRO);
        DynamicVO financeiroVO = daoFinanceiro.findByPK(nuFin);

        if (financeiroVO == null) {
            throw new IllegalArgumentException("Não foi possível encontrar o registro financeiro com NUFIN: " + nuFin);
        }

        // Verifica se o registro atende aos critérios básicos
        if (!"N".equals(financeiroVO.asString("PROVISAO"))) {
            return null;
        }

        // Verifica se existe na tabela AD_INTFINCRE com STATUS = 'C'
        JapeWrapper daoIntFincre = JapeFactory.dao("AD_INTFINCRE");
        DynamicVO intfinVO = daoIntFincre.findOne("NUFIN = ? AND STATUS = 'C'", nuFin);
        if (intfinVO == null) {
            return null;
        }

        Builder builder = new Builder()
                .documentReferenceId(financeiroVO.asBigDecimal("NUFIN"))
                .documentNumber(buildDocumentNumber(financeiroVO))
                .documentType(determineDocumentTypePut(financeiroVO, recDesp))
                .documentDate(formatDate(financeiroVO.asTimestamp("DTNEG")))
                .currentAmount(calculateCurrentAmount(financeiroVO))
                .originalAmount(getSafeBigDecimalString(financeiroVO, "VLRDESDOB"))
                .netDueDate(formatDate(financeiroVO.asTimestamp("DTVENC")))
                .discountDate(formatDate(financeiroVO.asTimestamp("DHBAIXA")))
                .balancedAtDate(formatDate(financeiroVO.asTimestamp("DHBAIXA")))
                .currentAmountDocumentCurrency(getSafeBigDecimalString(financeiroVO, "VLRDESDOB"))
                .originalAmountDocumentCurrency(getSafeBigDecimalString(financeiroVO, "VLRDESDOB"))
                .currentDunningLevel("0")
                .disputed("FALSE")
                .dunningBlock("")
                .documentCurrency("BRL")
                .partnerReferenceId(getSafeBigDecimalString(financeiroVO, "CODPARC"))
                .additionalField("HISTORICO", getSafeString(financeiroVO, "HISTORICO"));

        return validadeFinanceiroPut(financeiroVO, recDesp) ? builder : null;
    }

    private static String buildDocumentNumber(DynamicVO financeiroVO) {
        String baseNumber = "";
        String desdobramentoStr = "1";

        try {
            Object nunota = financeiroVO.getProperty("NUNOTA");
            Object numnota = financeiroVO.getProperty("NUMNOTA");
            Object desdobramento = financeiroVO.getProperty("DESDOBRAMENTO");

            if (nunota != null) {
                baseNumber = nunota.toString();
            } else if (numnota != null) {
                baseNumber = numnota.toString();
            }

            if (desdobramento != null) {
                desdobramentoStr = desdobramento.toString();
            }
        } catch (Exception e) {
            return "0-1";
        }

        return baseNumber + "-" + desdobramentoStr;
    }

    private static String determineDocumentType(DynamicVO financeiroVO) {
        try {
            Object recdesp = financeiroVO.getProperty("RECDESP");
            Object nureneg = financeiroVO.getProperty("NURENEG");

            if (recdesp != null && recdesp.toString().equals("1") &&
                    (nureneg == null || nureneg.toString().isEmpty())) {
                return "RE";
            }
        } catch (Exception e) {
            // Em caso de erro, retorna vazio
        }
        return "";
    }

    private static String determineDocumentTypePut(DynamicVO financeiroVO, BigDecimal recDesp) {
        try {
            BigDecimal nureneg = financeiroVO.asBigDecimal("NURENEG");

            // Caso de renegociação (RECDESP = 0)
            if (BigDecimal.ZERO.equals(recDesp) && nureneg != null) {
                return "SO";
            }

            // Caso de baixa (RECDESP = 1)
            if (BigDecimal.ONE.equals(recDesp)) {
                return "RE";
            }
        } catch (Exception e) {
            // Em caso de erro, retorna vazio
        }
        return "";
    }

    private static boolean validadeFinanceiroPut(DynamicVO financeiroVO, BigDecimal recDesp) throws Exception {
        try {
            // Validações comuns
            if (!"N".equals(financeiroVO.asString("PROVISAO"))) {
                return false;
            }

            // Caso renegociação (RECDESP = 0)
            if (BigDecimal.ZERO.equals(recDesp)) {
                return financeiroVO.asBigDecimal("NURENEG") != null;
            }

            // Caso baixa (RECDESP = 1)
            if (BigDecimal.ONE.equals(recDesp)) {
                return financeiroVO.asTimestamp("DHBAIXA") != null;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static String calculateCurrentAmount(DynamicVO financeiroVO) {
        try {
            BigDecimal vlrdesdob = financeiroVO.asBigDecimal("VLRDESDOB");
            BigDecimal vlrbaixa = financeiroVO.asBigDecimal("VLRBAIXA");
            BigDecimal vlrjuro = financeiroVO.asBigDecimal("VLRJURO");

            if (vlrdesdob == null) vlrdesdob = BigDecimal.ZERO;
            if (vlrbaixa == null) vlrbaixa = BigDecimal.ZERO;
            if (vlrjuro == null) vlrjuro = BigDecimal.ZERO;

            Timestamp dhbaixa = financeiroVO.asTimestamp("DHBAIXA");
            Timestamp dtvenc = financeiroVO.asTimestamp("DTVENC");

            BigDecimal currentAmount;
            if (dhbaixa != null && dtvenc != null && dhbaixa.after(dtvenc)) {
                currentAmount = vlrdesdob.add(vlrjuro).subtract(vlrbaixa);
            } else {
                currentAmount = vlrdesdob.subtract(vlrbaixa);
            }

            return currentAmount.compareTo(BigDecimal.ZERO) < 0 ? "0" : currentAmount.toString();
        } catch (Exception e) {
            return "0";
        }
    }

    private static String formatDate(Timestamp timestamp) {
        if (timestamp == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd").format(timestamp);
    }

    private static String getSafeString(DynamicVO vo, String field) {
        try {
            return Optional.ofNullable(vo.getProperty(field))
                    .map(Object::toString)
                    .orElse("");
        } catch (Exception e) {
            return "";
        }
    }

    private static String getSafeBigDecimalString(DynamicVO vo, String field) {
        try {
            BigDecimal value = vo.asBigDecimal(field);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }
}