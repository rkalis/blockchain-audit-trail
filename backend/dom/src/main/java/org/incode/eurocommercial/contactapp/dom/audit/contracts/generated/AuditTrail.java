package org.incode.eurocommercial.contactapp.dom.audit.contracts.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.3.1.
 */
public class AuditTrail extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b50336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506106df806100606000396000f300608060405260043610610062576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806341c0e1b5146100675780638da5cb5b1461007e578063d95a5f16146100d5578063e253b279146101e7575b600080fd5b34801561007357600080fd5b5061007c61023a565b005b34801561008a57600080fd5b506100936102cf565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156100e157600080fd5b50610100600480360381019080803590602001909291905050506102f4565b604051808060200180602001838103835285818151815260200191508051906020019080838360005b83811015610144578082015181840152602081019050610129565b50505050905090810190601f1680156101715780820380516001836020036101000a031916815260200191505b50838103825284818151815260200191508051906020019080838360005b838110156101aa57808201518184015260208101905061018f565b50505050905090810190601f1680156101d75780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b3480156101f357600080fd5b50610238600480360381019080803590602001908201803590602001919091929391929390803590602001908201803590602001919091929391929390505050610457565b005b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561029557600080fd5b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16ff5b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60018181548110151561030357fe5b9060005260206000209060020201600091509050806000018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103af5780601f10610384576101008083540402835291602001916103af565b820191906000526020600020905b81548152906001019060200180831161039257829003601f168201915b505050505090806001018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561044d5780601f106104225761010080835404028352916020019161044d565b820191906000526020600020905b81548152906001019060200180831161043057829003601f168201915b5050505050905082565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156104b257600080fd5b6001604080519081016040528086868080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050815260200184848080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505081525090806001815401808255809150509060018203906000526020600020906002020160009091929091909150600082015181600001908051906020019061057492919061060e565b50602082015181600101908051906020019061059192919061060e565b505050507ff0e260a9322a2a85a7b5af2a6a6a253dec9aee7c03d6a134fd4a1f7590b710ec848484846040518080602001806020018381038352878782818152602001925080828437820191505083810382528585828181526020019250808284378201915050965050505050505060405180910390a150505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061064f57805160ff191683800117855561067d565b8280016001018555821561067d579182015b8281111561067c578251825591602001919060010190610661565b5b50905061068a919061068e565b5090565b6106b091905b808211156106ac576000816000905550600101610694565b5090565b905600a165627a7a72305820c8dea9e3597614a1de24c510bb5fe52fe51f72af42c783ae05ae86b02f10a27c0029";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
    }

    protected AuditTrail(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected AuditTrail(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<AuditEventResponse> getAuditEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Audit", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<AuditEventResponse> responses = new ArrayList<AuditEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AuditEventResponse typedResponse = new AuditEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.preValue = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.postValue = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<AuditEventResponse> auditEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Audit", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, AuditEventResponse>() {
            @Override
            public AuditEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                AuditEventResponse typedResponse = new AuditEventResponse();
                typedResponse.log = log;
                typedResponse.preValue = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.postValue = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<String> owner() {
        final Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Tuple2<String, String>> auditEntries(BigInteger param0) {
        final Function function = new Function("auditEntries", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteCall<Tuple2<String, String>>(
                new Callable<Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, String>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue());
                    }
                });
    }

    public static RemoteCall<AuditTrail> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(AuditTrail.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<AuditTrail> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(AuditTrail.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public RemoteCall<TransactionReceipt> kill() {
        final Function function = new Function(
                "kill", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> audit(String preValue, String postValue) {
        final Function function = new Function(
                "audit", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(preValue), 
                new org.web3j.abi.datatypes.Utf8String(postValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static AuditTrail load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new AuditTrail(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static AuditTrail load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new AuditTrail(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class AuditEventResponse {
        public Log log;

        public String preValue;

        public String postValue;
    }
}
