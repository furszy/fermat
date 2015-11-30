package com.bitdubai.fermat_cbp_plugin.layer.business_transaction.open_contract.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationStatus;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation.Clause;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.open_contract.enums.ContractType;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.open_contract.exceptions.CantOpenContractException;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.open_contract.interfaces.AbstractOpenContract;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_sale.interfaces.CustomerBrokerContractSaleManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.exceptions.CantGetListSaleNegotiationsException;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.interfaces.CustomerBrokerSaleNegotiation;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.interfaces.CustomerBrokerSaleNegotiationManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation.exceptions.CantGetListClauseException;
import com.bitdubai.fermat_cbp_api.layer.network_service.TransactionTransmission.interfaces.TransactionTransmissionManager;
import com.bitdubai.fermat_cbp_plugin.layer.business_transaction.open_contract.developer.bitdubai.version_1.exceptions.CantGetNegotiationStatusException;

import java.util.Collection;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 27/11/15.
 */
public class OpenContractBrokerContractManager extends AbstractOpenContract {

    /**
     * Represents the sale contract
     */
    private CustomerBrokerContractSaleManager customerBrokerContractSaleManager;

    /**
     * Represents the sale negotiation
     */
    private CustomerBrokerSaleNegotiationManager customerBrokerSaleNegotiationManager;

    /**
     * Represents the negotiation ID.
     */
    //private String negotiationId;

    /**
     * Represents the transaction transmission manager
     */
    private TransactionTransmissionManager transactionTransmissionManager;

    public OpenContractBrokerContractManager(CustomerBrokerContractSaleManager customerBrokerContractSaleManager,
                                          CustomerBrokerSaleNegotiationManager customerBrokerSaleNegotiationManager,
                                          TransactionTransmissionManager transactionTransmissionManager){
        this.customerBrokerContractSaleManager=customerBrokerContractSaleManager;
        this.customerBrokerSaleNegotiationManager=customerBrokerSaleNegotiationManager;
        this.transactionTransmissionManager=transactionTransmissionManager;

    }

    private CustomerBrokerSaleNegotiation findSaleNegotiation(String negotiationId) throws CantGetNegotiationStatusException {

        try{
            Collection<CustomerBrokerSaleNegotiation> negotiationCollection= customerBrokerSaleNegotiationManager.getNegotiations(NegotiationStatus.CLOSED);
            for(CustomerBrokerSaleNegotiation customerBrokerPurchaseNegotiation : negotiationCollection){
                String negotiationUUID=customerBrokerPurchaseNegotiation.getNegotiationId().toString();
                if(negotiationId.equals(negotiationUUID)){
                    return customerBrokerPurchaseNegotiation;
                }
            }
            throw new CantGetNegotiationStatusException("Cannot find the Negotiation Id \n"+
                    negotiationId+"\n" +
                    "in the Purchase Negotiation Database in CLOSED status");
        } catch (CantGetListSaleNegotiationsException exception) {
            throw new CantGetNegotiationStatusException(exception,
                    "Checking if Negotiation is closed",
                    "Cannot get the Purchase Negotiation list");
        }

    }

    @Override
    public void openContract(String negotiationId) throws CantOpenContractException {

        contractType= ContractType.SALE;
        try{
            CustomerBrokerSaleNegotiation customerBrokerSaleNegotiation= findSaleNegotiation(negotiationId);
            Collection<Clause> negotiationClauses=customerBrokerSaleNegotiation.getClauses();
        } catch (CantGetListClauseException exception) {
            throw new CantOpenContractException(exception,
                    "Opening a new contract",
                    "Cannot get the negotiation clauses list");
        } catch(CantGetNegotiationStatusException exception){
            throw new CantOpenContractException(exception,
                    "Opening a new contract",
                    "Cannot get the negotiation status");
        }

    }
}
